package com.example.ninesolpractise.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.ninesolpractise.Model.TaskModel
import com.example.ninesolpractise.R

class TaskAdapter(private val tasks: List<TaskModel>, private val onTaskChecked: (TaskModel, Boolean) -> Unit): RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.task_design,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(tasks[position],onTaskChecked)
    }

    override fun getItemCount()= tasks.size
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val check=itemView.findViewById<CheckBox>(R.id.taskCheckBox)

    fun bind(task: TaskModel,onTaskChecked: (TaskModel, Boolean) -> Unit){
        check.text=task.title
        check.isChecked=task.completed
        check.setOnCheckedChangeListener { compoundButton, b ->
            onTaskChecked(task,b)
        }
    }


}
