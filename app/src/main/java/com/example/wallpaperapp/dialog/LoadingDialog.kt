package com.example.wallpaperapp.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.wallpaperapp.R

class LoadingDialog(var activity: Activity) : DialogFragment() {

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_loading, container, false)

        val dialogDisplaySpecs = dialog?.window
        dialogDisplaySpecs?.setBackgroundDrawableResource(android.R.color.transparent)

        context?.let {
            Glide.with(it).load(R.raw.loading).placeholder(R.raw.loading)
                .into(view.findViewById(R.id.ivCustomLoadingDialog))
        }

        return view
    }
}