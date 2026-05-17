package com.dogukanpayal.victus_frontend.data.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * Kalıcı oturum yöneticisi.
 * "Beni Hatırla" seçiliyken token SharedPreferences'a kaydedilir.
 * Uygulama kapatılıp açıldığında token hâlâ varsa login atlanır.
 */
class SessionManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "victus_session"
        private const val KEY_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_ROLE = "user_role"
    }

    /** Token'ı ve Kullanıcı ID'sini kalıcı olarak kaydeder (rememberMe = true ise). */
    fun saveSession(token: String, userId: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, userId)
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

    /** Kaydedilmiş kullanıcı ID'sini döner. */
    fun getUserId(): String {
        return prefs.getString(KEY_USER_ID, "") ?: ""
    }

    /** Oturumu temizler (logout veya rememberMe kapalıyken). */
    fun clearSession() {
        prefs.edit().clear().apply()
    }

    /** Kullanıcı rolünü kaydeder. */
    fun saveRole(role: String) {
        prefs.edit().putString(KEY_ROLE, role).apply()
    }

    /** Kaydedilmiş rolü döner (Varsayılan: patient). */
    fun getRole(): String {
        return prefs.getString(KEY_ROLE, "patient") ?: "patient"
    }
}
