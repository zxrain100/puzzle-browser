package com.plb.bwsr.d

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = PLBMark.Table.TABLE_NAME)
data class PLBMark(
    /**
     * 页面url
     */
    @ColumnInfo(name = "plb_url")
    @PrimaryKey
    val url: String,
    /**
     * 页面标题
     */
    @ColumnInfo(name = "plb_title")
    val title: String,

    /**
     * 添加时间/浏览时间
     */
    @ColumnInfo(name = "plb_time")
    val time: Long

) {
    object Table {
        const val TABLE_NAME = "plb_mark"
    }
}