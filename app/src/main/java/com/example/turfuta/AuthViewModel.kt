package com.example.turfuta

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Turf(
    val id: String = "",
    val location: String = "",
    val price: Double = 0.0,
    val turfName: String = "",
    val turfPhoto: String = ""
)

class AuthViewModel : ViewModel() {

    val isUserLoggedIn: Boolean = false
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    private val _username = MutableLiveData<String>("")
    val username: LiveData<String> get() = _username
    private val _turfs = MutableStateFlow<List<Turf>>(emptyList())
    val turfs: StateFlow<List<Turf>> = _turfs
    private val _featuredTurfs = MutableLiveData<List<Turf>>()
    val featuredTurfs: LiveData<List<Turf>> = _featuredTurfs

    // StateFlow for storing search results
    private val _searchResults = MutableStateFlow<List<Turf>>(emptyList())
    val searchResults: StateFlow<List<Turf>> = _searchResults

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

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    Log.d("AuthViewModel", "User ID :${uid}")
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }

    }

    fun signup(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signout() {
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
                        saveProfileToFirestore(
                            uid,
                            username,
                            downloadUrl.toString(),
                            userType,
                            onComplete
                        )
                    }.addOnFailureListener { e ->
                        onComplete(false, e.message)
                    }
                }
                .addOnFailureListener { e ->
                    onComplete(false, e.message)
                }
        } else {

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

    fun fetchAllTurfs() {
        viewModelScope.launch {
            firestore.collection("turfs")
                .get()
                .addOnSuccessListener { result ->
                    val turfList = result.documents.mapNotNull { document ->
                        Turf(
                            id = document.id,
                            location = document.getString("location") ?: "",
                            price = document.getDouble("price") ?: 0.0,
                            turfName = document.getString("turfName") ?: "",
                            turfPhoto = document.getString("turfPhoto") ?: ""
                        )
                    }
                    _turfs.value = turfList
                }
                .addOnFailureListener {

                    _turfs.value = emptyList()
                }
        }
    }


    fun searchTurfs(query: String, minPrice: Double? = null, maxPrice: Double? = null) {
        viewModelScope.launch {
            val filteredList = _turfs.value.filter { turf ->
                val matchesQuery =
                    query.isEmpty() || turf.location.contains(query, ignoreCase = true)
                val matchesPrice = (minPrice == null || turf.price >= minPrice) &&
                        (maxPrice == null || turf.price <= maxPrice)

                matchesQuery && matchesPrice
            }
            _searchResults.value = filteredList
        }
    }

    fun getTurfById(turfId: String, onResult: (Turf?) -> Unit) {
        firestore.collection("turfs").document(turfId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val turf = document.toObject(Turf::class.java)
                    onResult(turf)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AuthViewModel", "Error getting turf: $exception")
                onResult(null) // Handle error by passing null
            }
    }

//    fun bookTurf(
//        turfName: String,
//        selectedDate: String,
//        duration: String,
//        numberOfPlayers: String,
//        specialRequest: String
//    ) {
//        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
//        val bookingId = firestore.collection("bookings").document().id
//
//        val bookingData = hashMapOf(
//            "turfName" to turfName,
//            "userId" to uid,
//            "selectedDate" to selectedDate,
//            "duration" to duration.toDouble(),
//            "numberOfPlayers" to numberOfPlayers.toInt(),
//            "specialRequest" to specialRequest,
//            "status" to "Pending",
//            "createdAt" to FieldValue.serverTimestamp()
//        )
//
//        firestore.collection("bookings").document(bookingId)
//            .set(bookingData)
//            .addOnFailureListener { e ->
//                // Handle failure
//                Log.e("Booking", "Error saving booking: $e")
//            }
//    }
//
}

sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}