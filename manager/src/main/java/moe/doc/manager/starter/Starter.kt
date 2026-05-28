package moe.doc.manager.starter

import moe.doc.manager.application
import java.io.File

object Starter {

    private val starterFile = File(application.applicationInfo.nativeLibraryDir, "libdoc.so")

    val userCommand: String = starterFile.absolutePath

    val adbCommand = "adb shell $userCommand"

    val internalCommand = "$userCommand --apk=${application.applicationInfo.sourceDir}"
}
