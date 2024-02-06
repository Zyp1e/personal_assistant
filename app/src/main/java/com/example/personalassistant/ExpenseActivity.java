package com.example.personalassistant;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseActivity extends AppCompatActivity {

    private ExpenseDatabaseHelper databaseHelper;
    private RecyclerView recyclerViewExpense;
    private ExpenseAdapter expenseAdapter;
    private FloatingActionButton fabAddExpense;
    private PopupWindow popupWindow;
    private Button btnSaveExpense, btnCancelExpense,btnDeleteExpense;
    private EditText etExpenseTitle, etExpenseAmount, etExpenseCategory;
    private Spinner timeRangeSpinner; // 时间选择器
    private TextView totalAmountTextView; // 显示总额的文本视图
    private Button btnFilter;
    private DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        databaseHelper = new ExpenseDatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.expenseToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });

        timeRangeSpinner = findViewById(R.id.timeRangeSpinner);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);

        expenseAdapter = new ExpenseAdapter(this, getExpenseList(), new ExpenseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Expense expense) {
                if (expense != null) {
                    showEditExpensePopup(expense);
                }
            }
        });
        recyclerViewExpense = findViewById(R.id.recyclerViewExpense);
        recyclerViewExpense.setLayoutManager(new LinearLayoutManager(this));
        // 设置时间选择器的选项和监听器
        setupTimeRangeSpinner();

        // 初始显示当天的支出清单和总额
        updateExpenseListAndTotalAmount(TimeRange.TODAY);
        btnFilter = findViewById(R.id.btnFilter);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDatePickerVisibility();
            }
        });



        fabAddExpense = findViewById(R.id.fabAddExpense);
        fabAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditExpensePopup(null);
            }
        });
        // 初始化 DatePickerDialog
        datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // 处理日期选择
                        handleDateSelected(year, month, dayOfMonth);
                    }
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        // 按钮点击事件，显示 DatePickerDialog
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });


        recyclerViewExpense.setAdapter(expenseAdapter);
    }
    private void showEditExpensePopup(Expense expense) {
        View view = LayoutInflater.from(this).inflate(R.layout.popup_edit_expense, null);
        popupWindow = new PopupWindow(view, RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT, true);

        ColorDrawable background = new ColorDrawable(android.graphics.Color.WHITE);
        popupWindow.setBackgroundDrawable(background);

        etExpenseTitle = view.findViewById(R.id.etExpenseTitle);
        etExpenseAmount = view.findViewById(R.id.etExpenseAmount);
        etExpenseCategory = view.findViewById(R.id.etExpenseCategory);
        btnSaveExpense = view.findViewById(R.id.btnSaveExpense);
        btnDeleteExpense = view.findViewById(R.id.btnDeleteExpense);
        btnCancelExpense = view.findViewById(R.id.btnCancelExpense);

        // 将支出的信息填充到 EditText 中
        if (expense != null) {
            etExpenseTitle.setText(expense.getTitle());
            etExpenseAmount.setText(String.valueOf(expense.getAmount()));
            etExpenseCategory.setText(expense.getCategory());
        }

        btnSaveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户输入的修改后的支出信息，然后更新到数据库
                String title = etExpenseTitle.getText().toString().trim();
                String amount = etExpenseAmount.getText().toString().trim();
                String category = etExpenseCategory.getText().toString().trim();

                saveOrUpdateExpense(expense,title,amount,category);
                updateExpenseList();
                updateExpenseListAndTotalAmount(getSelectedTimeRange());
                popupWindow.dismiss();
            }
        });
        btnDeleteExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除备忘录
                if (expense != null) {
                    databaseHelper.deleteExpense(expense.getId());
                    updateExpenseList();
                }
                popupWindow.dismiss();
            }
        });

        btnCancelExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
    private void saveOrUpdateExpense(Expense expense, String title, String amount, String category) {
        if (expense == null) {
            // 新建备忘录
            if (!title.isEmpty() && !amount.isEmpty() && !category.isEmpty()) {
                double expenseAmount = Double.parseDouble(amount);
                Expense newExpense = new Expense(title, expenseAmount, category, System.currentTimeMillis());
                databaseHelper.addExpense(newExpense);

            } else {
                Toast.makeText(ExpenseActivity.this, "请填写完整的支出信息", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!title.isEmpty() && !amount.isEmpty() && !category.isEmpty()) {
                double expenseAmount = Double.parseDouble(amount);
                expense.setTitle(title);
                expense.setAmount(expenseAmount);
                expense.setCategory(category);

                databaseHelper.updateExpense(expense);
            } else {
                Toast.makeText(ExpenseActivity.this, "请填写完整的支出信息", Toast.LENGTH_SHORT).show();
            }
        }
        updateExpenseList();
        updateExpenseListAndTotalAmount(TimeRange.TODAY);
    }
    // 切换DatePicker的可见性
    private void toggleDatePickerVisibility() {
        if (datePickerDialog != null && !datePickerDialog.getDatePicker().isShown()) {
            datePickerDialog.show();
        } else {
            datePickerDialog.dismiss();
        }
    }

    // 处理日期选择
    private void handleDateSelected(int year, int month, int day) {
        // 获取选择的日期，并更新支出清单和总额
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, day);

        // 根据选择的时间范围更新支出清单和总额
        updateExpenseListAndTotalAmount(selectedDate.getTime());
    }


    // 更新支出清单和总额，传入选择的日期
    private void updateExpenseListAndTotalAmount(Date selectedDate) {
        // 根据时间范围从数据库中获取支出清单和总额
        List<Expense> expenses = databaseHelper.getExpensesBySelectedDate(selectedDate);
        double totalAmount = databaseHelper.getTotalAmountBySelectedDate(selectedDate);

        // 更新RecyclerView
        expenseAdapter.updateExpenseList(expenses);

        // 更新显示总额的文本视图
        totalAmountTextView.setText(String.format(Locale.getDefault(), "总额：%s", totalAmount));
        timeRangeSpinner.setSelection(0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_expense, menu);
        return true;
    }

    private void setupTimeRangeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.time_ranges,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeRangeSpinner.setAdapter(adapter);
        // 设置 Spinner 的默认选项为第 0 项
        timeRangeSpinner.setSelection(0);
        timeRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 根据选择的时间范围更新支出清单和总额
                updateExpenseListAndTotalAmount(getSelectedTimeRange());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 未选择时的处理
            }
        });
    }
    private TimeRange getSelectedTimeRange() {
        // 根据选择的时间范围返回相应的枚举值
        switch (timeRangeSpinner.getSelectedItemPosition()) {
            case 0:
                return TimeRange.TODAY;
            case 1:
                return TimeRange.MONTH;
            case 2:
                return TimeRange.YEAR;
            default:
                return TimeRange.TODAY;
        }
    }
    private void updateExpenseListAndTotalAmount(TimeRange timeRange) {
        // 根据时间范围从数据库中获取支出清单和总额
        List<Expense> expenses = databaseHelper.getExpensesByTimeRange(timeRange);
        double totalAmount = databaseHelper.getTotalAmountByTimeRange(timeRange);

        // 更新RecyclerView
        expenseAdapter.updateExpenseList(expenses);

        // 更新显示总额的文本视图
        totalAmountTextView.setText(String.format(Locale.getDefault(), "总额：%s", totalAmount));
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // 如果需要关闭当前备忘录页面，可以调用 finish()
    }
    private List<Expense> getExpenseList() {
        // 实现获取支出清单的逻辑
        return databaseHelper.getAllExpenses();
    }

    private void updateExpenseList() {
        expenseAdapter.updateExpenseList(getExpenseList());
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_clear_expense) {
            databaseHelper.clearExpenses();
            updateExpenseList();
            updateExpenseListAndTotalAmount(TimeRange.TODAY);
            return true;
        } else if (item.getItemId() == R.id.menu_go_to_setting_expense) {
            goToSettingActivity();
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }

    }
    private void goToSettingActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
