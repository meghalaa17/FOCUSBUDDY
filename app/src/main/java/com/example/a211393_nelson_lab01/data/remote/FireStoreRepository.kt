package com.example.a211393_nelson_lab01.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// ================================================================
// WHAT IS FIRESTORE?
// Firestore is a cloud NoSQL database by Google (part of Firebase).
// Data is stored as "documents" inside "collections" — think of a
// collection like a folder, and a document like a JSON file inside it.
//
// Structure for this app:
//   community_goals (collection)
//     └── abc123 (auto-generated document ID)
//           ├── userName: "Nelson"
//           ├── goal: "Finish Chapter 5"
//           └── subject: "Physics"
//
// Unlike Room (which is private to one device), Firestore is shared
// — all users of the app see the same community_goals collection.
// ================================================================

// Data class representing one community goal document
data class CommunityGoal(
    val id: String = "",            // Firestore document ID (filled after fetch)
    val userName: String = "",
    val goal: String = "",
    val subject: String = "",
    val timestamp: Long = System.currentTimeMillis()
    // NOTE: All fields must have default values for Firestore's
    // automatic deserialization to work (it uses reflection)
)


object FirestoreRepository {

    // Firebase.firestore gives you the Firestore instance
    // No setup needed if google-services.json is in your app/ folder
    private val db: FirebaseFirestore = Firebase.firestore

    private const val COLLECTION = "community_goals"

    // ------- POST a goal to Firestore -------
    suspend fun postGoal(goal: CommunityGoal) {
        db.collection(COLLECTION)
            .add(goal)          // Firestore generates a random doc ID
            .await()            // .await() converts the callback into a
        // coroutine (so we can use suspend fun)
        // After this line, the data is live on the server.
        // Any other device listening will immediately receive it.
    }

    // ------- LISTEN for community goals (real-time) -------
    fun getCommunityGoals(): Flow<List<CommunityGoal>> = callbackFlow {
        // callbackFlow bridges callback-based APIs (like Firestore's
        // snapshot listeners) into Kotlin Flows.
        //
        // Firestore uses addSnapshotListener — it fires IMMEDIATELY
        // with current data, then again whenever the data changes.
        // This is real-time: no polling, no manual refresh needed.

        val listener = db.collection(COLLECTION)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val goals = snapshot.documents.mapNotNull { doc ->
                    // toObject() uses the default values in CommunityGoal
                    // to deserialize each Firestore document automatically
                    doc.toObject(CommunityGoal::class.java)?.copy(id = doc.id)
                    // .copy(id = doc.id) sets the Firestore document ID
                    // on the object (Firestore doesn't include it by default)
                }
                trySend(goals)  // push the new list into the Flow
            }

        // awaitClose is called when the Flow collector stops listening
        // (e.g. user navigates away). This removes the Firestore listener
        // to avoid memory leaks.
        awaitClose { listener.remove() }
    }
}