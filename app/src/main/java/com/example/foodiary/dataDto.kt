package com.example.foodiary

import android.os.Parcel
import android.os.Parcelable

class dataDto(var seq:Int, var path:String?, var date:String?,
              var category:Int, var menu:String?): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString()
    ) {}
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel?, p1: Int) {
        parcel?.writeInt(seq)
        parcel?.writeString(path)
        parcel?.writeString(date)
        parcel?.writeInt(category)
        parcel?.writeString(menu)
    }
    companion object CREATOR : Parcelable.Creator<dataDto> {
        override fun createFromParcel(parcel: Parcel): dataDto {
            return dataDto(parcel)
        }
        override fun newArray(size: Int): Array<dataDto?> {
            return arrayOfNulls(size)
        }
    }
}