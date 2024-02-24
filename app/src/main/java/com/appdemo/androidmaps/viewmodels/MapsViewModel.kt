package com.appdemo.androidmaps.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdemo.androidmaps.models.PlaceNote
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsViewModel : ViewModel() {

    private val _listMarker = MutableLiveData<ArrayList<PlaceNote>>()
    val listMarker: LiveData<ArrayList<PlaceNote>>
        get() = _listMarker

    private val _searchListResult = MutableLiveData<ArrayList<PlaceNote>>()
    val searchListResult: LiveData<ArrayList<PlaceNote>>
        get() = _searchListResult

    private val _searchError = MutableLiveData<String>()
    val searchError: LiveData<String>
        get() = _searchError

    private val firebaseDatabase: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    var databaseReference: DatabaseReference? = null

    fun getNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            val refListNote = firebaseDatabase.getReference("/place_notes/")
            refListNote.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val noteList = ArrayList<PlaceNote>()

                        snapshot.children.forEach { item ->
                            val note = item.getValue(PlaceNote::class.java)
                            note?.let {
                                noteList.add(it)
                            }
                        }
                        _listMarker.postValue(noteList)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                }
            )
        }
    }

    fun addDataToFirebase(placeNote: PlaceNote) {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun searchNote(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val refListNote = firebaseDatabase.getReference("/place_notes/")
            refListNote.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val resultList = ArrayList<PlaceNote>()

                        snapshot.children.forEach { item ->
                            val note = item.getValue(PlaceNote::class.java)
                            note?.let {
                                if (it.userName.contains(keyword) || it.note.contains(keyword)) {
                                    resultList.add(note)
                                }
                            }
                        }

                        if (resultList.isEmpty()) {
                            _searchError
                                .postValue("Do not have any note matching with the keyword")
                        } else {
                            _searchListResult.postValue(resultList)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                }
            )
        }
    }

    fun clearData() {
        _searchListResult.postValue(arrayListOf())
        _searchError.postValue("")
    }
}