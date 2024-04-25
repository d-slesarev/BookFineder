package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SingIn

import android.content.Context
import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.databinding.FragSingInBinding
import ua.khai.slesarev.bookfinder.ui.util.UiState
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.GOOGLE_REQUEST_CODE
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.trusted.TokenStore
import androidx.core.view.isVisible
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import ua.khai.slesarev.bookfinder.data.repository.authentication.appoauth.TokenStorage
import ua.khai.slesarev.bookfinder.ui.util.launchAndCollectIn
import ua.khai.slesarev.bookfinder.ui.util.toast

class SingIn : Fragment() {

    private val binding by lazy { FragSingInBinding.inflate(layoutInflater) }
    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    private val getAuthResponse = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val dataIntent = it.data ?: return@registerForActivityResult
        handleAuthResponseIntent(dataIntent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        binding.singInBtn.setOnClickListener{
            viewModel.openLoginPage()
        }

        viewModel.loadingFlow.launchAndCollectIn(viewLifecycleOwner) {
            onLoad(it)
        }
        viewModel.openAuthPageFlow.launchAndCollectIn(viewLifecycleOwner) {
            openAuthPage(it)
        }
        viewModel.toastFlow.launchAndCollectIn(viewLifecycleOwner) {
            toast(it)
        }
        viewModel.tokenReceiptSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {
            viewModel.signInWithGoogle()
        }
        viewModel.firebaseAuthSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {
            viewModel.loadUserProfile()
        }
        viewModel.loadProfileSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_singIn_to_homeActivity)
            requireActivity().finish()
        }
    }

    private fun handleAuthResponseIntent(intent: Intent) {
        // пытаемся получить ошибку из ответа. null - если все ок
        val exception = AuthorizationException.fromIntent(intent)
        // пытаемся получить запрос для обмена кода на токен, null - если произошла ошибка
        val tokenExchangeRequest = AuthorizationResponse.fromIntent(intent)
            ?.createTokenExchangeRequest()
        when {
            // авторизация завершались ошибкой
            exception != null -> viewModel.onAuthCodeFailed(exception)
            // авторизация прошла успешно, меняем код на токен
            tokenExchangeRequest != null ->
                viewModel.onAuthCodeReceived(tokenExchangeRequest)
        }
    }

    private fun openAuthPage(intent: Intent) {
        getAuthResponse.launch(intent)
    }

    private fun onLoad(isLoading: Boolean) = with(binding) {
        signInProgrBar.isVisible = !isLoading
        singInBtn.isEnabled = isLoading
        singUpBtn.isEnabled = isLoading
    }
}