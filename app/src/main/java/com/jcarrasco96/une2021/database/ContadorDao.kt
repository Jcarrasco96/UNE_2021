package com.jcarrasco96.une2021.database

import androidx.room.*

@Dao
interface ContadorDao {

    @Query("SELECT * FROM contador")
    fun list(): List<Contador>

    @Insert
    fun insert(contador: Contador)

    @Delete
    fun delete(contador: Contador)

    @Query("DELETE FROM contador")
    fun deleteAll()

    @Query("SELECT * FROM contador WHERE id=:id")
    fun view(id: Int): Contador

}
