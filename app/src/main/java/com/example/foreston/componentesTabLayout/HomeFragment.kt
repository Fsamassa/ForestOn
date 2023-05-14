package com.example.foreston.componentesTabLayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foreston.R
import com.example.foreston.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private var _binding:FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding =  FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.pager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.pager,TabLayoutMediator.TabConfigurationStrategy { tab, position ->

            when(position){
                0 -> {
                    tab.text = "Forestación"
                    tab.setIcon(R.drawable.ic_arboles)
                }
                1 -> {
                    tab.text = "Aserraderos"
                    tab.setIcon(R.drawable.saw_icon)
                }
                2 -> {
                    tab.text = "Papeleras"
                    tab.setIcon(R.drawable.ic_outline_factory)
                }
            }
        }).attach()
    }

}