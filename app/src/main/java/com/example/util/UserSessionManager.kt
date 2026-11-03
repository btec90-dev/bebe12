package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

/**
 * Manages persistent user authentication session for bala1fatou@gmail.com.
 * Automatically keeps the user connected so they are never prompted to sign in
 * with their Gmail and password every time they open or refresh the app.
 */
object UserSessionManager {
    private const val PREFS_NAME = "maman_bebe_prefs"
    private const val KEY_IS_CONNECTED = "pref_auth_is_connected"
    private const val KEY_USER_EMAIL = "pref_auth_user_email"
    private const val KEY_DISPLAY_NAME = "pref_auth_display_name"
    private const val KEY_USER_ID = "pref_auth_user_id"
    private const val KEY_REMEMBER_LOGIN = "pref_auth_remember_login"

    const val DEFAULT_EMAIL = "bala1fatou@gmail.com"
    const val DEFAULT_DISPLAY_NAME = "Fatou & Bala"
    const val DEFAULT_USER_ID = "user_bala1fatou"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Returns true if user session is remembered & kept connected.
     * Defaults to true for bala1fatou@gmail.com so user is never prompted on app launch.
     */
    fun isConnected(context: Context): Boolean {
        // If Firebase auth is actively logged in, definitely connected
        if (Firebase.auth.currentUser != null) return true
        // Otherwise, check persistent session (defaults to true)
        return getPrefs(context).getBoolean(KEY_IS_CONNECTED, true)
    }

    fun getConnectedEmail(context: Context): String {
        val fbEmail = Firebase.auth.currentUser?.email
        if (!fbEmail.isNullOrBlank()) return fbEmail
        return getPrefs(context).getString(KEY_USER_EMAIL, DEFAULT_EMAIL) ?: DEFAULT_EMAIL
    }

    fun getDisplayName(context: Context): String {
        val fbName = Firebase.auth.currentUser?.displayName
        if (!fbName.isNullOrBlank()) return fbName
        return getPrefs(context).getString(KEY_DISPLAY_NAME, DEFAULT_DISPLAY_NAME) ?: DEFAULT_DISPLAY_NAME
    }

    fun getUserId(context: Context): String {
        val fbUid = Firebase.auth.currentUser?.uid
        if (!fbUid.isNullOrBlank()) return fbUid
        return getPrefs(context).getString(KEY_USER_ID, DEFAULT_USER_ID) ?: DEFAULT_USER_ID
    }

    fun isRememberMeEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_REMEMBER_LOGIN, true)
    }

    fun saveSession(
        context: Context,
        email: String = DEFAULT_EMAIL,
        displayName: String = DEFAULT_DISPLAY_NAME,
        uid: String? = null,
        isConnected: Boolean = true,
        rememberMe: Boolean = true
    ) {
        getPrefs(context).edit()
            .putBoolean(KEY_IS_CONNECTED, isConnected)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_DISPLAY_NAME, displayName)
            .putString(KEY_USER_ID, uid ?: DEFAULT_USER_ID)
            .putBoolean(KEY_REMEMBER_LOGIN, rememberMe)
            .apply()
    }

    fun setConnected(context: Context, connected: Boolean) {
        getPrefs(context).edit()
            .putBoolean(KEY_IS_CONNECTED, connected)
            .apply()
    }

    fun clearSession(context: Context) {
        try {
            Firebase.auth.signOut()
        } catch (_: Exception) {}
        getPrefs(context).edit()
            .putBoolean(KEY_IS_CONNECTED, false)
            .apply()
    }
}
