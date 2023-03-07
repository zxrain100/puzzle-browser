package com.plb.bwsr.d

import androidx.room.*

@Dao
abstract class PLBHistoryDao {

    @Query("SELECT * FROM ${PLBHistory.Table.TABLE_NAME}")
    abstract fun getAll(): MutableList<PLBHistory>

    @Query("SELECT * FROM ${PLBHistory.Table.TABLE_NAME} WHERE plb_url = :url")
    abstract fun getByUrl(url: String): PLBHistory?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addPage(list: MutableList<PLBHistory>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addPage(page: PLBHistory)

    @Update
    abstract fun updatePage(page: PLBHistory)

    @Delete
    abstract fun deletePage(page: PLBHistory)

    @Query("DELETE FROM ${PLBHistory.Table.TABLE_NAME}")
    abstract fun deleteAll()
}