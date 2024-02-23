package com.appdemo.androidmaps.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appdemo.androidmaps.models.PlaceNote
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MapsViewModel : ViewModel() {

    private val _listMarker = MutableLiveData<ArrayList<PlaceNote>>()
    val listMarker: LiveData<ArrayList<PlaceNote>>
        get() = _listMarker

    private val firebaseDatabase: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    var databaseReference: DatabaseReference? = null


    fun getNotes() {
        val refListNote = firebaseDatabase.getReference("/place_notes/")
        refListNote.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val noteList = ArrayList<PlaceNote>()

                    snapshot.children.forEach {
                        val note = it.getValue(PlaceNote::class.java)
                        note?.let { note ->
                            noteList.add(note)
                        }
                    }
                    _listMarker.postValue(noteList)
                }

                override fun onCancelled(error: DatabaseError) {}
            }
        )
    }

    fun addDataToFirebase(placeNote: PlaceNote) {
        databaseReference = firebaseDatabase.getReference("place_notes/").push()

        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                databaseReference?.setValue(placeNote)?.addOnSuccessListener {
                    getNotes()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO - should be show error msg, do not impl now
            }
        })
    }
}