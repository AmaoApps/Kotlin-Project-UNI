package pe.applying.kotlinuni.data.repository

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import pe.applying.kotlinuni.data.database.CulPartyDatabase
import pe.applying.kotlinuni.data.model.Usuario
import pe.applying.kotlinuni.data.model.UsuarioDao

class UsuarioRepository(application: Application) {

    private var usuarioDao: UsuarioDao
    private var lstUsuarios: LiveData<List<Usuario>>

    init {
        val database: CulPartyDatabase = CulPartyDatabase.getInstance(context = application.applicationContext)!!
        usuarioDao = database.usuarioDao()
        lstUsuarios = usuarioDao.getAllUsuarios()
    }

    fun insert(usuario: Usuario){
        insertUsuarioAsyncTask(usuarioDao).execute(usuario)
    }

    fun delete(usuario: Usuario){
        deleteUsuarioAsyncTask(usuarioDao).execute(usuario)
    }

    fun deleteAllUsers(){
        deleteAllUsuariosAsyncTask(usuarioDao).execute()
    }

    suspend fun getCountUsuarios() : Int{
        return usuarioDao.getCountUsers()
    }

    fun getUsuarios() : LiveData<List<Usuario>>{
        return lstUsuarios
    }

    fun isLogged(): Boolean{
        if(getUsuarios().value != null && getUsuarios().value!!.size>0){
            return true
        }
        return false
    }

    companion object{
        private class insertUsuarioAsyncTask(usuarioDao: UsuarioDao) : AsyncTask<Usuario, Unit, Unit>() {
            val usuarioDao = usuarioDao

            override fun doInBackground(vararg params: Usuario?) {
                usuarioDao.insert(params[0]!!)
            }
        }

        private class deleteUsuarioAsyncTask(usuarioDao: UsuarioDao) : AsyncTask<Usuario, Unit, Unit>() {
            val usuarioDao = usuarioDao

            override fun doInBackground(vararg params: Usuario?) {
                usuarioDao.delete(params[0]!!)
            }
        }

        private class deleteAllUsuariosAsyncTask(usuarioDao: UsuarioDao) : AsyncTask<Unit, Unit, Unit>() {
            val usuarioDao = usuarioDao

            override fun doInBackground(vararg params: Unit?) {
                usuarioDao.deleteAllUsuarios()
            }
        }

    }

}