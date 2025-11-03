package com.example.trailfinder

@Entity(tableName = "trails")
data class TrailEntity(
    @PrimaryKey val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val notes: String?
)