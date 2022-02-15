package com.jcarrasco96.une2021.database

import androidx.room.*
import com.jcarrasco96.utils.UIUtils

@Dao
interface RegistryDao {

    @Query("SELECT * FROM registry ORDER BY fecha DESC")
    fun list(): List<Registry>

    @Insert
    fun insert(registry: Registry)

    @Update
    fun update(registry: Registry)

    @Delete
    fun delete(registry: Registry)

    @Query("DELETE FROM registry")
    fun deleteAll()

    @Query("SELECT * FROM registry WHERE id=:id")
    fun view(id: Int): Registry

    @Query("UPDATE registry SET oficial=:oficial WHERE id=:id")
    fun setOficial(id: Long, oficial: Boolean)

    fun insert(read: Long, lastRead: Long) {
        val registry = Registry(0, read, lastRead, UIUtils.makeDate(), false)
        insert(registry)
    }

    fun insert(read: Long, lastRead: Long, date: String, oficial: Boolean) {
        val registry = Registry(0, read, lastRead, date, oficial)
        insert(registry)
    }

}