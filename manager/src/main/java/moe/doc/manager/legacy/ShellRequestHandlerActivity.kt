package moe.doc.manager.legacy

import android.os.Bundle
import android.widget.Toast
import moe.doc.manager.app.AppActivity
import moe.doc.manager.shell.ShellBinderRequestHandler

class ShellRequestHandlerActivity : AppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ShellBinderRequestHandler.handleRequest(this, intent)
        finish()
    }
}
