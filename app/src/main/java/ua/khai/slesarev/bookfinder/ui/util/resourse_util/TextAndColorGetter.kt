package ua.khai.slesarev.bookfinder.ui.util.resourse_util

val resourses_for_sign_up: Map<String, List<String>> = mapOf(
    "ERROR_INVALID_EMAIL" to listOf("invalid_email", "not_checked", "not_checked", "RED", "BROWN", "BROWN"),
    "ERROR_EMAIL_ALREADY_IN_USE" to listOf("email_in_use", "not_checked", "not_checked", "RED", "BROWN", "BROWN"),
    "ERROR_INVALID_CREDENTIAL" to listOf("invalid_credential", "invalid_credential", "not_checked", "RED", "RED", "BROWN"),
    "ERROR_WEAK_PASSWORD" to listOf("not_checked", "week_password", "not_checked", "BROWN", "RED", "BROWN"),
    "ERROR_MISSING_NAME" to listOf("not_checked", "not_checked", "required", "BROWN", "BROWN", "RED"),
    "ERROR_MISSING_EMAIL" to listOf("required", "not_checked", "not_checked", "RED", "BROWN", "BROWN"),
    "ERROR_MISSING_PASSWORD" to listOf("not_checked", "required", "not_checked", "BROWN", "RED", "BROWN"),
    "ERROR_MISSING_EMAIL_AND_PASSWORD" to listOf("required", "required", "not_checked", "RED", "RED", "BROWN"),
    "ERROR_MISSING_EMAIL_AND_NAME" to listOf("required", "not_checked", "required", "RED", "BROWN", "RED"),
    "ERROR_MISSING_NAME_AND_PASSWORD" to listOf("not_checked", "required", "required", "BROWN", "RED", "RED"),
    "ERROR_MISSING_EMAIL_AND_PASSWORD_AND_NAME" to listOf("required", "required", "required", "BROWN", "BROWN", "BROWN"),
    "ERROR_UNKNOWN" to listOf("correctly", "correctly", "correctly", "GREEN", "GREEN", "GREEN"),
    "SERVER_ERROR" to listOf("correctly", "correctly", "correctly", "GREEN", "GREEN", "GREEN"),
    "SUCCESS" to listOf("correctly", "correctly", "correctly", "GREEN", "GREEN", "GREEN"),
)

val resourses_for_sign_in: Map<String, List<String>> = mapOf(
    "ERROR_INVALID_EMAIL" to listOf("invalid_email", "not_checked", "RED", "BROWN"),
    "ERROR_WRONG_PASSWORD" to listOf("not_checked", "wrong_password", "BROWN", "RED"),
    "ERROR_EMAIL_ALREADY_IN_USE" to listOf("email_in_use", "not_checked", "RED", "BROWN"),
    "ERROR_USER_NOT_FOUND" to listOf("user_not_found", "not_checked", "RED", "BROWN"),
    "ERROR_INVALID_CREDENTIAL" to listOf("invalid_credential", "invalid_credential", "RED", "RED"),
    "ERROR_MISSING_EMAIL" to listOf("required", "not_checked", "RED", "BROWN"),
    "ERROR_MISSING_PASSWORD" to listOf("not_checked", "required", "BROWN", "RED"),
    "ERROR_MISSING_EMAIL_AND_PASSWORD" to listOf("required", "required", "RED", "RED"),
    "ERROR_UNKNOWN" to listOf("correctly", "correctly", "GREEN", "GREEN"),
    "ERROR_UNCONFIRMED_EMAIL" to listOf("correctly", "correctly", "GREEN", "GREEN"),
    "SUCCESS" to listOf("correctly", "correctly", "GREEN", "GREEN"),
)

fun getResoursesForSignUp(): Map<String, List<String>>{
    return resourses_for_sign_up
}

fun getResoursesForSignIn(): Map<String, List<String>>{
    return resourses_for_sign_in
}