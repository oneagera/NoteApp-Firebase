package com.markus.noteapp_firebase.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

//Class to authenticate usr
class AuthRepository {
    val currentUser: FirebaseUser? = Firebase.auth.currentUser //current normal firebase user

    //check if usr is logged in
    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    //get usr id
    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty() //orEmpty() if the usr id is null

    //create usr
    suspend fun createUser(
        email: String,
        password: String,
        onComplete: (Boolean) -> Unit
    ) = withContext(Dispatchers.IO) {//switch thread from main thread to IO using Dispatchers.IO as opposed to dispatchers.main
        Firebase.auth
            .createUserWithEmailAndPassword(email, password)//crete usr with email & pass
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onComplete.invoke(true) //invoke onComplete listener with true
                } else {
                    onComplete.invoke(false)
                }
            }.await() //call await() fun so as not to block the main thread
    }

    //login a usr
    suspend fun loginUser(
        email: String,
        password: String,
        onComplete: (Boolean) -> Unit
    ) = withContext(Dispatchers.IO) {//switch thread from main thread to IO using Dispatchers.IO as opposed to dispatchers.main
        Firebase.auth
            //.signInWithEmailLink() if it's enabled in firebase
            .signInWithEmailAndPassword(email, password)//crete usr with email & pass
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onComplete.invoke(true) //invoke onComplete listener with true
                } else {
                    onComplete.invoke(false)
                }
            }.await() //call await() fun so as not to block the main thread
    }


}