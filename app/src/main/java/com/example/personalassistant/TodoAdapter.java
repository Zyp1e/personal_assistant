package com.example.personalassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private final List<Todo> todoList;
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Todo todo);
    }

    public TodoAdapter(Context context, List<Todo> todoList, OnItemClickListener listener) {
        this.context = context;
        this.todoList = todoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Todo todo = todoList.get(position);

        holder.textViewTitle.setText(todo.getTitle());
        holder.textViewCategory.setText(todo.getCategory());
        holder.textViewDescription.setText(todo.getDescription());

        // 设置约定日期
        holder.textViewDueDate.setText(getFormattedDate(new Date(todo.getDueDate())));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查点击的是不是筛选按钮
                if (v.getId() != R.id.btnFilter_todo) {
                    listener.onItemClick(todo);
                }
            }
        });
    }


    private String getFormattedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewCategory;
        TextView textViewDescription;
        TextView textViewDueDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDueDate = itemView.findViewById(R.id.textViewDueDate);

            // 为每个 ViewHolder 设置点击监听器
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        // 检查点击的是不是筛选按钮
                        if (v.getId() != R.id.btnFilter_todo) {
                            listener.onItemClick(todoList.get(getAdapterPosition()));
                        }
                    }
                }
            });
        }
    }


    // 添加方法来更新待办事项列表
    public void updateTodoList(List<Todo> newTodoList) {
        todoList.clear();
        todoList.addAll(newTodoList);
        notifyDataSetChanged();
    }
}
