package com.example.contactify.data

import androidx.room.Embedded
import androidx.room.Relation

data class ContactWithPhones(
    @Embedded val contact: ContactEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "contactId"
    )
    val phoneNumbers: List<PhoneNumberEntity>
)