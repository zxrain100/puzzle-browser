package com.plb.bwsr.dw

import androidx.room.*
import com.plb.bwsr.lo.PLBMark

@Dao
abstract class PLBMarkDao {

    @Query("SELECT * FROM ${PLBMark.Table.TABLE_NAME}")
    abstract fun getAll(): MutableList<PLBMark>

    @Query("SELECT * FROM ${PLBMark.Table.TABLE_NAME} WHERE plb_url = :url")
    abstract fun getByUrl(url: String): PLBMark?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addPage(list: MutableList<PLBMark>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addPage(page: PLBMark)

    @Update
    abstract fun updatePage(page: PLBMark)

    @Delete
    abstract fun deletePage(page: PLBMark)

    @Query("DELETE FROM ${PLBMark.Table.TABLE_NAME}")
    abstract fun deleteAll()
}