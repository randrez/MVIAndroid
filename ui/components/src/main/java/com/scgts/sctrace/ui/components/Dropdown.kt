package com.scgts.sctrace.ui.components

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.view.isVisible
import com.scgts.sctrace.root.components.databinding.ItemDropdownBinding
import com.scgts.sctrace.base.model.Identifiable
import com.scgts.sctrace.base.model.Named

open class Dropdown<T>(
    open val context: Context,
    open val onItemClicked: (T) -> Unit
) : ListPopupWindow(context) where T : Identifiable, T : Named {

    private var data: List<T> = listOf()
    private val adapter: DropdownAdapter

    init {
        adapter = DropdownAdapter()
        setAdapter(adapter)
        height = WRAP_CONTENT
    }

    fun setData(data: List<T>) {
        this.data = data
        adapter.notifyDataSetChanged()
    }

    private inner class DropdownAdapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val binding =
                if (convertView != null) ItemDropdownBinding.bind(convertView) else ItemDropdownBinding.inflate(
                    LayoutInflater.from(context)
                )
            val dataDropdown = data[position]

            binding.dropdownName.text = dataDropdown.name
            binding.divider.rowDivider.isVisible = dataDropdown != data.last()
            binding.root.setOnClickListener {
                onItemClicked(dataDropdown)
                dismiss()
            }
            return binding.root
        }

        override fun getCount() = data.size
        override fun getItemId(position: Int) = position.toLong()
        override fun getItem(position: Int) = data[position]
    }
}