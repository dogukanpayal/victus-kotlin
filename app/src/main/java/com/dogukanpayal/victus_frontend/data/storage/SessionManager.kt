package com.dogukanpayal.victus_frontend.data.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * Kalıcı oturum yöneticisi.
 * "Beni Hatırla" seçiliyken token SharedPreferences'a kaydedilir.
 * Uygulama kapatılıp açıldığında token hâlâ varsa login atlanır.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "victus_session"
        private const val KEY_TOKEN = "access_token"
        private const val KEY_REMEMBER_ME = "remember_me"
    }

    /** Token'ı kalıcı olarak kaydeder (rememberMe = true ise). */
    fun saveSession(token: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putBoolean(KEY_REMEMBER_ME, true)
            .apply()
    }

    /** Kaydedilmiş token'ı döner; yoksa ya da rememberMe false ise null. */
    fun getSavedToken(): String? {
        val remembered = prefs.getBoolean(KEY_REMEMBER_ME, false)
        if (!remembered) return null
        val token = prefs.getString(KEY_TOKEN, null)
        return if (token.isNullOrBlank()) null else token
    }

    /** Oturumu temizler (logout veya rememberMe kapalıyken). */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
