package com.example.turfuta

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Turf(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val cost: Any = "", // Accept Any type for cost
    val description: String = "",
    val timeAvailable: String = "",
    val availability: Boolean = false,
    val images: List<String> = emptyList()
)

data class Booking(
    val userId: String,
    val turfId: String,
    val turfOwnerId: String,
    val bookingTime: String, // You can format this as needed
    val status: String = "pending" // You can set this to "pending", "confirmed", etc.
)


class AuthViewModel : ViewModel() {

    val isUserLoggedIn: Boolean = false
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    val isLoading = mutableStateOf(false)
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
    private val _turfDetails = MutableStateFlow<Turf?>(null)
    val turfDetails: StateFlow<Turf?> = _turfDetails

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
                            availability = document.getBoolean("availability") ?: false,
                            cost = document.get("cost")?.toString() ?: "", // Convert cost to String
                            description = document.getString("description") ?: "",
                            images = document.get("images") as? List<String> ?: emptyList(),
                            location = document.getString("location") ?: "",
                            name = document.getString("name") ?: "",
                            timeAvailable = document.getString("timeAvailable") ?: ""
                        )
                    }
                    _turfs.value = turfList
                    isLoading.value = false
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
                    query.isEmpty() || turf.location.contains(
                        query,
                        ignoreCase = true
                    ) || turf.name.contains(query, ignoreCase = true)

                // Check if cost is a String or Long and convert it to Double
                val costAsDouble = when (val cost = turf.cost) {
                    is String -> cost.toDoubleOrNull() ?: 0.0 // If cost is a String, try to convert to Double
                    is Long -> cost.toDouble() // If cost is a Long, convert directly to Double
                    else -> 0.0 // Handle other cases (null or unsupported types)
                }

                val matchesPrice = (minPrice == null || costAsDouble >= minPrice) &&
                        (maxPrice == null || costAsDouble <= maxPrice)

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

    fun bookTurf(
        turfId: String,
        userId: String,
        bookingDate: String,
        bookingTime: String,  // New parameter for the booking time
        cost: String
    ) {
        viewModelScope.launch {
            try {
                // Get the turf details
                val turfRef = firestore.collection("turfs").document(turfId)
                val turf = turfRef.get().await().toObject(Turf::class.java)

                if (turf != null && turf.availability) {
                    // Add a new booking document to Firestore, including booking time
                    val booking = hashMapOf(
                        "userId" to userId,
                        "turfId" to turfId,
                        "bookingDate" to bookingDate,
                        "bookingTime" to bookingTime,  // Store the booking time
                        "cost" to cost
                    )
                    firestore.collection("bookings").add(booking).await()

                    // Update the turf's availability (set it to false)
                    turfRef.update("availability", false).await()

                    // Optionally, notify the user about the successful booking
                } else {
                    // Handle the case when turf is not available
                    // Optionally, show a message to the user
                }
            } catch (e: Exception) {
                // Handle error, maybe show a toast or dialog
                Log.e("BookTurf", "Error during booking: ${e.message}")
            }
        }
    }


    fun getTurfDetails(turfId: String) {
        viewModelScope.launch {
            firestore.collection("turfs")
                .document(turfId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val turf = document.toObject(Turf::class.java)
                        _turfDetails.value = turf
                    }
                }
        }

    }
}
//


sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}