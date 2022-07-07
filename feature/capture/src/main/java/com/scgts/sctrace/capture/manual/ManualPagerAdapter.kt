package com.scgts.sctrace.capture.manual

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ManualPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val arrayList: ArrayList<Fragment> = ArrayList()
    override fun getItemCount(): Int = arrayList.size

    override fun createFragment(position: Int): Fragment = arrayList[position]

    fun addFragment(fragment: Fragment) {
        arrayList.add(fragment)
    }

    fun render(viewState: ManualCaptureMvi.ViewState) {
        arrayList.forEach { (it as FragmentRenderer).render(viewState) }
    }
}
