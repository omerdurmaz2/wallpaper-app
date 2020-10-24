package com.example.wallpaperapp.service

import android.widget.Toast
import com.example.wallpaperapp.WallpaperApp
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

abstract class NetworkCallback<T> : Callback<T> {


    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.code() == 401 && response.message() == "Unauthorized") {
            Toast.makeText(
                WallpaperApp.getApplicationContext(),
                "Technical Error",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (response.code() == 200) {
            if (response.body() != null) {
                try {
                    val errorBody = JSONObject(response.errorBody()!!.toString())
                    Toast.makeText(
                        WallpaperApp.getApplicationContext(),
                        "Error: $errorBody",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        WallpaperApp.getApplicationContext(),
                        "Technical Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        if (t.message!!.contains("Unable to resolve host")) {
            Toast.makeText(
                WallpaperApp.getApplicationContext(),
                "You do not have an internet connection",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                WallpaperApp.getApplicationContext(),
                t.localizedMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}