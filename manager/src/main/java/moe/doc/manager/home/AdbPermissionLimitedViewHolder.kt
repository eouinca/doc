package moe.doc.manager.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moe.doc.manager.Helps
import moe.doc.manager.databinding.HomeExtraStepRequiredBinding
import moe.doc.manager.databinding.HomeItemContainerBinding
import moe.doc.manager.utils.CustomTabsHelper
import rikka.recyclerview.BaseViewHolder
import rikka.recyclerview.BaseViewHolder.Creator

class AdbPermissionLimitedViewHolder(binding: HomeExtraStepRequiredBinding, root: View) : BaseViewHolder<Any?>(root) {

    companion object {
        val CREATOR = Creator<Any> { inflater: LayoutInflater, parent: ViewGroup? ->
            val outer = HomeItemContainerBinding.inflate(inflater, parent, false)
            val inner = HomeExtraStepRequiredBinding.inflate(inflater, outer.root, true)
            AdbPermissionLimitedViewHolder(inner, outer.root)
        }
    }

    init {
        binding.button1.setOnClickListener { v: View -> CustomTabsHelper.launchUrlOrCopy(v.context, Helps.ADB_PERMISSION.get()) }
    }
}
