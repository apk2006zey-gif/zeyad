package com.example

import com.example.data.local.RomanticQuotesRepository
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun romanticQuotes_areAvailable() {
        val quotes = RomanticQuotesRepository.quotes
        assertTrue("Quotes list should not be empty", quotes.isNotEmpty())
        val randomQuote = RomanticQuotesRepository.getRandomQuote()
        assertNotNull(randomQuote.text)
        assertTrue(randomQuote.text.isNotEmpty())
    }

    @Test
    fun defaultCoupleCredentials_format() {
        val expectedPass = "18/12"
        val expectedName1 = "زياد"
        val expectedName2 = "مريومه"

        assertEquals("18/12", expectedPass)
        assertEquals("زياد", expectedName1)
        assertEquals("مريومه", expectedName2)
    }
}
