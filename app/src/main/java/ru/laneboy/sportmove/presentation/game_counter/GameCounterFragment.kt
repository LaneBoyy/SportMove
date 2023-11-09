package ru.laneboy.sportmove.presentation.game_counter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import ru.laneboy.sportmove.data.network.responses.GameDiagramRequest
import ru.laneboy.sportmove.databinding.FragmentGameCounterBinding
import ru.laneboy.sportmove.domain.GameDiagram
import ru.laneboy.sportmove.util.gone
import ru.laneboy.sportmove.util.initProgressBar
import ru.laneboy.sportmove.util.showToast
import ru.laneboy.sportmove.util.visible

class GameCounterFragment : Fragment() {

    private var _binding: FragmentGameCounterBinding? = null
    private val binding: FragmentGameCounterBinding
        get() = _binding ?: throw RuntimeException("FragmentGameCounterBinding == null")

    private val dialog by lazy {
        initProgressBar(layoutInflater, requireContext())
    }

    private val gameDiagram by lazy {
        arguments?.getSerializable(GAME_DIAGRAM) as GameDiagram
    }


    private val isAdmin by lazy {
        arguments?.getBoolean(IS_ADMIN)!!
    }

    private val viewModel by lazy {
        ViewModelProvider(this, GameViewModelFactory(gameDiagram))[GameCounterViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameCounterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        binding.root.setOnRefreshListener {
            viewModel.loadGame()
        }
    }

    private fun setObservers() {
        viewModel.game.observe(viewLifecycleOwner) { gameDiagramResource ->
            gameDiagramResource.ifLoading {
                dialog.show()
            }.ifError {
                dialog.dismiss()
                binding.root.isRefreshing = false
                showToast(it.message)
            }.ifSuccess { game ->
                binding.root.isRefreshing = false
                game?.let {
                    setUI(it)
                }
            }
        }
        viewModel.gameResult.observe(viewLifecycleOwner) {
            it.ifLoading {
                dialog.show()
            }.ifError {
                dialog.dismiss()
                showToast(it.message)
            }.ifSuccess {
                dialog.dismiss()
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun setUI(gameDiagram: GameDiagramRequest) {
        binding.tvTeamName1.text = gameDiagram.gameData!!.firstTeam
        binding.tvTeamName2.text = gameDiagram.gameData.secondTeam
        binding.tvCounterTeam1.text = gameDiagram.gameData.firstTeamScore.toString()
        binding.tvCounterTeam2.text = gameDiagram.gameData.secondTeamScore.toString()
        if (isAdmin) {
            if (!gameDiagram.gameData.gameIsEnd) {
                binding.btnEndGame.visible()
                binding.btnTeamPlus1.setOnClickListener {
                    viewModel.changeScore(true, true)
                }
                binding.btnTeamPlus2.setOnClickListener {
                    viewModel.changeScore(false, true)

                }
                binding.btnTeamMinus1.setOnClickListener {
                    viewModel.changeScore(true, false)

                }
                binding.btnTeamMinus2.setOnClickListener {
                    viewModel.changeScore(false, false)
                }
                binding.btnEndGame.setOnClickListener {
                    viewModel.endGame()
                }
            } else {
                binding.btnEndGame.gone()
            }
        }else{
            binding.btnEndGame.gone()
            binding.btnTeamPlus1.gone()
            binding.btnTeamPlus2.gone()
            binding.btnTeamMinus1.gone()
            binding.btnTeamMinus2.gone()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        private const val GAME_DIAGRAM = "GAME_DIAGRAM"
        private const val IS_ADMIN = "IS_ADMIN"

        fun newInstance(gameDiagram: GameDiagram, isAdmin: Boolean) = GameCounterFragment().apply {
            arguments = bundleOf(GAME_DIAGRAM to gameDiagram, IS_ADMIN to isAdmin)
        }
    }
}