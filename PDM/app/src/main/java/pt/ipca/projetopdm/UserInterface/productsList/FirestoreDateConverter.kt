package pt.ipca.projetopdm.UserInterface.productsList

import java.util.Date

class FirestoreDateConverter {
    companion object {
        @JvmStatic
        fun fromTimestamp(value: com.google.firebase.Timestamp?): Date? {
            return value?.toDate()
        }

        @JvmStatic
        fun toTimestamp(date: Date?): com.google.firebase.Timestamp? {
            return date?.let { com.google.firebase.Timestamp(it) }
        }
    }
}