package nl.jpelgrom.huetemp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import nl.jpelgrom.huetemp.databinding.FragmentAddBridgeBinding
import nl.jpelgrom.huetemp.viewmodels.AddBridgeViewModel

class AddBridgeFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentAddBridgeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddBridgeViewModel by viewModels()

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
        viewModel.state.observe(viewLifecycleOwner, Observer {
            when(it) {
                AddBridgeViewModel.CurrentState.PUSHLINK, AddBridgeViewModel.CurrentState.CONNECTED -> Log.i("AddBridgeFragment", "State: $it")
                else -> {
                    Log.i("AddBridgeFragment", "State: $it")
                    discover()
                }
            }
        })
    }

    private fun discover() {
        viewModel.foundBridges.observe(viewLifecycleOwner, Observer { it.map { item ->
            Log.i("AddBridgeFragment", "Discovered: $item")
        } })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}