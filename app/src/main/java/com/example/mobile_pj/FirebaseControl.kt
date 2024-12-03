package com.example.mobile_pj

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

fun DocumentReference.createFireStoreData(
    setValue: Any,
    onSuccess: () -> Unit = {},
    onFailure: (Exception?) -> Unit = {},
) {
    this.set(setValue).addOnCompleteListener { task ->
        if (task.isSuccessful) onSuccess()
        else onFailure(task.exception)
    }
}

fun CollectionReference.readFireStoreData(
    onSuccess: (List<DocumentSnapshot>) -> Unit = {},
    onFailure: (Exception?) -> Unit = {}
) {
    this.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val querySnapshot = task.result
            onSuccess(querySnapshot.documents)
        } else onFailure(task.exception)
    }
}

fun DocumentReference.readFireStoreData(
    onSuccess: (DocumentSnapshot) -> Unit = {},
    onFailure: (Exception?) -> Unit = {}
) {
    this.get().addOnCompleteListener { task ->
        if (task.isSuccessful) onSuccess(task.result)
        else onFailure(task.exception)
    }
}

fun Query.readFireStoreData(
    onSuccess: (List<DocumentSnapshot>) -> Unit = {},
    onFailure: (Exception?) -> Unit = {}
) {
    this.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val querySnapshot = task.result
            onSuccess(querySnapshot.documents)
        } else onFailure(task.exception)
    }
}

fun DocumentReference.updateFireStoreData(
    updateValue: Map<String, Any>,
    onSuccess: () -> Unit = {},
    onFailure: (Exception?) -> Unit = {}
) {
    this.update(updateValue).addOnCompleteListener { task ->
        if (task.isSuccessful) onSuccess()
        else onFailure(task.exception)
    }
}

fun DocumentReference.updateFieldFireStoreData(
    updateField: String,
    updateValue: Any,
    onSuccess: () -> Unit = {},
    onFailure: (Exception?) -> Unit = {}
) {
    this.update(updateField, updateValue).addOnCompleteListener { task ->
        if (task.isSuccessful) onSuccess()
        else onFailure(task.exception)
    }
}

fun DocumentReference.deleteFireStoreData(
    onSuccess: () -> Unit = {},
    onFailure: (Exception?) -> Unit = {}
) {
    this.delete().addOnCompleteListener { task ->
        if (task.isSuccessful) onSuccess()
        else onFailure(task.exception)
    }
}