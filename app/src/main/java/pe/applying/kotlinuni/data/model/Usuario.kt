package pe.applying.kotlinuni.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario_table")
data class Usuario (
    var nombres: String,
    var perfil: String,
    var imagen: String,
    var calificacion: Double,
    var token: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}