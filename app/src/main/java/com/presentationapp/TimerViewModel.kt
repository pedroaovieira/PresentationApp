package com.presentationapp

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class TimerPhase {
    SETUP,      // Screen where user sets the duration
    RUNNING,    // Timer is actively counting down
    PAUSED,     // Timer is paused mid-way
    FINISHED    // Timer reached zero
}

enum class TimerColor {
    GREEN,  // > 50% remaining
    YELLOW, // 20–50% remaining
    RED,    // <= 20% remaining
    FLASH   // Overtime / finished
}

data class TimerState(
    val totalSeconds: Long = 0L,
    val remainingMillis: Long = 0L,
    val phase: TimerPhase = TimerPhase.SETUP,
    val color: TimerColor = TimerColor.GREEN,
    val isFlashing: Boolean = false
)

class TimerViewModel : ViewModel() {

    private val _state = MutableLiveData(TimerState())
    val state: LiveData<TimerState> = _state

    private var countDownTimer: CountDownTimer? = null
    private var totalMillis: Long = 0L
    private var remainingMillis: Long = 0L

    // --- Setup ---

    fun setDuration(hours: Int, minutes: Int, seconds: Int) {
        totalMillis = ((hours * 3600L) + (minutes * 60L) + seconds) * 1000L
        remainingMillis = totalMillis
        _state.value = TimerState(
            totalSeconds = totalMillis / 1000L,
            remainingMillis = totalMillis,
            phase = TimerPhase.SETUP,
            color = TimerColor.GREEN
        )
    }

    // --- Controls ---

    fun start() {
        if (totalMillis <= 0L) return
        val currentPhase = _state.value?.phase ?: return
        if (currentPhase == TimerPhase.RUNNING) return

        launchTimer(remainingMillis)
    }

    fun pause() {
        countDownTimer?.cancel()
        _state.value = _state.value?.copy(phase = TimerPhase.PAUSED)
    }

    fun reset() {
        countDownTimer?.cancel()
        remainingMillis = totalMillis
        _state.value = TimerState(
            totalSeconds = totalMillis / 1000L,
            remainingMillis = totalMillis,
            phase = TimerPhase.SETUP,
            color = TimerColor.GREEN
        )
    }

    // --- Internal ---

    private fun launchTimer(fromMillis: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(fromMillis, 100) {

            override fun onTick(millisUntilFinished: Long) {
                remainingMillis = millisUntilFinished
                val color = computeColor(millisUntilFinished, totalMillis)
                _state.value = TimerState(
                    totalSeconds = totalMillis / 1000L,
                    remainingMillis = millisUntilFinished,
                    phase = TimerPhase.RUNNING,
                    color = color
                )
            }

            override fun onFinish() {
                remainingMillis = 0L
                _state.value = TimerState(
                    totalSeconds = totalMillis / 1000L,
                    remainingMillis = 0L,
                    phase = TimerPhase.FINISHED,
                    color = TimerColor.FLASH,
                    isFlashing = true
                )
            }
        }.start()

        _state.value = _state.value?.copy(phase = TimerPhase.RUNNING)
    }

    private fun computeColor(remainingMillis: Long, totalMillis: Long): TimerColor {
        if (totalMillis == 0L) return TimerColor.GREEN
        val fraction = remainingMillis.toDouble() / totalMillis.toDouble()
        return when {
            fraction > 0.50 -> TimerColor.GREEN
            fraction > 0.20 -> TimerColor.YELLOW
            else -> TimerColor.RED
        }
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
