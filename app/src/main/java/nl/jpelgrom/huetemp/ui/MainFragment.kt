package nl.jpelgrom.huetemp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import nl.jpelgrom.huetemp.databinding.FragmentMainBinding
import nl.jpelgrom.huetemp.viewmodels.MainViewModel

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addBridgeButton.setOnClickListener {
            this.findNavController().navigate(
                MainFragmentDirections.actionAddBridge()
            )
        }
        binding.runWorkerButton.setOnClickListener { viewModel.runWorker(this.context) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}