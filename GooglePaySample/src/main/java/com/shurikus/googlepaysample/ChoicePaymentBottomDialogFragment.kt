package com.shurikus.googlepaysample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChoicePaymentBottomDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bs_choise_payment_method, container, false)
    }

    companion object {
        const val TAG = "ChoicePaymentBottomDialogFragment"

        fun newInstance(): ChoicePaymentBottomDialogFragment {
            return  ChoicePaymentBottomDialogFragment()
        }
    }
}