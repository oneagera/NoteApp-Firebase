package com.markus.noteapp_firebase.domain.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.markus.noteapp_firebase.domain.model.Note
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val NOTES_COLLECTION_REF = "notes" //reference/path to the firestore collection

class StorageRepository() {
    fun user() = Firebase.auth.currentUser //get the current user
    fun hasUser(): Boolean = Firebase.auth.currentUser != null //check if usr is logged in

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty() //get user id

    //obtain the collection reference
    private val notesRef: CollectionReference = Firebase
        .firestore.collection(NOTES_COLLECTION_REF)

    fun getUserNotes(
        userId: String
    ): Flow<Resource<List<Note>>> =
        callbackFlow {//coroutine flow helps to watch for data changes. Callback flow helps emit/execute data inside a callback
            var snapshotStateListener: ListenerRegistration? = null

            try {
                //create a query to be sent to the firebase server
                snapshotStateListener = notesRef
                    .orderBy("timestamp") //arrange data by timestamp. OrderBy also has direction parameter to help order by ascending or descending. In this case we leave it at default
                    .whereEqualTo(
                        "userId",
                        userId
                    ) //limit/filter the fields that match only, in this case userId
                    .addSnapshotListener { snapshot, e ->
                        val response = if (snapshot != null) {
                            val notes =
                                snapshot.toObjects(Note::class.java) //toObject transforms firestore data back to our data class
                            Resource.Success(data = notes)
                        } else {
                            Resource.Error(throwable = e?.cause)
                        }
                        trySend(response)
                    }
            } catch (e: Exception) {
                trySend(Resource.Error(e?.cause))
                e.printStackTrace()
            }
            awaitClose { //close callback
                snapshotStateListener?.remove() //deregister the listener and remove the data
            }
        }

    fun getNote(
        noteId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (Note?) -> Unit
    ) {
        notesRef
            .document(noteId)//document reference
            .get() //get the document
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(Note::class.java)) //invoke onSuccess to obtain the document snapshot then toObject and deserialize to whatcan be understood by the app
            }
            .addOnFailureListener { result ->
                onError.invoke(result.cause)
            }
    }

    fun addNote(
        userId: String = "",
        title: String = "",
        description: String = "",
        timestamp: Timestamp = Timestamp.now(),
        color: Int = 0,
        onComplete: (Boolean) -> Unit
    ) {
        val noteId = notesRef.document().id
        val note = Note(
            userId,
            title,
            description,
            timestamp,
            colorIndex = color,
            noteId = noteId
        )
        notesRef
            .document(noteId)
            .set(note) //adds the data if there is an already existing doc it is going to overwrite with this data
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun deleteNote(
        noteId: String,
        onComplete: (Boolean) -> Unit
    ) {
        notesRef.document(noteId)
            .delete()
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun updateNote(
        title: String,
        description: String,
        color: Int,
        noteId: String,
        onResult: (Boolean) -> Unit
    ) {
        //define key value pairs
        val updateData = hashMapOf<String, Any>(
            "colorIndex" to color,
            "description" to description,
            "title" to title
        )
        //obtain the document and update the data
        notesRef.document(noteId)
            .update(updateData)//alternatively .update("noteId", noteId) if you don't have many values but in this case we use the hashmap above.
            .addOnCompleteListener {
                onResult(it.isSuccessful)
            }
    }

    fun signOut() = Firebase.auth.signOut()
}


sealed class Resource<T>( //<T> stands for generic type
    val data: T? = null,
    val throwable: Throwable? = null
) {
    class Loading<T> : Resource<T>()
    class Success<T>(data: T?) : Resource<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resource<T>(throwable = throwable)
}