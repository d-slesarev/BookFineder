package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SingIn

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ua.khai.slesarev.bookfinder.ui.home_screen.HomeActivity
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.databinding.FragSingInBinding
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SignUp.UiState
import ua.khai.slesarev.bookfinder.util.AccountHelper.Response
import ua.khai.slesarev.bookfinder.util.resourse_util.getResoursesForSignIn

class SingIn : Fragment() {

    private val binding by lazy { FragSingInBinding.inflate(layoutInflater) }
    private val viewModel: SignInViewModel by viewModels()
    private val resorMap: Map<String, List<String>> = getResoursesForSignIn()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.singIn) {
                navController.popBackStack(R.id.singUp, true)
                navController.popBackStack(R.id.passRecovery, true)
            }
        }

        binding.singUpBtn.setOnClickListener {
            findNavController().navigate(R.id.action_singIn_to_singUp)
        }

        binding.singInBtn.setOnClickListener {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            startActivity(intent)
        }

        binding.forgotBtn.setOnClickListener {
            findNavController().navigate(R.id.action_singIn_to_passRecovery)
        }

        binding.passSignTexInp.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                // Обработка события "Готово" или "Enter"
                binding.passSignTexInp.clearFocus()

                // Скрытие клавиатуры
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                inputMethodManager.hideSoftInputFromWindow(binding.passSignTexInp.windowToken, 0)

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        viewModel.uiState.observe(requireActivity()) { uiState ->
            if (uiState != null) {
                render(uiState)
            }
        }
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
        }
    }

    private fun onLoad() = with(binding) {
        signInProgrBar.visibility = View.VISIBLE
        singInBtn.isEnabled = false
        singUpBtn.isEnabled = false
        googleSingInBtn.isEnabled = false
    }

    private fun updateRender(response:String) = with(binding){

        signInProgrBar.visibility = View.GONE
        singInBtn.isEnabled = true
        singUpBtn.isEnabled = true
        googleSingInBtn.isEnabled = true

        when (response) {

            Response.ERROR_INVALID_EMAIL.toString() -> {
                setResourses(response)
            }

            Response.ERROR_INVALID_CREDENTIAL.toString() -> {
                setResourses(response)
            }

            Response.ERROR_MISSING_EMAIL.toString() -> {
                setResourses(response)
            }

            Response.ERROR_USER_NOT_FOUND.toString() -> {
                setResourses(response)
            }


            Response.ERROR_MISSING_PASSWORD.toString() -> {
                setResourses(response)
            }

            Response.ERROR_MISSING_EMAIL_AND_PASSWORD.toString() -> {
                setResourses(response)
            }

            Response.ERROR_UNCONFIRMED_EMAIL.toString() -> {
                setResourses(response)

                val alertDialog =
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(context?.getString(R.string.unconfirmed_email))
                        .setMessage(context?.getString(R.string.unconfirmed_message))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .create()

                alertDialog.show()
            }

            Response.ERROR_UNKNOWN.toString() -> {

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

            Response.SUCCESS.toString() -> {

                setResourses(response)

                val alertDialog =
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(context?.getString(R.string.confirm))
                        .setMessage(context?.getString(R.string.confirm_message))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                            findNavController().navigate(R.id.action_singUp_to_singIn)
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
            emailTextInputLayout.helperText = getStringResourceByName(requireContext(), resorIdList.get(0))
            passTextInputLayout.helperText = getStringResourceByName(requireContext(), resorIdList.get(1))

            emailTextInputLayout.setHelperTextColor(getColorResourceByName(requireContext(), resorIdList.get(2))?.let {
                ColorStateList.valueOf(
                    it
                )
            })

            passTextInputLayout.setHelperTextColor(getColorResourceByName(requireContext(), resorIdList.get(3))?.let {
                ColorStateList.valueOf(
                    it
                )
            })

        }
    }











}