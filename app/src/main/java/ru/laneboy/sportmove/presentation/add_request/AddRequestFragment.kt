package ru.laneboy.sportmove.presentation.add_request

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import ru.laneboy.sportmove.databinding.FragmentAddRequestBinding
import ru.laneboy.sportmove.util.initProgressBar
import ru.laneboy.sportmove.util.showToast

class AddRequestFragment : Fragment() {

    private var _binding: FragmentAddRequestBinding? = null
    private val binding: FragmentAddRequestBinding
        get() = _binding ?: throw RuntimeException("FragmentAddRequestBinding == null")

    private val viewModel by lazy {
        ViewModelProvider(this)[AddRequestViewModel::class.java]
    }

    private val competitionId by lazy {
        arguments?.getInt(COMPETITION_ID)!!
    }

    private val dialog by lazy {
        initProgressBar(layoutInflater, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setUI()
    }

    private fun setObservers() {
        viewModel.request.observe(viewLifecycleOwner) { request ->
            request.ifLoading {
                dialog.show()
            }.ifError {
                dialog.dismiss()
                showToast(it.message)
            }.ifSuccess {
                dialog.dismiss()
                showToast("Заявка успешно создана")
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun setUI() {
        binding.btnAddRequest.setOnClickListener {
            viewModel.addRequest(
                competitionId,
                binding.etTeamName.text?.toString(),
                binding.etCaptain.text?.toString()
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        private const val COMPETITION_ID = "COMPETITION_ID"

        fun newInstance(competitionId: Int) = AddRequestFragment().apply {
            arguments = bundleOf(COMPETITION_ID to competitionId)
        }
    }
}