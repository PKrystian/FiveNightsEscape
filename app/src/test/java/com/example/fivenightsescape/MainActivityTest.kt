package com.example.fivenightsescape

import android.widget.RadioGroup
import android.widget.Toast
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class MainActivityTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var mainActivity: MainActivity

    @Test
    fun testSetupStartButtonEasy() {
        val selectedDifficulty = EASY
        mainActivity.setupStartButton(selectedDifficulty)
    }

    @Test
    fun testSetupStartButtonMedium() {
        val selectedDifficulty = MEDIUM
        mainActivity.setupStartButton(selectedDifficulty)
    }

    @Test
    fun testSetupStartButtonHard() {
        val selectedDifficulty = HARD
        mainActivity.setupStartButton(selectedDifficulty)
    }

    @Test
    fun testStartMapsActivityEasy() {
        val selectedDifficulty = EASY
        mainActivity.startMapsActivity(selectedDifficulty)
    }

    @Test
    fun testStartMapsActivityMedium() {
        val selectedDifficulty = MEDIUM
        mainActivity.startMapsActivity(selectedDifficulty)
    }

    @Test
    fun testStartMapsActivityHard() {
        val selectedDifficulty = HARD
        mainActivity.startMapsActivity(selectedDifficulty)
    }

    @Test
    fun testToastError() {
        ToastMock()
        mainActivity.toastError()
    }

    private class RadioGroupMock : RadioGroup(null) {
        var setOnCheckedChangeListenerCalled = false
        override fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
            setOnCheckedChangeListenerCalled = true
        }
    }

    private class ToastMock : Toast(null) {
        var showToastCalled = false
        override fun show() {
            showToastCalled = true
        }
    }
}
