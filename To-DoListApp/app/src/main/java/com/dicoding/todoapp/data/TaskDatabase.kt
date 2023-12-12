package com.dicoding.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dicoding.todoapp.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.Executors

//TODO 3 : Define room database class and prepopulate database using JSON
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabase(context)
                INSTANCE = instance
                instance
            }
        }

        private fun buildDatabase(context: Context): TaskDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                TaskDatabase::class.java,
                "task.db"
            )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Executors.newSingleThreadExecutor().execute {
                            fillWithStartingData(context.applicationContext, getInstance(context).taskDao())
                        }
                    }
                })
                .build()
        }

        private fun fillWithStartingData(context: Context, dao: TaskDao) {
            loadJsonArray(context)?.let { tasks ->
                try {
                    for (i in 0 until tasks.length()) {
                        val item = tasks.getJSONObject(i)
                        dao.insertAll(
                            Task(
                                item.getInt("id"),
                                item.getString("title"),
                                item.getString("description"),
                                item.getLong("dueDate"),
                                item.getBoolean("completed")
                            )
                        )
                    }
                } catch (exception: JSONException) {
                    exception.printStackTrace()
                }
            }
        }

        private fun loadJsonArray(context: Context): JSONArray? {
            val builder = StringBuilder()
            try {
                context.resources.openRawResource(R.raw.task).use { `in` ->
                    val reader = BufferedReader(InputStreamReader(`in`))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        builder.append(line)
                    }
                }
                val json = JSONObject(builder.toString())
                return json.getJSONArray("tasks")
            } catch (exception: IOException) {
                exception.printStackTrace()
            } catch (exception: JSONException) {
                exception.printStackTrace()
            }
            return null
        }
    }
}

