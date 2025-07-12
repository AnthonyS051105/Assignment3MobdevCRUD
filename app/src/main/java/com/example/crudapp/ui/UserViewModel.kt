package com.example.crudapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.crudapp.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        firestore.collection("users")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val userList = it.documents.mapNotNull { doc ->
                        User(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            email = doc.getString("email") ?: ""
                        )
                    }
                    _users.postValue(userList)
                }
            }
    }

    fun addUser(user: User) {
        val userData = hashMapOf(
            "name" to user.name,
            "email" to user.email
        )
        firestore.collection("users")
            .add(userData)
            .addOnSuccessListener {
                // Success
            }
            .addOnFailureListener {
                // Handle failure
            }
    }

    fun updateUser(user: User) {
        val userData = hashMapOf(
            "name" to user.name,
            "email" to user.email
        )
        firestore.collection("users").document(user.id)
            .set(userData)
            .addOnSuccessListener {
                // Success
            }
            .addOnFailureListener {
                // Handle failure
            }
    }

    fun deleteUser(user: User) {
        firestore.collection("users").document(user.id)
            .delete()
            .addOnSuccessListener {
                // Success
            }
            .addOnFailureListener {
                // Handle failure
            }
    }
}