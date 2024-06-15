package com.mastermovilesua.runtrackerraul

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mastermovilesua.runtrackerraul.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Base64
import java.util.Random

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser

        println(user?.email)
        println(user?.displayName)
        println(user?.photoUrl)
        println(user?.phoneNumber)

        // Initialize Credential Manager
        credentialManager = CredentialManager.create(this)

        binding.signInButton.setOnClickListener {
            signInWithGoogle(this)
        }
    }

    private fun signInWithGoogle(context: Context) {
        val nonce = generateNonce()
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("239386822758-n3a3gpsf96pbv79n2j1urlqtjhrb4jl5.apps.googleusercontent.com")
            .setAutoSelectEnabled(true)
            .setNonce(nonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                Log.e("LoginActivity", "Credential retrieval failed", e)
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        Log.d("LoginActivity", "Google ID Token: $idToken")
                        // Verificar si el idToken es nulo o vacío
                        if (idToken.isNullOrEmpty()) {
                            Log.e("LoginActivity", "Google ID Token is null or empty")
                        } else {
                            firebaseAuthWithGoogle(idToken)
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("LoginActivity", "Invalid Google ID token response", e)
                    }
                } else {
                    Log.e("LoginActivity", "Unexpected type of credential")
                }
            }
            else -> {
                Log.e("LoginActivity", "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "signInWithCredential:success")
                    val user = auth.currentUser
                    // Navegar a la siguiente pantalla

                    println(user?.email)
                    println(user?.displayName)
                    println(user?.photoUrl)
                    println(user?.phoneNumber)
                } else {
                    Log.w("LoginActivity", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun generateNonce(): String {
        return try {
            val nonce = ByteArray(16)
            Random().nextBytes(nonce)
            val digest = MessageDigest.getInstance("SHA-256")
            digest.update(nonce)
            Base64.getUrlEncoder().withoutPadding().encodeToString(digest.digest())
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to generate nonce", e)
        }
    }
}
