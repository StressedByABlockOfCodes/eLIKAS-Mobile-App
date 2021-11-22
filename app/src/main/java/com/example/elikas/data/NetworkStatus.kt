package com.example.elikas.data

sealed class NetworkStatus {
    object Available : NetworkStatus()
    object Unavailable : NetworkStatus()
}