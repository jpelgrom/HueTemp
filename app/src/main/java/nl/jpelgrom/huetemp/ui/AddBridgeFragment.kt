package nl.jpelgrom.huetemp.ui

import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.transition.MaterialSharedAxis
import nl.jpelgrom.huetemp.databinding.FragmentAddBridgeBinding
import nl.jpelgrom.huetemp.ui.adapter.DiscoveredBridgesAdapter
import nl.jpelgrom.huetemp.viewmodels.AddBridgeViewModel

class AddBridgeFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentAddBridgeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddBridgeViewModel by viewModels()

    private var lastState: AddBridgeViewModel.CurrentState? = null

    private lateinit var adapter: DiscoveredBridgesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBridgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DiscoveredBridgesAdapter(emptyList()) { bridge ->
            Log.i("AddBridgeFragment", "Starting pushlink for $bridge")
            viewModel.startPushlink(bridge, context)
        }
        binding.searchingList.adapter = adapter
        binding.searchingList.layoutManager = LinearLayoutManager(context)

        viewModel.currentState.observe(viewLifecycleOwner, Observer {
            transitionState(it)
            when (it) {
                AddBridgeViewModel.CurrentState.PUSHLINK -> {
                    binding.bridgeSearching.visibility = View.GONE
                    binding.bridgePushlink.visibility = View.VISIBLE
                    binding.bridgeConnected.visibility = View.GONE
                }
                AddBridgeViewModel.CurrentState.CONNECTED -> {
                    binding.bridgeSearching.visibility = View.GONE
                    binding.bridgePushlink.visibility = View.GONE
                    binding.bridgeConnected.visibility = View.VISIBLE
                }
                AddBridgeViewModel.CurrentState.CLOSE -> {
                    dismiss()
                }
                else -> {
                    binding.bridgeSearching.visibility = View.VISIBLE
                    binding.bridgePushlink.visibility = View.GONE
                    binding.bridgeConnected.visibility = View.GONE

                    discover()
                }
            }

            lastState = it
        })

        binding.pushlinkCancel.setOnClickListener {
            viewModel.cancelPushlink()
        }
    }

    private fun transitionState(newState: AddBridgeViewModel.CurrentState) {
        if (lastState != null) {
            val forward =
                (lastState == AddBridgeViewModel.CurrentState.SEARCHING && newState == AddBridgeViewModel.CurrentState.PUSHLINK) || (lastState == AddBridgeViewModel.CurrentState.PUSHLINK && newState == AddBridgeViewModel.CurrentState.CONNECTED)
            val sharedAxis =
                MaterialSharedAxis.create(MaterialSharedAxis.X, forward)
            TransitionManager.beginDelayedTransition(binding.bridgeContainer, sharedAxis)
        }
    }

    private fun discover() { // TODO cancel on state change
        viewModel.foundBridges.observe(viewLifecycleOwner, Observer {
            adapter.swap(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}