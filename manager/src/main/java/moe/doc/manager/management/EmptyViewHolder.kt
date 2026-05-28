package moe.doc.manager.management

import android.content.pm.PackageInfo
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import moe.doc.manager.Helps
import moe.doc.manager.R
import moe.doc.manager.authorization.AuthorizationManager
import moe.doc.manager.databinding.AppListEmptyBinding
import moe.doc.manager.databinding.AppListItemBinding
import moe.doc.manager.ktx.toHtml
import moe.doc.manager.utils.AppIconCache
import moe.doc.manager.utils.docSystemApis
import moe.doc.manager.utils.UserHandleCompat
import rikka.html.text.HtmlCompat
import rikka.recyclerview.BaseViewHolder
import rikka.recyclerview.BaseViewHolder.Creator
import rikka.doc.doc

class EmptyViewHolder(private val binding: AppListEmptyBinding) : BaseViewHolder<Any>(binding.root) {

    companion object {
        @JvmField
        val CREATOR = Creator<Any> { inflater: LayoutInflater, parent: ViewGroup? -> EmptyViewHolder(AppListEmptyBinding.inflate(inflater, parent, false)) }
    }

}
