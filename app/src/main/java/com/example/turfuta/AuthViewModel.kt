package com.example.turfuta

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AuthViewModel : ViewModel() {

    val isUserLoggedIn: Boolean = false
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    private val _username = MutableLiveData<String>("")
    val username: LiveData<String> get() = _username

    init {
        checkAuthStatus()
        fetchUsername()

    }

    private fun fetchUsername() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    _username.value = document.getString("username") ?: "Unknown"
                }
                .addOnFailureListener {
                    _username.value = "Error"
                }
        }
    }

    fun checkAuthStatus(){
        if(auth.currentUser==null){
            _authState.value = AuthState.Unauthenticated
        }else{
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email : String,password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    val uid = auth.currentUser?.uid
                    Log.d("AuthViewModel","User ID :${uid}")
                    _authState.value = AuthState.Authenticated
                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }

    }

    fun signup(email : String,password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }
    }

    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
        _username.value = ""
    }

    fun buildProfile(
        username: String,
        photoUri: Uri?,
        userType: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return

        if (photoUri != null) {
            // Upload photo to Firebase Storage
            val storageRef: StorageReference = storage.reference.child("profile_photos/$uid.jpg")
            storageRef.putFile(photoUri)
                .addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        saveProfileToFirestore(uid, username, downloadUrl.toString(), userType, onComplete)
                    }.addOnFailureListener { e ->
                        onComplete(false, e.message)
                    }
                }
                .addOnFailureListener { e ->
                    onComplete(false, e.message)
                }
        } else {
            // If no photo, save other profile info
            saveProfileToFirestore(uid, username, "", userType, onComplete)
        }
    }

    private fun saveProfileToFirestore(
        uid: String,
        username: String,
        profilePhotoUrl: String,
        userType: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val userProfile = mapOf(
            "username" to username,
            "profilePhotoUrl" to profilePhotoUrl,
            "userType" to userType
        )
        firestore.collection("users").document(uid)
            .set(userProfile)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }

}


sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}