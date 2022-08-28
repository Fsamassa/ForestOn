package com.example.foreston

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.foreston.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding:FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding =  FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnScanArbol.setOnClickListener{
            val builder = AlertDialog.Builder(binding.btnScanArbol.context)
            builder.setTitle("Pendiente!")
            builder.setMessage("Implementar fragment de escaneo de árbol")
            builder.setPositiveButton("Sorry Bro!", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        binding.btnBuscarSocios.setOnClickListener{
            val builder = AlertDialog.Builder(binding.btnBuscarSocios.context)
            builder.setTitle("Pendiente!")
            builder.setMessage("Implementar fragment para buscar socios")
            builder.setPositiveButton("Sorry Bro!", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        binding.btnInfo.setOnClickListener{
            val builder = AlertDialog.Builder(binding.btnInfo.context)
            builder.setTitle("Pendiente!")
            builder.setMessage("Implementar fragment para ingresar info")
            builder.setPositiveButton("Sorry Bro!", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        binding.btnReportes.setOnClickListener{
            val builder = AlertDialog.Builder(binding.btnReportes.context)
            builder.setTitle("Pendiente!")
            builder.setMessage("Implementar fragment para ver reportes")
            builder.setPositiveButton("Sorry Bro!", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        return binding.root
    }

}