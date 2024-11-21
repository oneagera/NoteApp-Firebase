package com.markus.noteapp_firebase.domain.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.markus.noteapp_firebase.domain.model.Note
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val NOTES_COLLECTION_REF = "notes" //reference/path to the firestore collection

class StorageRepository {
    fun user() = Firebase.auth.currentUser //get the current user
    fun hasUser(): Boolean = Firebase.auth.currentUser != null //check if usr is logged in

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty() //get user id

    //obtain the collection reference
    private val notesRef: CollectionReference = Firebase
        .firestore.collection(NOTES_COLLECTION_REF)

    fun getUserNotes(
        userId: String
    ): Flow<Resource<List<Note>>> =
        callbackFlow {
            var snapshotStateListener: ListenerRegistration? = null

            try {
                //create a query to be sent to the firebase server
                snapshotStateListener = notesRef
                    .orderBy("timestamp")
                    .whereEqualTo(
                        "userId",
                        userId
                    )
                    .whereEqualTo("deleted", false)
                    .addSnapshotListener { snapshot, e ->
                        val response = if (snapshot != null) {
                            val notes =
                                snapshot.toObjects(Note::class.java)
                            Resource.Success(data = notes)
                        } else {
                            Resource.Error(throwable = e?.cause)
                        }
                        trySend(response)
                    }
            } catch (e: Exception) {
                trySend(Resource.Error(e.cause))
                e.printStackTrace()
            }
            awaitClose { //close callback
                snapshotStateListener?.remove() //deregister the listener and remove the data
            }
        }

    fun getTrashedUserNotes(userId: String): Flow<Resource<List<Note>>> =
        callbackFlow {//coroutine flow helps to watch for data changes. Callback flow helps emit/execute data inside a callback
            var snapshotStateListener: ListenerRegistration? = null
            try {
                snapshotStateListener = notesRef
                    .orderBy("timestamp")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("deleted", true) // Only fetch trashed notes
                    .addSnapshotListener { snapshot, e ->
                        val response = if (snapshot != null) {
                            val notes = snapshot.toObjects(Note::class.java)
                            Resource.Success(data = notes)
                        } else {
                            Resource.Error(throwable = e?.cause)
                        }
                        trySend(response)
                    }
            } catch (e: Exception) {
                trySend(Resource.Error(e.cause))
                e.printStackTrace()
            }
            awaitClose {
                snapshotStateListener?.remove()
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
            noteId = noteId,
            deleted = false,
            deletedTimestamp = null
        )
        notesRef
            .document(noteId)
            .set(note) //adds the data if there is an already existing doc it is going to overwrite with this data
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun softDeleteNote(
        noteId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val deletionData = mapOf(
            "deleted" to true,
            "deletedTimestamp" to Timestamp.now()
        )
        notesRef.document(noteId)
            .update(deletionData)
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun restoreNote(
        noteId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val restoreData = mapOf(
            "deleted" to false,
            "deletedTimestamp" to null
        )
        notesRef.document(noteId)
            .update(restoreData)
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
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
        notesRef.document(noteId)
            .update(updateData)
            .addOnCompleteListener {
                onResult(it.isSuccessful)
            }
    }

    fun signOut() = Firebase.auth.signOut()
}


sealed class Resource<T>(
    val data: T? = null,
    val throwable: Throwable? = null
) {
    class Loading<T> : Resource<T>()
    class Success<T>(data: T?) : Resource<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resource<T>(throwable = throwable)
}