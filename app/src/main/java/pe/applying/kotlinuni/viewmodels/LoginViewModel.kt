package pe.applying.kotlinuni.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import pe.applying.kotlinuni.data.model.Usuario
import pe.applying.kotlinuni.data.repository.UsuarioRepository

class LoginViewModel(application: Application) : AndroidViewModel(application){

    //Conectamos al reopsitorio, que en este caso es el "Modelo"
    private var repository: UsuarioRepository =
        UsuarioRepository(application)

    //Obtenemos todos los usuarios
    private var allUsuarios : LiveData<List<Usuario>> = repository.getUsuarios()

    fun insert(usuario: Usuario){
        repository.insert(usuario)
    }

    fun getUsuarios() : LiveData<List<Usuario>> {
        return allUsuarios
    }

    suspend fun getCountUsuarios() : Int {
        return repository.getCountUsuarios()
    }

    fun isLogged() : Boolean {
        return repository.isLogged()
    }

    fun deleteUsers(){
        repository.deleteAllUsers()
    }
}