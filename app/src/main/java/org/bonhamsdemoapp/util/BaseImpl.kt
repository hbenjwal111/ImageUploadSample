package org.bonhamsdemoapp.util

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object BaseImpl : ConnectedCompat.ConnectedCompatImpl {
    /**
     * Is connected
     *
     * @param connectivityManager
     * @return
     */
    override fun isConnected(connectivityManager: ConnectivityManager): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }
}