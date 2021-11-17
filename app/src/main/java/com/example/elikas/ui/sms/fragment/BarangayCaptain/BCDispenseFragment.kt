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
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.elikas.MainApplication
import com.example.elikas.R
import com.example.elikas.data.DisasterResponse
import com.example.elikas.data.Resident
import com.example.elikas.data.User
import com.example.elikas.databinding.FragmentDispenseBinding
import com.example.elikas.utils.SMSUtil
import com.example.elikas.utils.SharedPreferenceUtil
import com.example.elikas.viewmodel.DisasterResponseViewModel
import com.example.elikas.viewmodel.DisasterResponseViewModelFactory
import com.example.elikas.viewmodel.ResidentViewModelFactory
import com.example.elikas.viewmodel.ResidentsViewModel


class BCDispenseFragment : Fragment() {

    private var mcontext: Context? = null
    private var _binding: FragmentDispenseBinding? = null
    private lateinit var DisasterResponseList: List<DisasterResponse>
    private lateinit var ResidentsList: List<Resident>
    private lateinit var user: User
    private lateinit var sms: SMSUtil
    private var selectedDR: DisasterResponse? = null
    private var selectedResident: Resident? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: ResidentsViewModel by viewModels {
        ResidentViewModelFactory((requireActivity().application as MainApplication).repository)
    }

    private val viewModelDR: DisasterResponseViewModel by viewModels {
        DisasterResponseViewModelFactory((requireActivity().application as MainApplication).repositoryDR)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDispenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = SharedPreferenceUtil.getUser(view.context)
        sms = SMSUtil(view.context)

        //SELECTED DISASTER RESPONSE
        val drAdapter = ArrayAdapter<DisasterResponse>(
            view.context,
            android.R.layout.simple_spinner_item
        )
        drAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewModelDR.allDisasterResponses.observe(viewLifecycleOwner) { disaster_responses ->
            DisasterResponseList = disaster_responses
            disaster_responses?.forEach { drAdapter.add(it) }
            drAdapter.notifyDataSetChanged()
            binding.spinnerDr.adapter = drAdapter

        }
        binding.spinnerDr.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                selectedDR = DisasterResponseList[pos]

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        })

        //SELECTED FAMILY HEAD
        val residentsAdapter = ArrayAdapter<Resident>(
            view.context,
            android.R.layout.simple_spinner_item
        )
        residentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewModel.getNonEvacuees().observe(viewLifecycleOwner) { residents ->
            ResidentsList = residents
            residents?.forEach { residentsAdapter.add(it) }
            residentsAdapter.notifyDataSetChanged()
            binding.spinnerFamilyRep.adapter = residentsAdapter

        }
        binding.spinnerFamilyRep.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                selectedResident = ResidentsList[pos]

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        })

        binding.btnAdmit.setOnClickListener {
            val foodPacks = binding.edTextFoodPacks.text.toString().trim()
            val water = binding.edTextWater.text.toString().trim()
            val hygieneKit = binding.edTextHygieneKit.text.toString().trim()
            val medicine = binding.edTextMedicine.text.toString().trim()
            val clothes = binding.edTextClothes.text.toString().trim()
            val esa = binding.edTextESA.text.toString().trim()

            val finalDR = selectedDR?.id ?: ""
            val finalResident = selectedResident?.id ?: ""

            var message = "dispense,${user.id},$finalDR,$finalResident,"
            message += "$foodPacks,$water,$hygieneKit,$medicine,$clothes,$esa"
            Log.i(TAG, message)
            sms.send(message)
            view.findNavController().navigate(R.id.navigation_home)
        }

        binding.btnCancel.setOnClickListener {
            view.findNavController().navigate(R.id.navigation_home)
        }

        addButtonsInit()
        minusdButtonsInit()

    }

    private fun addButtonsInit() {
        binding.btnAddFoodPacks.setOnClickListener {
            val num = binding.edTextFoodPacks.text.toString().toInt() + 1
            binding.edTextFoodPacks.setText(num.toString())
        }
        binding.btnAddWater.setOnClickListener {
            val num = binding.edTextWater.text.toString().toInt() + 1
            binding.edTextWater.setText(num.toString())
        }
        binding.btnAddClothes.setOnClickListener {
            val num = binding.edTextClothes.text.toString().toInt() + 1
            binding.edTextClothes.setText(num.toString())
        }
        binding.btnAddHygieneKit.setOnClickListener {
            val num = binding.edTextHygieneKit.text.toString().toInt() + 1
            binding.edTextHygieneKit.setText(num.toString())
        }
        binding.btnAddMedicine.setOnClickListener {
            val num = binding.edTextMedicine.text.toString().toInt() + 1
            binding.edTextMedicine.setText(num.toString())
        }
        binding.btnAddESA.setOnClickListener {
            val num = binding.edTextESA.text.toString().toInt() + 1
            binding.edTextESA.setText(num.toString())
        }
    }

    private fun minusdButtonsInit() {
        binding.btnMinusFoodPacks.setOnClickListener {
            var num = binding.edTextFoodPacks.text.toString().toInt()
            if(binding.edTextFoodPacks.text.toString().toInt() > 0) {
                num -= 1
                binding.edTextFoodPacks.setText(num.toString())
            }
        }
        binding.btnMinusWater.setOnClickListener {
            var num = binding.edTextWater.text.toString().toInt()
            if(binding.edTextWater.text.toString().toInt() > 0) {
                num -= 1
                binding.edTextWater.setText(num.toString())
            }
        }
        binding.btnMinusClothes.setOnClickListener {
            var num = binding.edTextClothes.text.toString().toInt()
            if(binding.edTextClothes.text.toString().toInt() > 0) {
                num -= 1
                binding.edTextClothes.setText(num.toString())
            }
        }
        binding.btnMinusHygieneKit.setOnClickListener {
            var num = binding.edTextHygieneKit.text.toString().toInt()
            if(binding.edTextHygieneKit.text.toString().toInt() > 0) {
                num -= 1
                binding.edTextHygieneKit.setText(num.toString())
            }

        }
        binding.btnMinusMedicine.setOnClickListener {
            var num = binding.edTextMedicine.text.toString().toInt()
            if(binding.edTextMedicine.text.toString().toInt() > 0) {
                num -= 1
                binding.edTextMedicine.setText(num.toString())
            }
        }
        binding.btnMinusESA.setOnClickListener {
            var num = binding.edTextESA.text.toString().toInt()
            if(binding.edTextESA.text.toString().toInt() > 0) {
                num -= 1
                binding.edTextESA.setText(num.toString())
            }
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
        const val TAG = "BCDispenseFragment"
    }
}