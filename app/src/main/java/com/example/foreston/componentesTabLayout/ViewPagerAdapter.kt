package com.example.foreston.componentesTabLayout

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fa: Fragment): FragmentStateAdapter(fa) {

    companion object{
        private const val ARG_OBJECT = "object"
    }
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {

        return when(position){
            0 -> { InfoForestacionFragment() }
            1 -> { IndustriaAserraderoFragment() }
            2 -> { IndustriaPapeleraFragment()   }
            else -> InfoForestacionFragment()
        }

    }
}