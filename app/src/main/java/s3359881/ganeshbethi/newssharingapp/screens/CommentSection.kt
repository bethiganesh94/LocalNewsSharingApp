package s3359881.ganeshbethi.newssharingapp.screens

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import s3359881.ganeshbethi.newssharingapp.UserAccountPrefs

data class Comment(
    val commentId: String = "",
    val author: String = "",
    val comment: String = "",
    val timestamp: Long = 0L
)

fun getCommentsForPost(
    userEmail: String,
    newsId: String,
    onResult: (List<Comment>) -> Unit
) {
    val db = FirebaseDatabase.getInstance().reference

    db.child("NewsPosts").child(userEmail).child(newsId).child("comments")
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val comments = snapshot.children.mapNotNull {
                    it.getValue(Comment::class.java)
                }
                onResult(comments.sortedByDescending { it.timestamp })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
}

fun addCommentToPost(
    context: Context,
    userEmail: String,
    newsId: String,
    commentText: String,
    onSuccess: () -> Unit
) {
    val db = FirebaseDatabase.getInstance().reference
    val commentId = db.push().key ?: return
    val authorName = UserAccountPrefs.getName(context)

    val comment = Comment(
        commentId = commentId,
        author = authorName,
        comment = commentText,
        timestamp = System.currentTimeMillis()
    )

    db.child("NewsPosts").child(userEmail).child(newsId).child("comments")
        .child(commentId)
        .setValue(comment)
        .addOnSuccessListener { onSuccess() }
}
