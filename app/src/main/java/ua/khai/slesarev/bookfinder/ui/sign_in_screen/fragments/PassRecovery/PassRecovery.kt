package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.PassRecovery

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.databinding.FragPassRecoveryBinding

class PassRecovery : Fragment() {

    private lateinit var binding: FragPassRecoveryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragPassRecoveryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backToLogInBtn.setOnClickListener {
            findNavController().navigate(R.id.action_passRecovery_to_singIn)
        }

        binding.resetEmailTextInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                // Обработка события "Готово" или "Enter"
                binding.resetEmailTextInput.clearFocus()

                // Скрытие клавиатуры
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                inputMethodManager.hideSoftInputFromWindow(binding.resetEmailTextInput.windowToken, 0)

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }
}
