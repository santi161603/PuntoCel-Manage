package com.uni.proyecto.event.domain.singles

import com.google.firebase.firestore.FirebaseFirestore

object SingleInstancesFB {
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

}