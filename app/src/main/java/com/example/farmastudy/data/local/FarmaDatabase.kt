package com.example.farmastudy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.farmastudy.data.local.dao.MedicationDao
import com.example.farmastudy.data.local.dao.QuestionDao
import com.example.farmastudy.data.local.dao.QuizAttemptDao
import com.example.farmastudy.data.local.dao.UserDao
import com.example.farmastudy.data.local.entity.MedicationEntity
import com.example.farmastudy.data.local.entity.QuestionEntity
import com.example.farmastudy.data.local.entity.QuizAttemptEntity
import com.example.farmastudy.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, MedicationEntity::class, QuestionEntity::class, QuizAttemptEntity::class],
    version = 2
)
abstract class FarmaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun medicationDao(): MedicationDao
    abstract fun questionDao(): QuestionDao
    abstract fun quizAttemptDao(): QuizAttemptDao

    companion object {
        @Volatile
        private var INSTANCE: FarmaDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `quiz_attempts` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`username` TEXT NOT NULL, " +
                        "`quiz_part` INTEGER NOT NULL, " +
                        "`score` INTEGER NOT NULL, " +
                        "`total` INTEGER NOT NULL, " +
                        "`date` INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_users_username` ON `users` (`username`)"
                )
            }
        }

        fun getInstance(context: Context): FarmaDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FarmaDatabase::class.java,
                    "farma_study.db"
                ).addMigrations(MIGRATION_1_2).build().also { INSTANCE = it }
            }
    }
}