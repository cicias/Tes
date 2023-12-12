package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var viewModel: DetailTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        //TODO 11 : Show detail task and implement delete action
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[DetailTaskViewModel::class.java]

        val taskId = intent.getIntExtra(TASK_ID, 0)
        viewModel.setTaskId(taskId)

        viewModel.task.observe(this@DetailTaskActivity) { task ->
            val detailEdTitle = findViewById<EditText>(R.id.detail_ed_title)
            val detailEdDescription = findViewById<EditText>(R.id.detail_ed_description)
            val detailEdDueDate = findViewById<EditText>(R.id.detail_ed_due_date)

            detailEdTitle.setText(task?.title)
            detailEdDescription.setText(task?.description)
            detailEdDueDate.setText(task?.dueDateMillis?.let {
                DateConverter.convertMillisToString(it)
            })
        }

        val btnDelete = findViewById<Button>(R.id.btn_delete_task)
        btnDelete.setOnClickListener {
            viewModel.deleteTask()
            finish()
        }
    }
}