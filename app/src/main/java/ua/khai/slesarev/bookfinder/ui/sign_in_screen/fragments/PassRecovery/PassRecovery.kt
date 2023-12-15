package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.PassRecovery

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import ua.khai.slesarev.bookfinder.databinding.FragPassRecoveryBinding
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SingIn.SignInViewModel
import ua.khai.slesarev.bookfinder.ui.util.UiState
import ua.khai.slesarev.bookfinder.ui.util.resourse_util.getResoursesForSignIn

class PassRecovery : Fragment() {

    private lateinit var binding: FragPassRecoveryBinding
    private val resorMap: Map<String, List<String>> = getResoursesForSignIn()
    private val viewModel: PassRecoveryViewModel by viewModels()

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

        binding.sendLinkBtn.setOnClickListener {
            val email = binding.resetEmailTextInput.text.toString()

            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.resetPassword(email)
            }
        }

        viewModel.uiState.observe(requireActivity(), Observer {uiState ->
            if (uiState != null) {
                render(uiState)
            }
        })
    }

    private fun render(uiState: UiState) {

        when (uiState) {
            is UiState.Loading -> {
                onLoad()
            }
            is UiState.Success -> {
                updateRender(uiState.response)
            }
            is UiState.Error -> {
                updateRender(uiState.response)
            }

            else -> {}
        }
    }

    private fun onLoad() = with(binding) {
        recovProgrBar.visibility = View.VISIBLE
        sendLinkBtn.isEnabled = false
        backToLogInBtn.isEnabled = false
    }

    private fun updateRender(response:String) = with(binding){
        recovProgrBar.visibility = View.GONE
        sendLinkBtn.isEnabled = true
        backToLogInBtn.isEnabled = true

        when (response) {

            Event.ERROR_INVALID_EMAIL.toString() -> {
                setResourses(response)
            }

            Event.ERROR_MISSING_EMAIL.toString() -> {
                setResourses(response)
            }

            Event.ERROR_USER_NOT_FOUND.toString() -> {
                setResourses(response)
            }

            Event.ERROR_UNKNOWN.toString() -> {

                setResourses(response)

                val alertDialog =
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(context?.getString(R.string.unknown))
                        .setMessage(context?.getString(R.string.unknown_message))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .create()

                alertDialog.show()
            }

            Event.ERROR_TOO_MANY_REQUESTS.toString() -> {

                setResourses(response)

                val alertDialog =
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(context?.getString(R.string.many_requests))
                        .setMessage(context?.getString(R.string.many_requests_message))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .create()

                alertDialog.show()
            }

            Event.SUCCESS.toString() -> {

                setResourses(response)

                val alertDialog =
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(context?.getString(R.string.reset_email))
                        .setMessage(context?.getString(R.string.reset_message))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                            findNavController().navigate(R.id.action_passRecovery_to_singIn)
                        }
                        .setCancelable(false)
                        .create()

                alertDialog.show()
            }

            else -> {}
        }
    }

    fun getStringResourceByName(context: Context, resourceName: String): String? {
        val resId: Int = context.resources.getIdentifier(resourceName, "string", context.packageName)

        // Проверяем, существует ли такой ресурс
        if (resId != 0) {
            return context.getString(resId)
        } else {
            // Ресурс не найден
            return null
        }
    }

    fun getColorResourceByName(context: Context, resourceName: String): Int? {
        val resId: Int = context.resources.getIdentifier(resourceName, "color", context.packageName)

        // Проверяем, существует ли такой ресурс
        if (resId != 0) {
            return ContextCompat.getColor(context, resId)
        } else {
            // Ресурс не найден
            return null
        }
    }

    fun setResourses(response: String) = with(binding){

        val resorIdList = resorMap.get(response)

        if (resorIdList != null) {

            emailResetTextInputLayout.setHelperTextColor(getColorResourceByName(requireContext(), resorIdList.get(2))?.let {
                ColorStateList.valueOf(
                    it
                )
            })

            emailResetTextInputLayout.helperText = getStringResourceByName(requireContext(), resorIdList.get(0))
        }
    }
}
