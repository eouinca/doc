package moe.doc.manager.shell

import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Parcel
import moe.doc.manager.utils.Logger.LOGGER
import rikka.doc.doc

object ShellBinderRequestHandler {

    fun handleRequest(context: Context, intent: Intent): Boolean {
        if (intent.action != "rikka.doc.intent.action.REQUEST_BINDER") {
            return false
        }

        val binder = intent.getBundleExtra("data")?.getBinder("binder") ?: return false
        val docBinder = doc.getBinder()
        if (docBinder == null) {
            LOGGER.w("Binder not received or doc service not running")
        }

        val data = Parcel.obtain()
        return try {
            data.writeStrongBinder(docBinder)
            data.writeString(context.applicationInfo.sourceDir)
            binder.transact(1, data, null, IBinder.FLAG_ONEWAY)
            true
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        } finally {
            data.recycle()
        }
    }
}
