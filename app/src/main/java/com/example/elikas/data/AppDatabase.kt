package com.example.elikas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.elikas.utils.Constants.DATABASE_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Resident::class, DisasterResponse::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun residentDao(): ResidentDao
    abstract fun disasterResponseDao(): DisasterResponseDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    //.fallbackToDestructiveMigration()
                    .addCallback(ResidentDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class ResidentDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                /*INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.residentDao())
                    }
                }*/
            }
        }
        suspend fun populateDatabase(residentDao: ResidentDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            residentDao.deleteAll()
        }
    }
}