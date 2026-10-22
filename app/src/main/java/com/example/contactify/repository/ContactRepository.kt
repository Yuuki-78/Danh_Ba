package com.example.contactify.repository

import com.example.contactify.data.ContactDao
import com.example.contactify.data.ContactEntity
import com.example.contactify.data.ContactWithPhones
import com.example.contactify.data.PhoneNumberEntity
import kotlinx.coroutines.flow.Flow

class ContactRepository(private val contactDao: ContactDao) {

    // Lấy danh sách contact kèm danh sách số điện thoại dưới dạng Flow
    val allContactsWithPhones: Flow<List<ContactWithPhones>> = contactDao.getAllContactsWithPhones()

    // Thêm contact mới cùng danh sách số điện thoại
    suspend fun insertContactWithPhones(contact: ContactEntity, phoneNumbers: List<String>) {
        val contactId = contactDao.insertContact(contact).toInt()
        val phoneEntities = phoneNumbers.map { number ->
            PhoneNumberEntity(contactId = contactId, phoneNumber = number)
        }
        contactDao.insertPhoneNumbers(phoneEntities)
    }

    // Cập nhật contact và làm mới danh sách số điện thoại
    suspend fun updateContactWithPhones(contact: ContactEntity, phoneNumbers: List<String>) {
        contactDao.updateContact(contact)
        contactDao.deletePhoneNumbersForContact(contact.id)
        val phoneEntities = phoneNumbers.map { number ->
            PhoneNumberEntity(contactId = contact.id, phoneNumber = number)
        }
        contactDao.insertPhoneNumbers(phoneEntities)
    }

    // Xóa contact và các số điện thoại liên quan
    suspend fun deleteContact(contact: ContactEntity) {
        contactDao.deletePhoneNumbersForContact(contact.id)
        contactDao.deleteContact(contact)
    }
}