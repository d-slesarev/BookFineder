package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SignUp

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.databinding.FragSingUpBinding
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.SingInActivity
import ua.khai.slesarev.bookfinder.util.AccountHelper.FirebaseAccHelper
import ua.khai.slesarev.bookfinder.util.AccountHelper.Response
import ua.khai.slesarev.bookfinder.util.resourse_util.getResourseMap

class SingUp : Fragment() {

    private lateinit var binding: FragSingUpBinding
    private val viewModel: SignUpViewModel by viewModels()
    private lateinit var act: SingInActivity
    private val resorMap: Map<String, List<String>> = getResourseMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragSingUpBinding.inflate(inflater, container, false)
        act = (activity as SingInActivity)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logInBtn.setOnClickListener {
            findNavController().navigate(R.id.action_singUp_to_singIn)
        }

        binding.createAccountBtn.setOnClickListener {
            lateinit var result: Response
            var userName = binding.nameTexInp.text.toString()
            var email = binding.emailRegisterTexInp.text.toString()
            var password = binding.passRegisterTexImp.text.toString()

            viewModel.signUpWithEmailPassword(email, password, userName)
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

        viewModel.uiState.observe(requireActivity()) { uiState ->
            if (uiState != null) {
                render(uiState)
            }
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

    fun setResourses(response: String){

        val resorIdList = resorMap.get(response)

        if (resorIdList != null) {
            binding.emailTextInputLayout.helperText = getStringResourceByName(requireContext(), resorIdList.get(0))
            binding.passTextInputLayout.helperText = getStringResourceByName(requireContext(), resorIdList.get(1))
            binding.textInputLayoutName.helperText = getStringResourceByName(requireContext(), resorIdList.get(2))

            binding.emailTextInputLayout.setHelperTextColor(getColorResourceByName(requireContext(), resorIdList.get(3))?.let {
                ColorStateList.valueOf(
                    it
                )
            })

            binding.passTextInputLayout.setHelperTextColor(getColorResourceByName(requireContext(), resorIdList.get(4))?.let {
                ColorStateList.valueOf(
                    it
                )
            })

            binding.textInputLayoutName.setHelperTextColor(getColorResourceByName(requireContext(), resorIdList.get(5))?.let {
                ColorStateList.valueOf(
                    it
                )
            })
        }
    }

    private fun updateRender(response:String){
        when (response) {

            Response.ERROR_INVALID_EMAIL.toString() -> {
                setResourses(response)
            }

            Response.ERROR_EMAIL_ALREADY_IN_USE.toString() -> {
                setResourses(response)
            }

            Response.ERROR_INVALID_CREDENTIAL.toString() -> {
                setResourses(response)
            }

            Response.ERROR_WEAK_PASSWORD.toString() -> {
                setResourses(response)
            }

            Response.ERROR_MISSING_NAME.toString() -> {
                setResourses(response)
            }

            Response.ERROR_MISSING_EMAIL.toString() -> {
                setResourses(response)
            }

            Response.ERROR_MISSING_PASSWORD.toString() -> {
                setResourses(response)
            }

            Response.ERROR_MISSING_EMAIL_AND_PASSWORD.toString() -> {
                setResourses(response)
            }

            Response.ERROR_MISSING_EMAIL_AND_NAME.toString() -> {
                setResourses(response)
            }

            Response.ERROR_MISSING_NAME_AND_PASSWORD.toString() -> {
                setResourses(response)
            }

            Response.ERROR_MISSING_EMAIL_AND_PASSWORD_AND_NAME.toString() -> {
                setResourses(response)
            }

            Response.ERROR_UNKNOWN.toString() -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(context?.getString(R.string.unknown))
                    .setMessage(context?.getString(R.string.unknown_message))
                    .setPositiveButton("OK") { dialog, which ->
                    }.show()

                setResourses(response)
            }

            Response.SERVER_ERROR.toString() -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(context?.getString(R.string.server))
                    .setMessage(context?.getString(R.string.server_message))
                    .setPositiveButton("OK") { dialog, which ->
                    }.show()

                setResourses(response)
            }

            Response.SUCCESS.toString() -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(context?.getString(R.string.confirm))
                    .setMessage(context?.getString(R.string.confirm_message))
                    .setPositiveButton("OK") { dialog, which ->
                        findNavController().navigate(R.id.action_singUp_to_singIn)
                    }.show()

                setResourses(response)

            }

            else -> {}
        }
    }

    private fun render(uiState: UiState) {

        when (uiState) {
            is UiState.Loading -> {

            }
            is UiState.Success -> {
                updateRender(uiState.response)
            }
            is UiState.Error -> {
                updateRender(uiState.response)
            }
        }
    }
}