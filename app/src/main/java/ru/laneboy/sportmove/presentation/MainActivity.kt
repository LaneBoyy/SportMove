package ru.laneboy.sportmove.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.fragment.app.Fragment
import ru.laneboy.sportmove.R
import ru.laneboy.sportmove.databinding.ActivityMainBinding
import ru.laneboy.sportmove.presentation.splash_screen.SplashScreenFragment
import kotlin.math.log2

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        removeStatusBar()
        Log.d("MainLog", "Round ${log2(0F)}")
        if (savedInstanceState == null) {
            setFragment(SplashScreenFragment.newInstance())
        }
//        setFragment(MatchesListForParticipantFragment.newInstance())
//        setFragment(GameDiagramFragment.newInstance())
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_main, fragment)
            .commit()
    }

    private fun removeStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}