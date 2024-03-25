package com.example.sound_sculpt_final

import android.widget.Button
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LinkFilesTest {

    private lateinit var activity: LinkFiles

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(LinkFiles::class.java)
            .create()
            .start()
            .resume()
            .get()
    }

    @Test
    fun testStartRecordingButton() {
        val startRecordingButton = activity.findViewById<Button>(R.id.startRecordingButton)
        startRecordingButton.performClick()
        assertEquals("Recording...", startRecordingButton.text)
    }

    @Test
    fun testStopRecordingButton() {
        val stopRecordingButton = activity.findViewById<Button>(R.id.stopRecordingButton)
        stopRecordingButton.performClick()
        assertEquals("Record", stopRecordingButton.text)
    }

    @Test
    fun testMaxDecibelTextView() {
        val maxDecibelTextView = activity.findViewById<TextView>(R.id.maxDecibelTextView)
        assertEquals("Decibel Value: 0.0", maxDecibelTextView.text)
    }

    @Test
    fun testTimerTextView() {
        val timerTextView = activity.findViewById<TextView>(R.id.timerTextView)
        assertEquals("00:00:00", timerTextView.text)
    }
}
