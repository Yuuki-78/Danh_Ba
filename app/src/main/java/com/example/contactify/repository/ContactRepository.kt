package com.example.contactify.repository

import com.example.contactify.data.ContactDao
import com.example.contactify.data.ContactEntity
import com.example.contactify.data.ContactWithPhones
import com.example.contactify.data.PhoneNumberEntity
import android.content.ContentResolver
import android.provider.ContactsContract
import android.content.ContentProviderOperation
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
    // HÀM MỚI: Đẩy 1 liên hệ từ App ra danh bạ hệ thống (Export)
    fun addContactToSystem(contentResolver: ContentResolver, name: String, phone: String) {
        val operations = ArrayList<ContentProviderOperation>()

        // 1. Tạo RawContact mới
        operations.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .build()
        )

        // 2. Thêm Tên (Display Name)
        operations.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) // 👈 Sửa thành RAW_CONTACT_ID
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build()
        )

        // 3. Thêm Số điện thoại
        operations.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) // 👈 Sửa thành RAW_CONTACT_ID
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build()
        )

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Xóa contact và các số điện thoại liên quan
    suspend fun deleteContact(contact: ContactEntity) {
        contactDao.deletePhoneNumbersForContact(contact.id)
        contactDao.deleteContact(contact)
    }
    fun fetchSystemContacts(contentResolver: ContentResolver): Map<String, List<String>> {
        val contactMap = mutableMapOf<String, MutableList<String>>()

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null, null, null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = if (nameIndex != -1) it.getString(nameIndex) else "Unknown"
                val number = if (numberIndex != -1) it.getString(numberIndex) else ""

                if (number.isNotBlank()) {
                    contactMap.getOrPut(name) { mutableListOf() }.add(number)
                }
            }
        }
        return contactMap
    }

}