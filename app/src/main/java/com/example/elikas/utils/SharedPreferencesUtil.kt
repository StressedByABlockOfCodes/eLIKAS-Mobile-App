/*
 * Copyright 2019 Google LLC
 * Copyright 2021 eLIKAS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.elikas.utils

import android.content.Context
import android.location.Location
import androidx.core.content.edit
import com.example.elikas.R
import com.example.elikas.data.Area
import com.example.elikas.data.User
import com.google.gson.Gson

/**
 * Returns the `location` object as a human readable string.
 */
fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}

/**
 * Provides access to SharedPreferences for location to Activities and Services.
 */
internal object SharedPreferenceUtil {

    const val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"
    const val KEY_USER = "current_user"
    const val KEY_AREA = "area"
    const val KEY_GPS_ENABLED = "gps"

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The [Context].
     */
    fun getLocationTrackingPref(context: Context): Boolean =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            .getBoolean(KEY_FOREGROUND_ENABLED, false)

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    fun saveLocationTrackingPref(context: Context, requestingLocationUpdates: Boolean) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE).edit {
            putBoolean(KEY_FOREGROUND_ENABLED, requestingLocationUpdates)
        }

    fun getUser(context: Context): User {
        val user = context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            .getString(KEY_USER, "")

        val gson = Gson()
        return gson.fromJson(user, User::class.java) ?: User(-1, "")
    }

    fun saveUser(context: Context, user: User) {
        val gson = Gson()
        val json: String = gson.toJson(user)

        context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE).edit {
            putString(KEY_USER, json)
        }
    }

    fun getArea(context: Context): Area {
        val area = context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            .getString(KEY_AREA, "")

        val gson = Gson()
        return gson.fromJson(area, Area::class.java) ?: Area("", "0")
    }

    fun saveArea(context: Context, area: Area) {
        val gson = Gson()
        val json: String = gson.toJson(area)

        context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE).edit {
            putString(KEY_AREA, json)
        }
    }

    /*fun getUserType(context: Context): String? =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            .getString(KEY_USER_TYPE, null)

    fun saveUserType(context: Context, user_type: String) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE).edit {
            putString(KEY_USER_TYPE, user_type)
        }*/

    fun setGPS(context: Context): Boolean =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            .getBoolean(KEY_GPS_ENABLED, false)

    fun reset(context: Context) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE).edit().clear().commit()

}