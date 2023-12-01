package org.bonhamsdemoapp.util

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/**
 * Network monitor
 *
 * @property connectivityManager
 * @constructor Create empty Network monitor
 */
class NetworkMonitor @Inject constructor(
    private val connectivityManager: ConnectivityManager
) {

    /**
     * Is connected
     */
    val isConnected: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            /**
             * On available
             *
             * @param network
             */
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(true)
            }

            /**
             * On lost
             *
             * @param network
             */
            override fun onLost(network: Network) {
                trySend(false)
                super.onLost(network)
            }
        }

        /**
         * Request
         */
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .apply {
                addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            }
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        trySend(ConnectedCompat.isConnected(connectivityManager))
        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
}
