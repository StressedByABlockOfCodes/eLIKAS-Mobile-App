package com.example.elikas.utils

internal object  Constants {
    const val DEV_PAGE_URL = "http://192.168.1.6:8000"
    const val PROD_PAGE_URL = "https://elikasphilippines.herokuapp.com"
    const val CURRENT_URL = DEV_PAGE_URL
    const val LOCATION_POST_URL = "$CURRENT_URL/api/update/location"
    const val RESIDENTS_GET_URL = "$PROD_PAGE_URL/api/affected_residents"
}