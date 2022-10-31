package com.example.foreston.componentesTabLayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foreston.R
import com.example.foreston.databinding.FragmentInfoForestacionBinding
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IndustriaPapeleraFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoForestacionFragment : Fragment() {

    private var _binding: FragmentInfoForestacionBinding? = null
    private val binding get() = _binding!!

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentInfoForestacionBinding.inflate(inflater, container, false)

        val carousel: ImageCarousel = binding.carouselForestacion
        carousel.registerLifecycle(viewLifecycleOwner)
        val list = mutableListOf<CarouselItem>()

        list.add(CarouselItem(
            imageDrawable = R.drawable.forestacion3,
            caption = "Bosques reforestados")
        )
        list.add(CarouselItem(
            imageDrawable = R.drawable.plantaciones,
            caption = "Forestación escalonada")
        )
        list.add(CarouselItem(
            imageDrawable = R.drawable.plantaciones2,
            caption = "Forestaciones adultas")
        )
        list.add(CarouselItem(
            imageDrawable = R.drawable.forestacion1,
            caption = "Forestaciones longevas")
        )
        carousel.setData(list)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {

        }
    }
    companion object {
        private const val ARG_OBJECT = "object"
    }
}