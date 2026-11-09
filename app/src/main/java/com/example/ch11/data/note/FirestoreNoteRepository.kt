package com.example.ch11.data.note

import android.util.Log
import com.example.ch11.data.auth.AuthRepository
import com.example.ch11.data.model.Note
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirestoreNoteRepo"

@Singleton
class FirestoreNoteRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) : NoteRepository {

    private fun notesOf(uid: String): CollectionReference =
        firestore.collection("users").document(uid).collection("notes")

    private fun currentNotes(): CollectionReference =
        notesOf(authRepository.currentUser?.uid ?: throw NotSignedInException())

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeNotes(): Flow<List<Note>> = authRepository.authState
        .map { it?.uid }
        .distinctUntilChanged()
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else notesSnapshots(uid) }

    private fun notesSnapshots(uid: String): Flow<List<Note>> = callbackFlow {
        val registration = notesOf(uid)
            .orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    close(NoteAccessException(firestoreErrorMessage(error), error))
                    return@addSnapshotListener
                }
                val notes = snapshot?.documents.orEmpty().map { document ->
                    noteFromMap(
                        document.id,
                        document.data.orEmpty(),
                        document.metadata.hasPendingWrites()
                    )
                }
                trySend(sortNotes(notes))
            }
        awaitClose { registration.remove() }
    }

    override suspend fun getNote(id: String): Note? {
        val document = currentNotes().document(id).get().await()
        return if (document.exists()) {
            noteFromMap(document.id, document.data.orEmpty(), document.metadata.hasPendingWrites())
        } else null
    }

    override fun addNote(note: Note): Result<String> = runCatching {
        val document = currentNotes().document()
        document.set(note.toFirestoreMap()).logFailure("add")
        document.id
    }

    override fun updateNote(note: Note): Result<Unit> = runCatching {
        currentNotes().document(note.id).set(note.toFirestoreMap()).logFailure("update")
        Unit
    }

    override fun deleteNote(id: String): Result<Unit> = runCatching {
        currentNotes().document(id).delete().logFailure("delete")
        Unit
    }

    private fun Task<Void>.logFailure(action: String) {
        addOnFailureListener { error ->
            Log.w(TAG, "$action ditolak server: ${error.message}")
        }
    }
}

class NoteAccessException(message: String, cause: Throwable? = null) : Exception(message, cause)

private fun firestoreErrorMessage(e: FirebaseFirestoreException): String = when (e.code) {
    FirebaseFirestoreException.Code.PERMISSION_DENIED ->
        "Akses ditolak server. Periksa Security Rules di Firebase Console."
    FirebaseFirestoreException.Code.UNAVAILABLE -> "Server tidak dapat dihubungi."
    else -> e.message ?: "Gagal memuat catatan."
}
