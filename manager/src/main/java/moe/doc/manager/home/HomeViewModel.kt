package moe.doc.manager.home

import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.doc.manager.BuildConfig
import moe.doc.manager.Manifest
import moe.doc.manager.model.ServiceStatus
import moe.doc.manager.utils.Logger.LOGGER
import moe.doc.manager.utils.docSystemApis
import rikka.lifecycle.Resource
import rikka.doc.doc

class HomeViewModel : ViewModel() {

    private val _serviceStatus = MutableLiveData<Resource<ServiceStatus>>()
    val serviceStatus = _serviceStatus as LiveData<Resource<ServiceStatus>>

    private fun load(): ServiceStatus {
        if (!doc.pingBinder()) {
            return ServiceStatus()
        }

        val uid = doc.getUid()
        val apiVersion = doc.getVersion()
        val patchVersion = doc.getServerPatchVersion().let { if (it < 0) 0 else it }
        val seContext = if (apiVersion >= 6) {
            try {
                doc.getSELinuxContext()
            } catch (tr: Throwable) {
                LOGGER.w(tr, "getSELinuxContext")
                null
            }
        } else null
        val permissionTest =
            doc.checkRemotePermission("android.permission.GRANT_RUNTIME_PERMISSIONS") == PackageManager.PERMISSION_GRANTED

        // Before a526d6bb, server will not exit on uninstall, manager installed later will get not permission
        // Run a random remote transaction here, report no permission as not running
        docSystemApis.checkPermission(Manifest.permission.API_V23, BuildConfig.APPLICATION_ID, 0)
        return ServiceStatus(uid, apiVersion, patchVersion, seContext, permissionTest)
    }

    fun reload() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val status = load()
                _serviceStatus.postValue(Resource.success(status))
            } catch (e: CancellationException) {

            } catch (e: Throwable) {
                _serviceStatus.postValue(Resource.error(e, ServiceStatus()))
            }
        }
    }
}
