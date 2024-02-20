package ua.khai.slesarev.bookfinder

import org.junit.Test
import org.junit.jupiter.api.DisplayName
// import org.junit.Assert.*

//import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    @DisplayName("Incorrect email address")
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    @DisplayName("mail is already in use")
    fun addition_isCorrect1() {
        assertEquals(4, 2 + 2)
    }

    @Test
    @DisplayName("invalid password")
    fun addition_isCorrect2() {
        assertEquals(4, 2 + 2)
    }

    @Test
    @DisplayName("weak password")
    fun addition_isCorrect3() {
        assertEquals(4, 2 + 2)
    }
}