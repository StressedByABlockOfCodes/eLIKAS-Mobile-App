package com.example.elikas.ui.sms.fragment.BarangayCaptain

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.elikas.MainApplication
import com.example.elikas.adapter.NonEvacueesAdapter
import com.example.elikas.databinding.FragmentBcViewNonevacueesBinding
import com.example.elikas.viewmodel.ResidentViewModelFactory
import com.example.elikas.viewmodel.ResidentsViewModel


class BCViewNonEvacueesFragment : Fragment() {

    private var mcontext: Context? = null
    private var _binding: FragmentBcViewNonevacueesBinding? = null

    private val viewModel: ResidentsViewModel by viewModels {
        ResidentViewModelFactory((requireActivity().application as MainApplication).repository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBcViewNonevacueesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       /* val recyclerView = binding.recyclerViewNonEvacuees
        val adapter = NonEvacueesAdapter()
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(mcontext)

        //get the data from db and pass to the recyclerview
        subscribeUi(adapter)*/


    }

    private fun subscribeUi(adapter: NonEvacueesAdapter) {
        viewModel.allResidents.observe(viewLifecycleOwner) { residents ->
            adapter.submitList(residents)
        }

        //Log.i("getResidents", viewModel.getResidentsByFamCode("eLIKAS-JRKCsR").toString())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mcontext = context
    }

    override fun onDetach() {
        super.onDetach()
        mcontext = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}