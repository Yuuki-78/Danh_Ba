package com.example.contactify.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    // Lấy tất cả danh bạ kèm danh sách số điện thoại (Quan hệ 1-N)
    @Transaction
    @Query("SELECT * FROM contacts")
    fun getAllContactsWithPhones(): Flow<List<ContactWithPhones>>

    // Thêm Contact mới, trả về ID vừa sinh ra
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity): Long

    // Thêm danh sách số điện thoại cho contact
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoneNumbers(phoneNumbers: List<PhoneNumberEntity>)

    // Cập nhật thông tin Contact
    @Update
    suspend fun updateContact(contact: ContactEntity)

    // Xóa tất cả số điện thoại cũ của contact (dùng khi cập nhật lại danh sách số)
    @Query("DELETE FROM phone_numbers WHERE contactId = :contactId")
    suspend fun deletePhoneNumbersForContact(contactId: Int)

    // Xóa một Contact
    @Delete
    suspend fun deleteContact(contact: ContactEntity)
}