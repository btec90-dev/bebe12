package com.example.data.model

data class IvfMilestone(
    val id: String = java.util.UUID.randomUUID().toString(),
    val phase: String,
    val title: String,
    val dateString: String,
    val time: String? = null,
    val status: String,
    val notes: String = "",
    val dosage: String? = null,
    val location: String? = null
)
