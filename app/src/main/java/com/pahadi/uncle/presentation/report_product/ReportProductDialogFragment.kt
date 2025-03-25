package com.pahadi.uncle.presentation.report_product

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.pahadi.uncle.databinding.DialogReportProductBinding
import com.pahadi.uncle.presentation.utils.hideKeyboard

class ReportProductDialogFragment : DialogFragment() {
    private lateinit var mBinding: DialogReportProductBinding
    private var mView: View? = null
    private val mProductId by lazy { requireArguments().getString("product_id")!! }
    private val mViewModel by viewModels<ReportProductViewModel> { ReportViewModelFactory(mProductId) }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBinding = DialogReportProductBinding.inflate(layoutInflater, null, false)
        mBinding.viewModel = mViewModel
        mBinding.lifecycleOwner = this
        mView = mBinding.root
        return AlertDialog.Builder(requireContext()).setView(mBinding.root).create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.submittedSuccessfully.observe(viewLifecycleOwner) {
            if (it == false) return@observe
            Toast.makeText(requireContext(), "Post Reported Successfully", Toast.LENGTH_SHORT)
                .show()
            hideKeyboard(requireContext(), mBinding.reportEt)
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mView = null
    }
}
