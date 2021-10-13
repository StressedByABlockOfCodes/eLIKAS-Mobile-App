package com.example.elikas.ui.sms.fragment.BarangayCaptain

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.elikas.R
import com.example.elikas.databinding.FragmentBcAddSupplyBinding
import com.example.elikas.utils.SMSUtil
import com.example.elikas.utils.SharedPreferenceUtil


class BCAddFragment : Fragment() {

    private var mcontext: Context? = null
    private var _binding: FragmentBcAddSupplyBinding? = null
    private var selectedItem: Any? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBcAddSupplyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = SharedPreferenceUtil.getUser(view.context)
        val sms = SMSUtil(view.context)

        ArrayAdapter.createFromResource(
            view.context,
            R.array.supply_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spinner.adapter = adapter
        }
        binding.spinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                selectedItem = parent.getItemAtPosition(pos)
                Log.i(TAG, selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        })

        binding.btnAdd.setOnClickListener {
            val finalItem = selectedItem ?: ""
            val message = "addSupply,${user.id},$finalItem,${binding.txtQuantity.text},${binding.txtSource.text}"
            Log.i(TAG, message)
            sms.send(message)

            view.findNavController().navigate(R.id.navigation_home)
        }

        binding.btnCancel.setOnClickListener {
            view.findNavController().navigate(R.id.navigation_home)
        }
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

    companion object{
        const val TAG = "BCAddFragment"
    }
}