package com.example.foreston.componentesTabLayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foreston.R
import com.example.foreston.databinding.FragmentIndustriaAserraderoBinding
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IndustriaAserraderoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IndustriaAserraderoFragment : Fragment() {

    private var _binding: FragmentIndustriaAserraderoBinding? = null
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding =  FragmentIndustriaAserraderoBinding.inflate(inflater, container, false)

        val carousel: ImageCarousel = binding.carouselAserradero
        carousel.registerLifecycle(viewLifecycleOwner)
        val list = mutableListOf<CarouselItem>()

        list.add(
            CarouselItem(
            imageDrawable = R.drawable.industria_aserradero2,
            caption = "Obtención de la madera")
        )
        list.add(
            CarouselItem(
            imageDrawable = R.drawable.aserradero,
            caption = "Puesta en planta")
        )
        list.add(
            CarouselItem(
            imageDrawable = R.drawable.industria_aserradero3,
            caption = "Seccionando")
        )
        list.add(
            CarouselItem(
            imageDrawable = R.drawable.aserradero4,
            caption = "Almacenamiento")
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