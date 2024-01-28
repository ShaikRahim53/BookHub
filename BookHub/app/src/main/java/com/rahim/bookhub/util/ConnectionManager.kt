package com.rahim.bookhub.util

import android.content.Context
import android.net.ConnectivityManager

class ConnectionManager {

    fun checkConnectivity(context: Context):Boolean
    {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetworkInfo

        return if ( activeNetwork?.isConnected !=null) {
            activeNetwork.isConnected
        } else {
            false
        }
    }
}