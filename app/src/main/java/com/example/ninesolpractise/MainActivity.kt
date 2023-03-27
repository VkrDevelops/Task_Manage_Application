package com.example.ninesolpractise

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ninesolpractise.Adapter.TaskAdapter
import com.example.ninesolpractise.Model.TaskModel
import com.example.ninesolpractise.SharedFunctions.SharedFunctions.initializeSharedPreferences
import com.example.ninesolpractise.SharedFunctions.SharedFunctions.loadTasks
import com.example.ninesolpractise.SharedFunctions.SharedFunctions.saveTasks
import com.example.ninesolpractise.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var timer: CountDownTimer? = null
    private val tasks = mutableListOf<TaskModel>()
    private val TIMER_NOTIFICATION_ID = 1
    private val TIMER_NOTIFICATION_CHANNEL_ID = "timer_notification_channel"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeSharedPreferences(this)

        binding.startButton.setOnClickListener {
            binding.startButton.visibility = View.GONE
            binding.stopButton.visibility = View.VISIBLE
            startTimer()
        }
        binding.stopButton.setOnClickListener {
            binding.stopButton.visibility = View.GONE
            binding.startButton.visibility = View.VISIBLE
            stopTimer()
        }
        binding.breakButton.setOnClickListener {
            binding.startButton.visibility=View.GONE
            startBreakTimer()
        }
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.tasksRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = TaskAdapter(tasks, ::onTaskChecked)
        }
        binding.newTaskButton.setOnClickListener {
            showNewTaskDialog()
        }
        tasks.addAll(loadTasks())
        createNotificationChannel()

    }

    private fun startTimer() {
        timer?.cancel()
        val workDurationInMillis = 1 * 60 * 1000L
        startCountDownTimer(workDurationInMillis)
    }

    private fun startBreakTimer() {
        val breakDurationInMillis = 5 * 60 * 1000L
        startCountDownTimer(breakDurationInMillis)
    }

    private fun stopTimer() {
        binding.timerTextView.text = getString(R.string.stopping_time)
        timer?.cancel()
    }

    private fun startCountDownTimer(durationInMillis: Long) {
        timer?.cancel()

        timer = object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / (60 * 1000)
                val seconds = (millisUntilFinished % (60 * 1000)) / 1000
                binding.timerTextView.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.timerTextView.text = getString(R.string.stopping_time)
                showTimerNotification()
            }
        }.start()

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun onTaskChecked(task: TaskModel, isChecked: Boolean) {
        val index = tasks.indexOf(task)
        tasks[index] = task.copy(completed = isChecked)
        binding.tasksRecyclerView.adapter?.notifyDataSetChanged()
        saveTasks(tasks)
    }

    private fun showNewTaskDialog() {
        val dialogView = layoutInflater.inflate(R.layout.new_task_dialog, null)
        val taskEditText = dialogView.findViewById<TextInputEditText>(R.id.taskEditText)

        MaterialAlertDialogBuilder(this@MainActivity)
            .setTitle("New Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = taskEditText.text.toString()
                if (title.isNotBlank()) {
                    addTask(title)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addTask(title: String) {
        val taskId = (tasks.maxByOrNull { it.id }?.id ?: 0) + 1
        val task = TaskModel(taskId, title, false)
        tasks.add(task)
        binding.tasksRecyclerView.adapter?.notifyDataSetChanged()
    }


    private fun showTimerNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE )

        val notification = NotificationCompat.Builder(this, TIMER_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("Pomodoro Timer")
            .setContentText("Your timer has ended!")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(TIMER_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Timer Notifications"
            val descriptionText = "Notification channel for timer alerts"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(TIMER_NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}