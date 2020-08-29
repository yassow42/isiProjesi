package com.example.isiprojesi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.numara_girme.view.*

class NumaraDialog : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView: View = inflater.inflate(R.layout.numara_girme, container, false)
        var cancelButton = rootView.imgCancel
        var onayButton = rootView.imgOnay

        cancelButton.setOnClickListener {

        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}