package moe.doc.manager

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.topjohnwu.superuser.Shell
import moe.doc.manager.ktx.logd
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.core.util.BuildUtils.atLeast30
import rikka.material.app.LocaleDelegate

lateinit var application: docApplication

class docApplication : Application() {

    companion object {

        init {
            logd("docApplication", "init")

            Shell.setDefaultBuilder(Shell.Builder.create().setFlags(Shell.FLAG_REDIRECT_STDERR))
            if (Build.VERSION.SDK_INT >= 28) {
                HiddenApiBypass.setHiddenApiExemptions("")
            }
            if (atLeast30) {
                System.loadLibrary("adb")
            }
        }
    }

    private fun init(context: Context?) {
        docSettings.initialize(context)
        LocaleDelegate.defaultLocale = docSettings.getLocale()
        AppCompatDelegate.setDefaultNightMode(docSettings.getNightMode())
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        init(this)
    }

}
