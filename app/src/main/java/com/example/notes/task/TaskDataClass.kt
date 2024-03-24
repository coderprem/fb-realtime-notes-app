package com.example.notes.task

import android.os.Parcel
import android.os.Parcelable

data class TaskDataClass(
    val title: String?=null,
    val description: String?=null,
    val timeStamp: Long?=null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeValue(timeStamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaskDataClass> {
        override fun createFromParcel(parcel: Parcel): TaskDataClass {
            return TaskDataClass(parcel)
        }

        override fun newArray(size: Int): Array<TaskDataClass?> {
            return arrayOfNulls(size)
        }
    }
}