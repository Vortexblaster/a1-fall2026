package com.example.pacak_rapidrecall

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Checks that the installed app has the expected package name.
 *
 * This uses AndroidJUnit4 to get the app context on a device, which is not
 * available in a regular local unit test.
 *
 * Known issues: it needs an emulator or phone to run. It does not test the UI,
 * gameplay, saved attempts, or what happens when the activity is recreated.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.pacak_rapidrecall", appContext.packageName)
    }
}