package moe.doc.manager

import android.os.Bundle
import androidx.core.os.bundleOf
import moe.doc.api.BinderContainer
import moe.doc.manager.utils.Logger.LOGGER
import rikka.doc.doc
import rikka.doc.docApiConstants.USER_SERVICE_ARG_TOKEN
import rikka.doc.docProvider
import rikka.doc.server.ktx.workerHandler
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class docManagerProvider : docProvider() {

    companion object {
        private const val EXTRA_BINDER = "moe.doc.privileged.api.intent.extra.BINDER"
        private const val METHOD_SEND_USER_SERVICE = "sendUserService"
    }

    override fun onCreate(): Boolean {
        disableAutomaticSuiInitialization()
        return super.onCreate()
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        if (extras == null) return null

        return if (method == METHOD_SEND_USER_SERVICE) {
            try {
                extras.classLoader = BinderContainer::class.java.classLoader

                val token = extras.getString(USER_SERVICE_ARG_TOKEN) ?: return null
                val binder = extras.getParcelable<BinderContainer>(EXTRA_BINDER)?.binder ?: return null

                val countDownLatch = CountDownLatch(1)
                var reply: Bundle? = Bundle()

                val listener = object : doc.OnBinderReceivedListener {

                    override fun onBinderReceived() {
                        try {
                            doc.attachUserService(binder, bundleOf(
                                USER_SERVICE_ARG_TOKEN to token
                            ))
                            reply!!.putParcelable(EXTRA_BINDER, BinderContainer(doc.getBinder()))
                        } catch (e: Throwable) {
                            LOGGER.e(e, "attachUserService $token")
                            reply = null
                        }

                        doc.removeBinderReceivedListener(this)

                        countDownLatch.countDown()
                    }
                }

                doc.addBinderReceivedListenerSticky(listener, workerHandler)

                return try {
                    countDownLatch.await(5, TimeUnit.SECONDS)
                    reply
                } catch (e: TimeoutException) {
                    LOGGER.e(e, "Binder not received in 5s")
                    null
                }
            } catch (e: Throwable) {
                LOGGER.e(e, "sendUserService")
                null
            }
        } else {
            super.call(method, arg, extras)
        }
    }
}
