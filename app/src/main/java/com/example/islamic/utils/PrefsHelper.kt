// File: PrefsHelper.kt
package com.example.islamic.utils

import android.content.Context
import android.content.SharedPreferences

object PrefsHelper {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_NAME = "key_name"
    private const val KEY_EMAIL = "key_email"
    private const val KEY_PHONE = "key_phone"
    private const val KEY_PASSWORD = "key_password"

    private fun getPrefs(ctx: Context): SharedPreferences {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // --- Profil ---
    fun getName(ctx: Context): String? =
        getPrefs(ctx).getString(KEY_NAME, null)

    fun getEmail(ctx: Context): String? =
        getPrefs(ctx).getString(KEY_EMAIL, null)

    fun getPhone(ctx: Context): String? =
        getPrefs(ctx).getString(KEY_PHONE, null)

    fun setProfile(ctx: Context, name: String, email: String, phone: String) {
        getPrefs(ctx).edit()
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putString(KEY_PHONE, phone)
            .apply()
    }

    // --- Password ---
    fun getPassword(ctx: Context): String? =
        getPrefs(ctx).getString(KEY_PASSWORD, null)

    fun setPassword(ctx: Context, newPassword: String) {
        getPrefs(ctx).edit()
            .putString(KEY_PASSWORD, newPassword)
            .apply()
    }
}
