package pe.applying.kotlinuni.data.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import pe.applying.kotlinuni.data.model.Usuario
import pe.applying.kotlinuni.data.model.UsuarioDao

@Database(entities = [Usuario::class], version = 1)
abstract class CulPartyDatabase : RoomDatabase(){

    abstract fun usuarioDao() : UsuarioDao

    companion object{
        private var instance: CulPartyDatabase? = null

        fun getInstance(context: Context): CulPartyDatabase? {
            if (instance == null) {
                synchronized(CulPartyDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CulPartyDatabase::class.java, "uni_database"
                    )
                        .fallbackToDestructiveMigration() // when version increments, it migrates (deletes db and creates new) - else it crashes
                        .addCallback(roomCallback)
                        .build()
                }
            }
            return instance
        }

        fun destroyInstance() {
            instance = null
        }

        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateDbAsyncTask(instance)
                    .execute()
            }
        }

        class PopulateDbAsyncTask(db: CulPartyDatabase?) : AsyncTask<Unit, Unit, Unit>() {
            private val noteDao = db?.usuarioDao()

            override fun doInBackground(vararg p0: Unit?) {
                /*
                noteDao?.insert(Note("title 1", "description 1", 1))
                noteDao?.insert(Note("title 2", "description 2", 2))
                noteDao?.insert(Note("title 3", "description 3", 3))
                */
            }
        }

    }

}