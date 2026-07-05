package id.vanard.ayatqu.domain.usecase

import com.google.firebase.auth.FirebaseUser
import id.vanard.ayatqu.domain.repository.AuthRepository

class AuthUseCase(private val repository: AuthRepository) {

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> =
        repository.signIn(email, password)

    suspend fun signUp(email: String, password: String): Result<FirebaseUser> =
        repository.signUp(email, password)

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> =
        repository.signInWithGoogle(idToken)

    suspend fun signOut() =
        repository.signOut()
}
