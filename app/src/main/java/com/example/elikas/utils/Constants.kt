package com.example.elikas.utils

internal object  Constants {
    const val DEV_PAGE_URL = "http://192.168.1.5:8000"
    const val PROD_PAGE_URL = "https://elikasphilippines.herokuapp.com"
    const val CURRENT_URL = DEV_PAGE_URL
    const val LOCATION_POST_URL = "$CURRENT_URL/api/update/location"
    const val RESIDENTS_GET_URL = "$CURRENT_URL/api/affected_residents"
    const val BARANGAY_RESIDENTS_GET_URL = "$CURRENT_URL/api/barangay_residents/"
    const val DISASTER_RESPONSE_GET_URL = "$CURRENT_URL/api/disaster_responses/"
    const val AREA_GET_URL = "$CURRENT_URL/api/area/"
    const val DATABASE_NAME = "residents-db"
}