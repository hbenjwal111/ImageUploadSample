package org.bonhamsdemoapp.util

import android.net.ConnectivityManager

object ConnectedCompat {

    private val IMPL: ConnectedCompatImpl

    init {
        IMPL = BaseImpl
    }

    /**
     * Is connected
     *
     * @param connectivityManager
     */
    fun isConnected(connectivityManager: ConnectivityManager) =
        IMPL.isConnected(connectivityManager)

    /**
     * Connected compat impl
     */
    internal interface ConnectedCompatImpl {
        fun isConnected(connectivityManager: ConnectivityManager): Boolean
    }
}
