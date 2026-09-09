package id.vanard.ayatqu.presentation.auth.component

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import id.vanard.ayatqu.BuildConfig

suspend fun requestGoogleIdToken(context: Context): Result<String> = runCatching {
    val option = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
        .build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(option)
        .build()
    val result = CredentialManager.create(context).getCredential(context, request)
    GoogleIdTokenCredential.createFrom(result.credential.data).idToken
}
