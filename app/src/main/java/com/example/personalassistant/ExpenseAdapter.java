package com.example.personalassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final Context context;
    private List<Expense> expenseList;
    private final ExpenseAdapter.OnItemClickListener listener;

    public ExpenseAdapter(Context context, List<Expense> expenseList, ExpenseAdapter.OnItemClickListener listener) {
        this.context = context;
        this.expenseList = expenseList;
        this.listener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(Expense expense);
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expense_list_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.bind(expense);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 确保 listener 不为空并且 expense 不为空时才触发回调
                if (listener != null && expense != null) {
                    listener.onItemClick(expense);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public void updateExpenseList(List<Expense> expenseList) {
        this.expenseList = expenseList;
        notifyDataSetChanged();
    }



    static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewExpenseTitle;
        private final TextView textViewExpenseAmount;
        private final TextView textViewExpenseCategory;
        private final TextView textViewExpenseDate;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewExpenseTitle = itemView.findViewById(R.id.textViewExpenseTitle);
            textViewExpenseAmount = itemView.findViewById(R.id.textViewExpenseAmount);
            textViewExpenseCategory = itemView.findViewById(R.id.textViewExpenseCategory);
            textViewExpenseDate = itemView.findViewById(R.id.textViewExpenseDate);
        }

        public void bind(Expense expense) {
            textViewExpenseTitle.setText(expense.getTitle());
            textViewExpenseAmount.setText(String.format(Locale.getDefault(), "%.2f", expense.getAmount()));
            textViewExpenseCategory.setText(expense.getCategory());
            textViewExpenseDate.setText(getFormattedDate(expense.getTimestamp()));
        }

        private String getFormattedDate(long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.format(timestamp);
        }
    }
}
