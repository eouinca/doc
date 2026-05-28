package moe.doc.manager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import moe.doc.manager.shell.ShellBinderRequestHandler

class docReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if ("rikka.doc.intent.action.REQUEST_BINDER" == intent.action) {
            ShellBinderRequestHandler.handleRequest(context, intent)
        }
    }
}
