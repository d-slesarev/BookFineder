package ua.khai.slesarev.bookfinder.sing_in.fragments

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.databinding.FragSingInBinding
import ua.khai.slesarev.bookfinder.databinding.FragSingUpBinding

class SingIn : Fragment() {

    private lateinit var binding: FragSingInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

       binding = FragSingInBinding.inflate(inflater, container, false)
        Log.i("MyLog", "Pressed Registration111")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.SingUpBtn.setOnClickListener {
            findNavController().navigate(R.id.action_singIn_to_singUp)
            Log.i("MyLog", "Pressed Registration!!!")
        }
    }
}