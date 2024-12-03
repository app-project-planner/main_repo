package com.example.mobile_pj.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.mobile_pj.data.models.PlanData
import androidx.lifecycle.ViewModel
import com.example.mobile_pj.deleteFireStoreData
import com.example.mobile_pj.readFireStoreData
import com.example.mobile_pj.stringToUnixTimestamp
import com.example.mobile_pj.updateFireStoreData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlanViewModel: ViewModel() {
    private val _plans = MutableStateFlow<List<PlanData>>(emptyList())
    val plans = _plans.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPlans(uid: String, date: String) {
        val getPlansRef = FirebaseFirestore.getInstance()
            .collection("schedule")
            .document(uid)
            .collection(date.stringToUnixTimestamp().toString())

        getPlansRef.readFireStoreData(
            onSuccess = { documents ->
                val currentPlans = documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(PlanData::class.java)
                }
                _plans.value = currentPlans // 한 번에 업데이트
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCompleteOrNotPlans(uid: String, date: String, complete: Boolean) {
        val getPlansRef = FirebaseFirestore.getInstance()
            .collection("schedule")
            .document(uid)
            .collection(date.stringToUnixTimestamp().toString())

        getPlansRef.whereEqualTo("complete", complete).readFireStoreData(
            onSuccess = { documents ->
                val currentPlans = documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(PlanData::class.java)
                }
                _plans.value = currentPlans // 한 번에 업데이트
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun modifyPlan(
        uid: String,
        date: String,
        documentId: String,
        title: String,
        description: String,
        onModifySuccess: () -> Unit = {},
        onModifyFailed: (Exception?) -> Unit = {}
    ) {
        val modifyPlanRef = FirebaseFirestore.getInstance()
            .collection("schedule")
            .document(uid)
            .collection(date.stringToUnixTimestamp().toString())
            .document(documentId)

        modifyPlanRef.updateFireStoreData(
            updateValue = mapOf(
                "title" to title,
                "description" to description
            ),
            onSuccess = onModifySuccess,
            onFailure = onModifyFailed
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deletePlan(
        uid: String,
        date: String,
        documentId: String,
        onDeleteSuccess: () -> Unit = {},
        onDeleteFailed: (Exception?) -> Unit = {}
    ) {
        val checkDate = date.stringToUnixTimestamp().toString()
        val deletePlanRef = FirebaseFirestore.getInstance()
            .collection("schedule")
            .document(uid)
            .collection(checkDate)
            .document(documentId)

        deletePlanRef.deleteFireStoreData(
            onSuccess = onDeleteSuccess,
            onFailure = onDeleteFailed
        )
    }

//    fun changePlanCompleteAtIndex(position: Int, checked: Boolean) {
//        _plans.value = _plans.value.mapIndexed { index, plan ->
//            if (index == position) {
//                plan.copy(complete = checked) // PlanData가 data class라면 copy 메서드를 사용
//            } else {
//                plan
//            }
//        }
//    }
}
