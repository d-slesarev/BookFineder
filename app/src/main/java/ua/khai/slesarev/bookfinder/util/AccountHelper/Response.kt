package ua.khai.slesarev.bookfinder.util.AccountHelper

enum class Response {
    // Возможные коды ошибок при регистрации
    ERROR_INVALID_EMAIL, // Указан некорректный формат адреса электронной почты.
    ERROR_EMAIL_ALREADY_IN_USE, // Указанный адрес электронной почты уже используется другим пользователем.
    ERROR_INVALID_CREDENTIAL, // Предоставленные учетные данные недействительны или их тип не поддерживается
    ERROR_WEAK_PASSWORD, // Пароль слишком слабый
    // Возможные коды ошибок при входе
    ERROR_USER_NOT_FOUND, // Пользователь не найден
    ERROR_EXPIRED_ACTION_CODE, // Код действия (например, подтверждение адреса электронной почты) истек.
    // Другие ошибки
    ERROR_UNKNOWN,
    // Если всё хорошо
    SUCCESS

}