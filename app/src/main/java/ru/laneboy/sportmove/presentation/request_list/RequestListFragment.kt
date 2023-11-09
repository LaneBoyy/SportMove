package ru.laneboy.sportmove.presentation.request_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import ru.laneboy.sportmove.databinding.FragmentRequestListBinding
import ru.laneboy.sportmove.presentation.request_list.adapter.RequestAdapter
import ru.laneboy.sportmove.util.gone
import ru.laneboy.sportmove.util.initProgressBar
import ru.laneboy.sportmove.util.visible

class RequestListFragment : Fragment() {

    private var _binding: FragmentRequestListBinding? = null
    private val binding: FragmentRequestListBinding
        get() = _binding ?: throw RuntimeException("FragmentRequestListBinding == null")

    private val dialog by lazy {
        initProgressBar(layoutInflater, requireContext())
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[RequestViewModel::class.java]
    }

    private val isUserAccount by lazy {
        arguments?.getBoolean(IS_USER_ACCOUNT)!!
    }

    private val requestAdapter by lazy {
        RequestAdapter(isUserAccount)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isUserAccount) {
            viewModel.loadRequestList()
        } else {
            viewModel.loadCompetitionRequests(arguments?.getInt(COMPETITION_ID)!!)
        }
        setObservers()
        setUI()
    }

    private fun setObservers() {
        viewModel.requestList.observe(viewLifecycleOwner) { list ->
            binding.root.isRefreshing = false
            list.ifLoading {
                dialog.show()
            }.ifError {
                dialog.dismiss()
            }.ifSuccess {
                dialog.dismiss()
                if(it?.isNotEmpty() == true){
                    binding.rvRequests.visible()
                    binding.tvEmptyList.gone()
                    requestAdapter.setList(it)
                }else{
                    binding.rvRequests.gone()
                    binding.tvEmptyList.visible()
                }
            }
        }
    }

    private fun setUI() {
        binding.root.setOnRefreshListener {
            if (isUserAccount) {
                viewModel.loadRequestList()
            } else {
                viewModel.loadCompetitionRequests(arguments?.getInt(COMPETITION_ID)!!)
            }
        }
        requestAdapter.onRequestClick = { requestId: Int, permission: Boolean ->
            viewModel.setRequestDecide(requestId, permission)
        }
        binding.rvRequests.adapter = requestAdapter
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val IS_USER_ACCOUNT = "IS_USER_ACCOUNT"
        private const val COMPETITION_ID = "COMPETITION_ID"

        fun newInstance(isUserAccount: Boolean, competitionId: Int?) = RequestListFragment().apply {
            arguments = bundleOf(IS_USER_ACCOUNT to isUserAccount, COMPETITION_ID to competitionId)
        }
    }
}