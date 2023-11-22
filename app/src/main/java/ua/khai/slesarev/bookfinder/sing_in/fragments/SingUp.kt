package ua.khai.slesarev.bookfinder.sing_in.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.databinding.FragSingInBinding
import ua.khai.slesarev.bookfinder.databinding.FragSingUpBinding

class SingUp : Fragment() {

    private lateinit var binding: FragSingUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragSingUpBinding.inflate(inflater, container, false)
        Log.i("MyLog", "Pressed Registration111")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logInBtn.setOnClickListener {
            findNavController().navigate(R.id.action_singUp_to_singIn)
            Log.i("MyLog", "Pressed Registration!!!")
        }

        binding.passRegisterTexImp.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                // Обработка события "Готово" или "Enter"
                binding.passRegisterTexImp.clearFocus()

                // Скрытие клавиатуры
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                inputMethodManager.hideSoftInputFromWindow(binding.passRegisterTexImp.windowToken, 0)

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }
}