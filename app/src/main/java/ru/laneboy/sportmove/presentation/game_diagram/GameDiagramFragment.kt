package ru.laneboy.sportmove.presentation.game_diagram

import android.animation.Animator
import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import ru.laneboy.sportmove.R
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.laneboy.sportmove.databinding.FragmentGameDiagramBinding
import ru.laneboy.sportmove.presentation.game_counter.GameCounterFragment
import ru.laneboy.sportmove.util.gone
import ru.laneboy.sportmove.util.initProgressBar
import ru.laneboy.sportmove.util.invisible
import ru.laneboy.sportmove.util.showToast
import ru.laneboy.sportmove.util.visible


class GameDiagramFragment : Fragment() {

    private var _binding: FragmentGameDiagramBinding? = null
    private val binding: FragmentGameDiagramBinding
        get() = _binding ?: throw RuntimeException("FragmentGameDiagramBinding == null")

    private val viewModel by lazy {
        ViewModelProvider(this)[GameDiagramViewModel::class.java]
    }

    private val dialog by lazy {
        initProgressBar(layoutInflater, requireContext())
    }

    private val competitionId by lazy {
        arguments?.getInt(COMPETITION_ID)!!
    }


    private val isAdmin by lazy {
        arguments?.getBoolean(IS_ADMIN)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameDiagramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setUI()
    }


    private fun setObservers() {
        viewModel.setup(competitionId)
        viewModel.loadGameState()
        viewModel.gameDiagram.observe(viewLifecycleOwner) {
            it.ifLoading {
                dialog.show()
            }.ifError {
                dialog.dismiss()
                binding.zoom.gone()
                binding.flStartMatch.visible()
                binding.flStartMatchActive.gone()
                binding.gameNotStarted.gone()
                isEnd = false
                showToast(it.message)
            }.ifSuccess {
                dialog.dismiss()
                it?.let {
                    binding.flStartMatch.gone()
                    binding.flStartMatchActive.gone()
                    binding.gameNotStarted.gone()
                    binding.zoom.invisible()
                    binding.zoom.post {
                        binding.zoom.moveTo(0.8f, 0f, 0f, false)
                        binding.zoom.visible()
                    }
                    binding.diagramView.updateGameDiagram(it)
                }
            }
        }
        viewModel.gameState.observe(viewLifecycleOwner) { response ->
            response.ifLoading {
                dialog.show()
            }.ifError {
                dialog.dismiss()
                showToast(it.message)
            }.ifSuccess { isStart ->
                if (isStart!!) {
                    binding.zoom.visible()
                    binding.flStartMatch.gone()
                    binding.flStartMatchActive.gone()
                    binding.gameNotStarted.gone()
                    isEnd = true
                } else {
                    dialog.dismiss()
                    binding.zoom.gone()
                    if (isAdmin) {
                        binding.flStartMatch.visible()
                        binding.flStartMatchActive.gone()
                        isEnd = false
                    } else {
                        binding.gameNotStarted.visible()
                        binding.flStartMatch.gone()
                        binding.flStartMatchActive.gone()
                    }
                }
            }

        }
    }

    private fun setUI() {
        setupAnimation {
            viewModel.generateGames()
        }
        binding.diagramView.onGameClick = {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_enter_left,
                    R.anim.slide_exit_left,
                    R.anim.slide_enter_right,
                    R.anim.slide_exit_right
                )
                .addToBackStack(null)
                .replace(
                    R.id.fragment_container_main,
                    GameCounterFragment.newInstance(it, isAdmin)
                )
                .commit()
        }
    }

    private var isEnd = false

    @SuppressLint("ClickableViewAccessibility")
    private fun setupAnimation(onAccept: (() -> Unit)) {
        var isCancel = false
        var isEnd = false
        val vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val scaleOut = AnimatorInflater.loadAnimator(requireContext(), R.animator.scale_out).apply {
            setTarget(binding.flStartMatchActive)
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    binding.flStartMatchActive.visible()
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!isCancel) {
                        isEnd = true
                        vibrator.vibrate(500)
                        onAccept.invoke()
                    }
                    isCancel = false
                }

                override fun onAnimationCancel(animation: Animator) {
                    binding.flStartMatchActive.gone()
                    binding.flStartMatchActive.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                    )
                }

                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
        binding.flStartMatch.setOnTouchListener { v, event ->
            if (event.action != MotionEvent.ACTION_UP && event.action != MotionEvent.ACTION_CANCEL) {
                if (!scaleOut.isRunning && !isEnd) {
                    scaleOut.start()
                }
            } else {
                isCancel = true
                scaleOut.cancel()
            }
            return@setOnTouchListener true
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        private const val IS_ADMIN = "IS_ADMIN"
        private const val COMPETITION_ID = "COMPETITION_ID"
        fun newInstance(isAdmin: Boolean, competitionId: Int) = GameDiagramFragment().apply {
            arguments = bundleOf(COMPETITION_ID to competitionId, IS_ADMIN to isAdmin)
        }

//        val bigGame = GameDiagram(
//            firstTeam = "Vlad",
//            secondTeam = "Rodion",
//            previousTopMatch = GameDiagram(
//                firstTeam = "Vlad",
//                secondTeam = "Vanya",
//                previousTopMatch = GameDiagram(
//                    firstTeam = "Vlad",
//                    secondTeam = "Misha",
//                    previousTopMatch = GameDiagram(
//                        firstTeam = "Vlad",
//                        secondTeam = "Veronika",
//                        previousTopMatch = null,
//                        previousBottomMatch = null
//                    ),
//                    previousBottomMatch = GameDiagram(
//                        firstTeam = "Misha",
//                        secondTeam = "Valya",
//                        previousTopMatch = null,
//                        previousBottomMatch = null
//                    )
//                ),
//                previousBottomMatch = GameDiagram(
//                    firstTeam = "Vanya",
//                    secondTeam = "Ilnar",
//                    previousTopMatch = GameDiagram(
//                        firstTeam = "Vanya",
//                        secondTeam = "Sofya",
//                        previousTopMatch = null,
//                        previousBottomMatch = null
//                    ),
//                    previousBottomMatch = GameDiagram(
//                        firstTeam = "Ilnar",
//                        secondTeam = "Katya",
//                        previousTopMatch = null,
//                        previousBottomMatch = null
//                    )
//                )
//            ),
//            previousBottomMatch = GameDiagram(
//                "Rodion",
//                "Sasha",
//                previousTopMatch = GameDiagram(
//                    "Vlad Kuz.",
//                    "Rodion",
//                    previousTopMatch = GameDiagram(
//                        firstTeam = "Vlad Kuz.",
//                        secondTeam = "Masha",
//                        previousTopMatch = null,
//                        previousBottomMatch = null
//                    ),
//                    previousBottomMatch = GameDiagram(
//                        firstTeam = "Rodion",
//                        secondTeam = "Masha",
//                        previousTopMatch = null,
//                        previousBottomMatch = null
//                    )
//                ),
//                previousBottomMatch = GameDiagram(
//                    "Sasha",
//                    secondTeam = "Ilnaz",
//                    previousTopMatch = GameDiagram(
//                        firstTeam = "Ilnaz",
//                        secondTeam = "Nastya",
//                        previousTopMatch = null,
//                        previousBottomMatch = null
//                    ),
//                    previousBottomMatch = GameDiagram(
//                        firstTeam = "Sasha",
//                        secondTeam = "Lera",
//                        previousTopMatch = null,
//                        previousBottomMatch = null
//                    )
//                )
//            )
//        )
//
//        val middleGame = GameDiagram(
//            firstTeam = "Vlad",
//            secondTeam = "Rodion",
//            previousTopMatch = GameDiagram(
//                firstTeam = "Vlad",
//                secondTeam = "Misha",
//                previousTopMatch = GameDiagram(
//                    firstTeam = "Vlad",
//                    secondTeam = "Veronika",
//                    previousTopMatch = null,
//                    previousBottomMatch = null
//                ),
//                previousBottomMatch = GameDiagram(
//                    firstTeam = "Misha",
//                    secondTeam = "Valya",
//                    previousTopMatch = null,
//                    previousBottomMatch = null
//                )
//            ),
//            previousBottomMatch = GameDiagram(
//                firstTeam = "Rodion",
//                secondTeam = "Vanya",
//                previousTopMatch = GameDiagram(
//                    firstTeam = "Rodion",
//                    secondTeam = "Masha",
//                    previousTopMatch = null,
//                    previousBottomMatch = null
//                ),
//                previousBottomMatch = GameDiagram(
//                    firstTeam = "Vanya",
//                    secondTeam = "Sofya",
//                    previousTopMatch = null,
//                    previousBottomMatch = null
//                )
//            )
//        )
//        val game = GameDiagram(
//            firstTeam = "Vlad",
//            secondTeam = "Misha",
//            previousTopMatch = GameDiagram(
//                firstTeam = "Vlad",
//                secondTeam = "Veronika",
//                previousTopMatch = null,
//                previousBottomMatch = null
//            ),
//            previousBottomMatch = GameDiagram(
//                firstTeam = "Misha",
//                secondTeam = "Valya",
//                previousTopMatch = null,
//                previousBottomMatch = null
//            )
//        )
    }
}