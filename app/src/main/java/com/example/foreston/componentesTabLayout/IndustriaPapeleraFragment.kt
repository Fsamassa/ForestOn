package com.example.foreston.componentesTabLayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foreston.R
import com.example.foreston.databinding.FragmentIndustriaPapeleraBinding
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IndustriaPapeleraFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IndustriaPapeleraFragment : Fragment() {

    private var _binding: FragmentIndustriaPapeleraBinding? = null
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
        _binding =  FragmentIndustriaPapeleraBinding.inflate(inflater, container, false)

        val carousel: ImageCarousel = binding.carouselPapeleras
        carousel.registerLifecycle(viewLifecycleOwner)
        val list = mutableListOf<CarouselItem>()

        list.add(
            CarouselItem(
            imageDrawable = R.drawable.papeleras,
            caption = "La Industria Papelera")
        )
        list.add(
            CarouselItem(
            imageDrawable = R.drawable.celulosa,
            caption = "Pasta de Celulosa")
        )
        list.add(
            CarouselItem(
            imageDrawable = R.drawable.papeleras2,
            caption = "Materia Prima de Reciclaje")
        )
        list.add(
            CarouselItem(
            imageDrawable = R.drawable.papeleras_color,
            caption = "Diversificación de Productos")
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