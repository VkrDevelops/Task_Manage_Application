package com.example.ninesolpractise.SharedFunctions

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.ninesolpractise.Model.TaskModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedFunctions {
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    fun initializeSharedPreferences(activity: Activity) {
        sharedPreferences = activity.getSharedPreferences("PomodoroApp", MODE_PRIVATE)
    }


    fun loadTasks(): List<TaskModel> {
        val tasksJson = sharedPreferences.getString("tasks", null) ?: return emptyList()
        val tasksType = object : TypeToken<List<TaskModel>>() {}.type
        return gson.fromJson(tasksJson, tasksType)
    }

    fun saveTasks(tasks: List<TaskModel>) {
        sharedPreferences.edit().apply {
            putString("tasks", gson.toJson(tasks))
            apply()
        }
    }

    fun getValuesFromSharedPreferences(key: String): Int {
        return sharedPreferences.getInt(key, 5)
    }

    fun addValuesToSharedPreferences(key: String, values: Int) {

        sharedPreferences.edit().apply {
            putInt(key, values)
            apply()
        }
    }
}