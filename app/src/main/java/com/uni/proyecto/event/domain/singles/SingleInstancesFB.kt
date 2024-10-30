package com.uni.proyecto.event.domain.singles

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object SingleInstancesFB {
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

}