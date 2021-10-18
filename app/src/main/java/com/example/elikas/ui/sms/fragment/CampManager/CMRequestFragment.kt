package com.example.elikas.ui.sms.fragment.CampManager

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
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
import com.example.elikas.databinding.FragmentCmRequestBinding
import com.example.elikas.utils.SMSUtil
import com.example.elikas.utils.SharedPreferenceUtil
import com.example.elikas.viewmodel.DisasterResponseViewModel
import com.example.elikas.viewmodel.DisasterResponseViewModelFactory


class CMRequestFragment : Fragment() {

    private var mcontext: Context? = null
    private var _binding: FragmentCmRequestBinding? = null
    private lateinit var DisasterResponseList: List<DisasterResponse>
    private var selectedDR: DisasterResponse? = null

    private val viewModelDR: DisasterResponseViewModel by viewModels {
        DisasterResponseViewModelFactory((requireActivity().application as MainApplication).repositoryDR)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCmRequestBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = SharedPreferenceUtil.getUser(view.context)
        val sms = SMSUtil(view.context)

        //SELECTED DISASTER RESPONSE
        viewModelDR.allDisasterResponses.observe(viewLifecycleOwner) { disaster_responses ->
            DisasterResponseList = disaster_responses
            val drAdapter = ArrayAdapter(
                view.context,
                android.R.layout.simple_spinner_item,
                DisasterResponseList
            )
            drAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDr.adapter = drAdapter
            binding.spinnerDr.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                    selectedDR = DisasterResponseList[pos]

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            })
        }

        binding.btnRequest.setOnClickListener {
            val foodPacks = binding.edTextFoodPacks.text.toString().trim()
            val water = binding.edTextWater.text.toString().trim()
            val hygieneKit = binding.edTextHygieneKit.text.toString().trim()
            val medicine = binding.edTextMedicine.text.toString().trim()
            val clothes = binding.edTextClothes.text.toString().trim()
            val esa = binding.edTextESA.text.toString().trim()
            var note = binding.edTextNote.text.toString()
            if(TextUtils.isEmpty(note) || note.trim() == "") {
                note = "none"
            }

            val finalDR = selectedDR?.id ?: ""
            var message = "request,${user.id},$finalDR,"
            message += "$foodPacks,$water,$hygieneKit,$medicine,$clothes,$esa,$note"
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
        const val TAG = "CMRequestFragment"
    }
}