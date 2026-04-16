package com.presentationapp

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.presentationapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TimerViewModel by viewModels()

    private var flashAnimation: AlphaAnimation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Keep screen on during presentations
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setupClickListeners()
        observeViewModel()
    }

    // ── Click listeners ──────────────────────────────────────────────────────

    private fun setupClickListeners() {
        binding.btnStart.setOnClickListener {
            val phase = viewModel.state.value?.phase
            if (phase == TimerPhase.SETUP || phase == TimerPhase.PAUSED) {
                if (phase == TimerPhase.SETUP) {
                    val h = binding.inputHours.text.toString().toIntOrNull() ?: 0
                    val m = binding.inputMinutes.text.toString().toIntOrNull() ?: 0
                    val s = binding.inputSeconds.text.toString().toIntOrNull() ?: 0
                    viewModel.setDuration(h, m, s)
                }
                viewModel.start()
            }
        }

        binding.btnPause.setOnClickListener {
            viewModel.pause()
        }

        binding.btnReset.setOnClickListener {
            stopFlashAnimation()
            viewModel.reset()
        }
    }

    // ── ViewModel observation ────────────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            updateTimerDisplay(state)
            updateBackground(state)
            updateButtons(state)
        }
    }

    private fun updateTimerDisplay(state: TimerState) {
        val totalSecs = state.remainingMillis / 1000L
        val hours = totalSecs / 3600
        val minutes = (totalSecs % 3600) / 60
        val seconds = totalSecs % 60

        binding.tvTimer.text = if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }

        // Progress arc
        val progress = if (state.totalSeconds > 0) {
            ((state.remainingMillis.toFloat() / (state.totalSeconds * 1000f)) * 100).toInt()
        } else 100
        binding.progressArc.progress = progress
    }

    private fun updateBackground(state: TimerState) {
        val (bgColor, textColor, arcColor) = when (state.color) {
            TimerColor.GREEN -> Triple(
                R.color.bg_green, R.color.text_on_color, R.color.arc_green
            )
            TimerColor.YELLOW -> Triple(
                R.color.bg_yellow, R.color.text_on_yellow, R.color.arc_yellow
            )
            TimerColor.RED -> Triple(
                R.color.bg_red, R.color.text_on_color, R.color.arc_red
            )
            TimerColor.FLASH -> Triple(
                R.color.bg_red, R.color.text_on_color, R.color.arc_red
            )
        }

        binding.rootLayout.setBackgroundColor(ContextCompat.getColor(this, bgColor))
        binding.tvTimer.setTextColor(ContextCompat.getColor(this, textColor))
        binding.progressArc.setIndicatorColor(ContextCompat.getColor(this, arcColor))

        if (state.isFlashing) {
            startFlashAnimation()
        } else {
            stopFlashAnimation()
        }

        // Label
        binding.tvPhaseLabel.text = when (state.phase) {
            TimerPhase.SETUP -> "Set your time"
            TimerPhase.RUNNING -> when (state.color) {
                TimerColor.GREEN -> "On track 🟢"
                TimerColor.YELLOW -> "Hurry up! 🟡"
                TimerColor.RED -> "Almost out of time! 🔴"
                TimerColor.FLASH -> "Time's up! ⏰"
            }
            TimerPhase.PAUSED -> "Paused ⏸"
            TimerPhase.FINISHED -> "Time's up! ⏰"
        }
    }

    private fun updateButtons(state: TimerState) {
        when (state.phase) {
            TimerPhase.SETUP -> {
                binding.setupPanel.visibility = View.VISIBLE
                binding.btnStart.text = "Start"
                binding.btnStart.visibility = View.VISIBLE
                binding.btnPause.visibility = View.GONE
                binding.btnReset.visibility = View.GONE
            }
            TimerPhase.RUNNING -> {
                binding.setupPanel.visibility = View.GONE
                binding.btnStart.visibility = View.GONE
                binding.btnPause.visibility = View.VISIBLE
                binding.btnReset.visibility = View.VISIBLE
            }
            TimerPhase.PAUSED -> {
                binding.setupPanel.visibility = View.GONE
                binding.btnStart.text = "Resume"
                binding.btnStart.visibility = View.VISIBLE
                binding.btnPause.visibility = View.GONE
                binding.btnReset.visibility = View.VISIBLE
            }
            TimerPhase.FINISHED -> {
                binding.setupPanel.visibility = View.GONE
                binding.btnStart.visibility = View.GONE
                binding.btnPause.visibility = View.GONE
                binding.btnReset.visibility = View.VISIBLE
            }
        }
    }

    // ── Flash animation ──────────────────────────────────────────────────────

    private fun startFlashAnimation() {
        if (flashAnimation != null) return
        flashAnimation = AlphaAnimation(1f, 0.2f).apply {
            duration = 600
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.tvTimer.startAnimation(flashAnimation)
    }

    private fun stopFlashAnimation() {
        flashAnimation?.let {
            binding.tvTimer.clearAnimation()
            flashAnimation = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopFlashAnimation()
    }
}
