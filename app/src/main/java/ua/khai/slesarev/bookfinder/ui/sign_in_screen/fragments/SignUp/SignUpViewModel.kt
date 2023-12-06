package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SignUp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ua.khai.slesarev.bookfinder.util.AccountHelper.AccountHelper
import ua.khai.slesarev.bookfinder.util.AccountHelper.AccountHelperProvider
import ua.khai.slesarev.bookfinder.util.AccountHelper.FirebaseAccHelper
import ua.khai.slesarev.bookfinder.util.AccountHelper.Response

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    fun signUpWithEmailPassword(email: String, password: String, username: String) {
        lateinit var result: Response
        val accHelper = AccountHelperProvider().getAccountHelper()

        result = accHelper.signUpWithEmailPassword(email, password, username)

        MaterialAlertDialogBuilder(getApplication())

        when (result) {
            Response.ERROR_INVALID_EMAIL -> {
                result = Response.ERROR_INVALID_EMAIL
            }

            Response.ERROR_EMAIL_ALREADY_IN_USE -> {
                result = Response.ERROR_EMAIL_ALREADY_IN_USE
            }

            Response.ERROR_INVALID_CREDENTIAL -> {
                result = Response.ERROR_INVALID_CREDENTIAL
            }

            Response.ERROR_WEAK_PASSWORD -> {
                result = Response.ERROR_WEAK_PASSWORD
            }

            Response.ERROR_UNKNOWN -> {
                result = Response.ERROR_WEAK_PASSWORD
            }

            else -> {}
        }
    }

    private fun getAlertDialog(status:Response): MaterialAlertDialogBuilder{
        lateinit var result: MaterialAlertDialogBuilder

        when (status) {
            Response.ERROR_INVALID_EMAIL -> {
                MaterialAlertDialogBuilder(getApplication())
                    .setTitle("Заголовок диалога")
                    .setMessage("Это текст сообщения в диалоге.")
                    .setPositiveButton("Подтвердить") { dialog, which ->
                        // Обработка нажатия кнопки "Подтвердить"
                    }
                    .setNegativeButton("Отмена"){ dialog, which ->
                        // Обработка нажатия кнопки "Подтвердить"
                    }
                    .setNeutralButton("Дополнительно"){ dialog, which ->
                        // Обработка нажатия кнопки "Подтвердить"
                    }
            }

            Response.ERROR_EMAIL_ALREADY_IN_USE -> {
                MaterialAlertDialogBuilder(getApplication())
                    .setTitle("Заголовок диалога")
                    .setMessage("Это текст сообщения в диалоге.")
                    .setPositiveButton("OK") { dialog, which ->
                        // Обработка нажатия кнопки "Подтвердить"
                    }
            }

            Response.ERROR_INVALID_CREDENTIAL -> {
                MaterialAlertDialogBuilder(getApplication())
                    .setTitle("Заголовок диалога")
                    .setMessage("Это текст сообщения в диалоге.")
                    .setPositiveButton("OK") { dialog, which ->
                        // Обработка нажатия кнопки "Подтвердить"
                    }
            }

            Response.ERROR_WEAK_PASSWORD -> {
                MaterialAlertDialogBuilder(getApplication())
                    .setTitle("Password is too simple")
                    .setMessage("Try to think of a more complex password to protect your account from intruders.")
                    .setPositiveButton("OK") { dialog, which ->
                        // Обработка нажатия кнопки "Подтвердить"
                    }
            }

            Response.ERROR_UNKNOWN -> {
                MaterialAlertDialogBuilder(getApplication())
                    .setTitle("Unknown error")
                    .setMessage("This may be a temporary problem, please try again later.")
                    .setPositiveButton("OK") { dialog, which ->
                        // Обработка нажатия кнопки "Подтвердить"
                    }
            }

            else -> {
                MaterialAlertDialogBuilder(getApplication())
                    .setTitle("Confirm your e-mail")
                    .setMessage("We've sent you a link to confirm your email. Please go to your email and follow the confirmation link.")
                    .setPositiveButton("OK") { dialog, which ->
                        // TODO: Добавь здесь навигацию к SignIn Fragment. Создай для этого ViewModelFactory
                    }
            }
        }


        return  MaterialAlertDialogBuilder(getApplication())
    }
}