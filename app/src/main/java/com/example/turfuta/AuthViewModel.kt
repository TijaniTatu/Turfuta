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
    val userId: String = "",
    val turfId: String = "",
    val turfOwnerId: String = "",
    val bookingDate: String = "",
    val bookingTime: String = "",
    val cost: String = "",
    val status: String = "pending"
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
    private val _userBookings = MutableStateFlow<List<Booking>>(emptyList())
    val userBookings: StateFlow<List<Booking>> = _userBookings
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
        phone_number: String,
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
                            phone_number,
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

            saveProfileToFirestore(uid, username, "", userType, phone_number,onComplete)
        }
    }

    private fun saveProfileToFirestore(
        uid: String,
        username: String,
        profilePhotoUrl: String,
        userType: String,
        phone_number: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val userProfile = mapOf(
            "username" to username,
            "profilePhotoUrl" to profilePhotoUrl,
            "userType" to userType,
            "phone_number" to phone_number
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
    fun fetchUserBookings(userId: String) {
        viewModelScope.launch {
            try {
                val bookings = firestore.collection("bookings")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { it.toObject(Booking::class.java) }
                _userBookings.value = bookings
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error fetching user bookings: ${e.message}")
                _userBookings.value = emptyList()
            }
        }
    }


    fun bookTurf(
        turfId: String,
        userId: String,
        bookingDate: String,
        bookingTime: String,
        cost: String,
        userName: String,
        userPhone: String
    ) {
        viewModelScope.launch {
            try {
                val turfRef = firestore.collection("turfs").document(turfId)
                val turf = turfRef.get().await().toObject(Turf::class.java)

                if (turf != null && turf.availability) {
                    val booking = hashMapOf(
                        "userId" to userId,
                        "userName" to userName,
                        "userPhone" to userPhone,
                        "turfId" to turfId,
                        "bookingDate" to bookingDate,
                        "bookingTime" to bookingTime,
                        "cost" to cost
                    )
                    firestore.collection("bookings").add(booking).await()
                    firestore.collection("bookinghistory").add(booking).await()

                    Log.d("BookTurf", "Booking successful!")
                } else {
                    Log.e("BookTurf", "Turf not available or does not exist.")
                }
            } catch (e: Exception) {
                Log.e("BookTurf", "Error during booking: ${e.message}")
            }
        }
    }

    fun fetchUserBookingsWithTurfNames(userId: String) {
        viewModelScope.launch {
            try {
                // Fetch bookings for the user
                val bookingDocs = firestore.collection("bookinghistory")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                    .documents

                val bookingsWithTurfNames = bookingDocs.mapNotNull { bookingDoc ->
                    val booking = bookingDoc.toObject(Booking::class.java) ?: return@mapNotNull null
                    val turfId = booking.turfId

                    // Fetch the associated turf's name
                    val turfName = firestore.collection("turfs").document(turfId)
                        .get()
                        .await()
                        .getString("name") ?: "Unknown Turf"

                    // Return a modified booking object with the turf name
                    booking.copy(turfId = turfName) // Replace turfId with the turf name
                }

                _userBookings.value = bookingsWithTurfNames
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error fetching user bookings with turf names: ${e.message}")
                _userBookings.value = emptyList()
            }
        }
    }
    fun fetchPendingBookings(userId: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true // Add a loading indicator
                val bookingsSnapshot = firestore.collection("bookings")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                // Map the documents to Booking objects
                val bookings = bookingsSnapshot.documents.mapNotNull { it.toObject(Booking::class.java) }

                Log.d("PendingBookings", "Fetched ${bookings.size} pending bookings for user: $userId")
                _userBookings.value = bookings
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error fetching pending bookings: ${e.message}")
                _userBookings.value = emptyList()
            } finally {
                isLoading.value = false // Remove the loading indicator
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