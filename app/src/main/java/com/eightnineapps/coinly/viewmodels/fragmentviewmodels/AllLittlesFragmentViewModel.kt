package com.eightnineapps.coinly.viewmodels.fragmentviewmodels

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore

class AllLittlesFragmentViewModel: TabLayoutFragmentViewModel() {

    private var hasLoadedData = false

    fun addAllLittlesToRecyclerView(recyclerView: RecyclerView, context: Context?) {
        setupRecycler(recyclerView)
        addSpaceBetweenItems(context)
        if (!hasLoadedData) {
            Firestore.getLittles(CurrentUser.instance!!.email!!).get().addOnSuccessListener {
                val littles = mutableListOf<String>()
                for (doc in it) {
                    littles.add(doc["email"].toString())
                }
                addUsersToRecyclerView(littles, context)
                hasLoadedData = true
            }
        } else {
            updateRecyclerViewAdapterAndLayoutManager(context)
        }
    }

    fun removeUserToDisplay(user: User) {
        removeUserFromAssociates(user)
    }
}