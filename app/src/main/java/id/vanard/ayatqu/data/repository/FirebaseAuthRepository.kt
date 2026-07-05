package id.vanard.ayatqu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import id.vanard.ayatqu.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun signIn(email: String, password: String): Result<FirebaseUser> =
        runCatching {
            auth.signInWithEmailAndPassword(email, password).await().user
                ?: error("Sign-in succeeded but user is null")
        }

    override suspend fun signUp(email: String, password: String): Result<FirebaseUser> =
        runCatching {
            auth.createUserWithEmailAndPassword(email, password).await().user
                ?: error("Sign-up succeeded but user is null")
        }

    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> =
        runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await().user
                ?: error("Google sign-in succeeded but user is null")
        }

    override suspend fun signOut() {
        auth.signOut()
    }
}
