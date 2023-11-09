package ru.laneboy.sportmove.presentation.splash_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.laneboy.sportmove.R
import ru.laneboy.sportmove.databinding.FragmentSplashScreenBinding
import ru.laneboy.sportmove.presentation.sign_in.SignInFragment

class SplashScreenFragment : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding: FragmentSplashScreenBinding
        get() = _binding ?: throw RuntimeException("FragmentSplashScreenBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            delay(2500)
            launchSignInFragment()
        }
    }

    private fun launchSignInFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_main, SignInFragment.newInstance())
            .commit()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = SplashScreenFragment()
    }
}