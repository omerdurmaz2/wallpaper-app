package com.example.wallpaperapp.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.wallpaperapp.R
import java.util.*

class LanguageDialog(var activity: Activity, val clickListener: (String) -> Unit) :
    DialogFragment() {

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View? {
        val view = inflater.inflate(R.layout.dialog_language, container, false)

        val dialogDisplaySpecs = dialog?.window
        dialogDisplaySpecs?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnTurkish = view.findViewById<ImageButton>(R.id.btnTurkish)
        val btnEnglish = view.findViewById<ImageButton>(R.id.btnEnglish)

        when (Locale.getDefault().language) {
            "tr" -> btnTurkish.setPadding(10, 10, 10, 10)
            "en" -> btnEnglish.setPadding(10, 10, 10, 10)
        }



        view.findViewById<ImageButton>(R.id.btnEnglish).setOnClickListener {
            clickListener("en")
        }
        view.findViewById<ImageButton>(R.id.btnTurkish).setOnClickListener {
            clickListener("tr")
        }

        return view
    }


}