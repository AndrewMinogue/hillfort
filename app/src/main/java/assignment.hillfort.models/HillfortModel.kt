package assignment.hillfort.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class HillfortModel(@PrimaryKey(autoGenerate = true)
                         var id: Long = 0,
                         var fbId : String = "",

                         var image: String = "",
                         var image1: String = "",
                         var image2: String = "",
                         var image3: String = "",
                         var title: String = "",
                         var notes: String = "",
                         var description: String = "",
                         var visited: Boolean = false,
                         var datevisited: String = "",
                         var rating: Float = 0F,
                         var favourite: Boolean = false,
                         @Embedded var location : Location = Location()) : Parcelable

@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable

