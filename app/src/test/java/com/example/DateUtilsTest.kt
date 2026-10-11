package com.example

import com.example.ui.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DateUtilsTest {

    @Test
    fun testFormatMediaDate_validTimestamp() {
        // Timestamp for 2024-05-15 UTC
        val ts = 1715774400000L
        val formatted = DateUtils.formatMediaDate(ts, null, "yyyy-MM")
        assertEquals("2024-05", formatted)
    }

    @Test
    fun testGetLocalTimeMs_secondsConversion() {
        val seconds = 1715774400L
        val ms = DateUtils.getLocalTimeMs(seconds, null)
        assertEquals(1715774400000L, ms)
    }

    @Test
    fun testGetLocalTimeMs_withOffset() {
        val msBase = 1715774400000L
        val offset = "+02:00"
        val localMs = DateUtils.getLocalTimeMs(msBase, offset)
        // Expected base + 2 hours (2 * 3600 * 1000 = 7,200,000)
        assertEquals(msBase + 7200000L, localMs)
    }

    @Test
    fun testGetLocalTimeMs_invalidTimestampReturnsZero() {
        assertEquals(0L, DateUtils.getLocalTimeMs(null, null))
        assertEquals(0L, DateUtils.getLocalTimeMs(-100L, null))
    }
}
