package ua.khai.slesarev.bookfinder.data.util

enum class Event {
    // ВОЗМОЖНЫЕ КОДЫ ОШИБОК ПРИ РЕГИСТРАЦИИ
    // Ошибки которые выводим в текст
    ERROR_EMAIL_ALREADY_IN_USE, // Указанный адрес электронной почты уже используется другим пользователем.
    ERROR_INVALID_CREDENTIAL, // Предоставленные учетные данные недействительны или их тип не поддерживается
    ERROR_WEAK_PASSWORD, // Пароль слишком слабый
    ERROR_INVALID_EMAIL, // Указан некорректный формат адреса электронной почты.
    ERROR_MISSING_EMAIL, // Адрес электронной почты отсутствует
    ERROR_MISSING_PASSWORD, // Пароль отсутствует
    ERROR_MISSING_NAME,
    ERROR_MISSING_EMAIL_AND_PASSWORD,
    ERROR_MISSING_NAME_AND_PASSWORD,
    ERROR_MISSING_EMAIL_AND_PASSWORD_AND_NAME,
    ERROR_MISSING_EMAIL_AND_NAME,
    // ВОЗМОЖНЫЕ КОДЫ ОШИБОК ПРИ ВХОДЕ
    ERROR_USER_NOT_FOUND, // Пользователь не найден
    ERROR_UNCONFIRMED_EMAIL, // Код действия (например, подтверждение адреса электронной почты) истек.
    // ДРУГИЕ ОШИБКИ
    ERROR_TOO_MANY_REQUESTS,
    ERROR_UNKNOWN,
    SERVER_ERROR,
    ERROR_WRONG_PASSWORD,
    FAILURE,
    // ЕСЛИ ВСЁ ХОРОШО
    SUCCESS,
    GOOGLE_SUCCESS,
    // ВОЗМОЖНЫЕ КОДЫ ОШИБОК ПРИ ВХОДЕ ЧЕРЕЗ GOOGLE
    ERROR_INVALID_CUSTOM_TOKEN,
    ERROR_CUSTOM_TOKEN_MISMATCH,
    ERROR_USER_MISMATCH,
    // ДРУГИЕ КОДЫ
    NEW_USER,
    OLD_USER
}

/*
* Log.d(TAG, "UserRepository.addUser: SUCCESS!")
*
* Log.d(TAG, "UserRepository.addUser: FAILURE!")
*
* Log.d(TAG, "UserRepository.addUser-Exception: ${e.message.toString()}")
*
* */