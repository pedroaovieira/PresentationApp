package org.pedrov.presentationtimer

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import org.pedrov.presentationtimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TimerViewModel by viewModels()
    private lateinit var repository: PhasesRepository
    private var flashAnimation: AlphaAnimation? = null
    private var auraColorAnimator: ValueAnimator? = null
    private var currentAuraColor: Int = Color.parseColor("#5AF0B3")

    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.phases = repository.loadPhases()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        repository = PhasesRepository(this)
        viewModel.phases = repository.loadPhases()

        setupClickListeners()
        observeViewModel()
    }

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

        binding.btnPause.setOnClickListener { viewModel.pause() }

        binding.btnReset.setOnClickListener {
            stopFlashAnimation()
            viewModel.reset()
        }

        binding.btnSettings.setOnClickListener {
            settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            updateTimerDisplay(state)
            updateAccentColor(state)
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

        val progress = if (state.totalSeconds > 0) {
            ((state.remainingMillis.toFloat() / (state.totalSeconds * 1000f)) * 100).toInt()
        } else 100
        binding.progressArc.progress = progress
    }

    private fun updateAccentColor(state: TimerState) {
        val colorHex = state.activePhase?.colorHex ?: "#5AF0B3"
        val targetColor = Color.parseColor(colorHex)

        // Animate the aura bar colour cross-fade (500ms per design spec)
        if (targetColor != currentAuraColor) {
            auraColorAnimator?.cancel()
            auraColorAnimator = ValueAnimator.ofArgb(currentAuraColor, targetColor).apply {
                duration = 500
                addUpdateListener { anim ->
                    val c = anim.animatedValue as Int
                    binding.auraBar.setBackgroundColor(c)
                    binding.progressArc.setIndicatorColor(c)
                    binding.tvPhaseLabel.setTextColor(c)
                }
                start()
            }
            currentAuraColor = targetColor
        }

        if (state.isFlashing) startFlashAnimation() else stopFlashAnimation()

        binding.tvPhaseLabel.text = when (state.phase) {
            TimerPhase.SETUP    -> "READY"
            TimerPhase.RUNNING  -> (state.activePhase?.message ?: "ON TRACK").uppercase()
            TimerPhase.PAUSED   -> "PAUSED"
            TimerPhase.FINISHED -> "TIME'S UP"
        }
    }

    private fun updateButtons(state: TimerState) {
        binding.btnSettings.visibility = if (state.phase == TimerPhase.SETUP) View.VISIBLE else View.GONE

        // Show/hide timer area views
        val timerVisible = if (state.phase == TimerPhase.SETUP) View.GONE else View.VISIBLE
        binding.tvPhaseLabel.visibility = timerVisible
        binding.tvTimer.visibility = timerVisible
        binding.progressRow.visibility = timerVisible

        when (state.phase) {
            TimerPhase.SETUP -> {
                binding.setupPanel.visibility = View.VISIBLE
                binding.btnStart.text = "INITIALIZE"
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
                binding.btnStart.text = "▶  RESUME"
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

    private fun startFlashAnimation() {
        if (flashAnimation != null) return
        flashAnimation = AlphaAnimation(1f, 0.15f).apply {
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
        auraColorAnimator?.cancel()
    }
}
