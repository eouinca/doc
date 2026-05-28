package moe.doc.manager.authorization

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Parcel
import moe.doc.manager.BuildConfig
import moe.doc.manager.Manifest
import moe.doc.manager.utils.Logger.LOGGER
import moe.doc.manager.utils.docSystemApis
import rikka.doc.server.ServerConstants
import rikka.parcelablelist.ParcelableListSlice
import rikka.doc.doc
import java.util.*

object AuthorizationManager {

    private const val FLAG_ALLOWED = 1 shl 1
    private const val FLAG_DENIED = 1 shl 2
    private const val MASK_PERMISSION = FLAG_ALLOWED or FLAG_DENIED

    private fun getApplications(userId: Int): List<PackageInfo> {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            data.writeInterfaceToken("moe.doc.server.IdocService")
            data.writeInt(userId)
            try {
                doc.getBinder()!!.transact(ServerConstants.BINDER_TRANSACTION_getApplications, data, reply, 0)
            } catch (e: Throwable) {
                throw RuntimeException(e)
            }
            reply.readException()
            @Suppress("UNCHECKED_CAST")
            (ParcelableListSlice.CREATOR.createFromParcel(reply) as ParcelableListSlice<PackageInfo>).list!!
        } finally {
            reply.recycle()
            data.recycle()
        }
    }

    fun getPackages(): List<PackageInfo> {
        val packages: MutableList<PackageInfo> = ArrayList()
        if (doc.isPreV11() || (doc.getVersion() == 11 && doc.getServerPatchVersion() < 3)) {
            val allPackages: MutableList<PackageInfo> = ArrayList()
            for (user in docSystemApis.getUsers(useCache = false)) {
                try {
                    allPackages.addAll(docSystemApis.getInstalledPackages((PackageManager.GET_META_DATA or PackageManager.GET_PERMISSIONS).toLong(), user.id))
                } catch (e: Throwable) {
                    LOGGER.w(e, "getInstalledPackages")
                }
            }
            for (pi in allPackages) {
                if (BuildConfig.APPLICATION_ID == pi.packageName) continue
                if (pi.applicationInfo?.metaData?.getBoolean("moe.doc.client.V3_SUPPORT") != true) continue
                if (pi.requestedPermissions?.contains(Manifest.permission.API_V23) != true) continue

                packages.add(pi)
            }
        } else {
            packages.addAll(getApplications(-1))
        }
        return packages
    }

    fun granted(packageName: String, uid: Int): Boolean {
        return if (doc.isPreV11()) {
            docSystemApis.checkPermission(Manifest.permission.API_V23, packageName, uid / 100000) == PackageManager.PERMISSION_GRANTED
        } else {
            (doc.getFlagsForUid(uid, MASK_PERMISSION) and FLAG_ALLOWED) == FLAG_ALLOWED
        }
    }

    fun grant(packageName: String, uid: Int) {
        if (doc.isPreV11()) {
            docSystemApis.grantRuntimePermission(packageName, Manifest.permission.API_V23, uid / 100000)
        } else {
            doc.updateFlagsForUid(uid, MASK_PERMISSION, FLAG_ALLOWED)
        }
    }

    fun revoke(packageName: String, uid: Int) {
        if (doc.isPreV11()) {
            docSystemApis.revokeRuntimePermission(packageName, Manifest.permission.API_V23, uid / 100000)
        } else {
            doc.updateFlagsForUid(uid, MASK_PERMISSION, 0)
        }
    }
}
