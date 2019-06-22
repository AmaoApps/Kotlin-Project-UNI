package pe.applying.kotlinuni.data.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UsuarioDao {

    @Insert
    fun insert(usuario: Usuario)

    @Update
    fun update(usuario: Usuario)

    @Delete
    fun delete(usuario: Usuario)

    @Query("DELETE FROM usuario_table")
    fun deleteAllUsuarios()

    @Query("SELECT * FROM usuario_table ORDER BY perfil DESC")
    fun getAllUsuarios() : LiveData<List<Usuario>>

    @Query("SELECT COUNT(*) FROM usuario_table")
    suspend fun getCountUsers() : Int
}