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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
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

class SingUp : Fragment() {

    private lateinit var binding: FragSingUpBinding
    private lateinit var viewModel: SignUpViewModel
    private lateinit var act: SingInActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragSingUpBinding.inflate(inflater, container, false)

        act = (activity as SingInActivity)

        val factory = SignUpViewModelFactory(requireActivity().application, (activity as SingInActivity))

        viewModel = ViewModelProvider(this, factory).get(SignUpViewModel::class.java)


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.emailLiveData.observe(requireActivity(), Observer {
            binding.emailTextInputLayout.helperText = it
        })

        viewModel.passwordLiveData.observe(requireActivity(), Observer {
            binding.passTextInputLayout.helperText = it
        })

        viewModel.usernameLiveData.observe(requireActivity(), Observer {
            binding.textInputLayoutName.helperText = it
        })

        viewModel.emailColorLiveData.observe(requireActivity(), Observer {
            binding.emailTextInputLayout.setHelperTextColor(ColorStateList.valueOf(it))
        })

        viewModel.passwordColorLiveData.observe(requireActivity(), Observer {
            binding.passTextInputLayout.setHelperTextColor(ColorStateList.valueOf(it))
        })

        viewModel.usernameColorLiveData.observe(requireActivity(), Observer {
            binding.textInputLayoutName.setHelperTextColor(ColorStateList.valueOf(it))
        })

        binding.logInBtn.setOnClickListener {
            findNavController().navigate(R.id.action_singUp_to_singIn)
        }

        binding.createAccountBtn.setOnClickListener {
            lateinit var result: Response
            var userName = binding.nameTexInp.text.toString()
            var email = binding.emailRegisterTexInp.text.toString()
            var password = binding.passRegisterTexImp.text.toString()

            result = performSignUpTransaction(email, password, userName)
            println(result.toString())
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

    private fun performSignUpTransaction(email: String, password: String, username: String): Response {
        lateinit var result: Response
        act.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(act){ task: Task<AuthResult> ->

                if (task.isSuccessful) {
                    val user = task.result?.user!!

                    if (addUserToDatabase(user?.uid, email, username) == Response.SUCCESS) {
                        if (sendEmailVerification(user) == Response.SUCCESS) {
                            result = Response.SUCCESS
                        } else {
                            if (rollBackAddUser(user?.uid) == Response.SUCCESS) {
                                result = Response.ERROR_UNKNOWN
                            } else {
                                result = Response.SERVER_ERROR
                            }

                            if (rollBackRegister(user) == Response.SUCCESS) {
                                result = Response.ERROR_UNKNOWN
                            } else {
                                result = Response.SERVER_ERROR
                            }
                        }
                    } else {
                        if (rollBackRegister(user) == Response.SUCCESS) {
                            result = Response.ERROR_UNKNOWN
                        } else {
                            result = Response.SERVER_ERROR
                        }
                    }

                    act.auth.signOut()

                } else {
                    val exception = task.exception as FirebaseAuthException
                    val errorCode = exception.errorCode

                    when (errorCode) {
                        "ERROR_INVALID_EMAIL" -> {
                            result = Response.ERROR_INVALID_EMAIL
                        }

                        "ERROR_EMAIL_ALREADY_IN_USE" -> {
                            result = Response.ERROR_EMAIL_ALREADY_IN_USE
                        }

                        "ERROR_INVALID_CREDENTIAL" -> {
                            result = Response.ERROR_INVALID_CREDENTIAL
                        }

                        "ERROR_WEAK_PASSWORD" -> {
                            result = Response.ERROR_WEAK_PASSWORD
                        }

                        else -> {
                            result = Response.ERROR_UNKNOWN
                        }
                    }
                }
            }

        return result
    }

    fun sendEmailVerification(user: FirebaseUser): Response {
        lateinit var result: Response

        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = Response.SUCCESS
            } else {
                result = Response.ERROR_UNKNOWN
            }
        }

        return result
    }

    fun addUserToDatabase(uid: String?, email: String?, username: String): Response {
        lateinit var result: Response

        uid?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(it)
            val newUser = User(username, email!!)
            databaseReference.setValue(newUser).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result = Response.SUCCESS
                } else {
                    result = Response.ERROR_UNKNOWN
                }
            }
        }
        return result
    }

    fun rollBackAddUser(uid: String?): Response {
        lateinit var result: Response

        uid?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(it)
            databaseReference.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result = Response.SUCCESS
                } else {
                    result = Response.SERVER_ERROR
                }
            }
        }
        return result
    }

    fun rollBackRegister(user: FirebaseUser): Response {
        lateinit var result: Response

        user?.delete()
            ?.addOnCompleteListener { deleteTask ->
                if (deleteTask.isSuccessful) {
                    result = Response.SUCCESS
                } else {
                    result = Response.SERVER_ERROR
                }
            }

        return result
    }
}