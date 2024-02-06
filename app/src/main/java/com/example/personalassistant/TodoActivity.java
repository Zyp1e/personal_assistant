package com.example.personalassistant;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodoActivity extends AppCompatActivity {

    private TodoDatabaseHelper databaseHelper;
    private RecyclerView recyclerViewTodo;
    private TodoAdapter todoAdapter;
    private FloatingActionButton fabAddTodo;
    private PopupWindow popupWindow;
    private EditText editTextTitle, editTextCategory, editTextDescription;
    private Button btnSave, btnDelete, btnReturn, btnDueDate;

    public static final String ACTION_SET_REMINDER = "com.example.personalassistant.SET_REMINDER";
    public static final String ACTION_CANCEL_REMINDER = "com.example.personalassistant.CANCEL_REMINDER";
    private static final String TAG = "TodoActivity";
    private AlarmManager alarmManager;
    // 添加成员变量
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private String currentFilterCategory = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        databaseHelper = new TodoDatabaseHelper(this);
        Toolbar toolbar = findViewById(R.id.todoToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(TodoActivity.ACTION_SET_REMINDER);
        filter.addAction(TodoActivity.ACTION_CANCEL_REMINDER);
        AlarmReceiver receiver = new AlarmReceiver();
        registerReceiver(receiver, filter);
        // 为返回箭头按钮设置点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });

        Button btnFilter = findViewById(R.id.btnFilter_todo);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("TodoActivity", "Filter button clicked");
                showFilterDialog();
            }
        });

        recyclerViewTodo = findViewById(R.id.recyclerViewTodo);
        recyclerViewTodo.setLayoutManager(new LinearLayoutManager(this));
        todoAdapter = new TodoAdapter(this, getTodoList(), new TodoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Todo todo) {
                Log.d(TAG,"isReapting "+String.valueOf(todo.isRepeating()));
                showEditTodoPopup(todo);
            }
        });
        recyclerViewTodo.setAdapter(todoAdapter);

        fabAddTodo = findViewById(R.id.fabAddTodo);
        fabAddTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTodoPopup(null);
            }
        });

        // 初始化日期选择器
        calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    if (btnDueDate != null) {
                        // 保存选择的日期
                        calendar.set(year, monthOfYear, dayOfMonth);
                        btnDueDate.setText(getFormattedDate(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // 获取 DatePickerDialog 中的 DatePicker 控件，设置最小日期为当前日期
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMinDate(System.currentTimeMillis());
    }

    private void updateFilteredTodoList(String category, long startDate, long endDate) {
        Log.d("TodoActivity", "updateFilteredTodoList: Category - " + category + ", StartDate - " + startDate + ", EndDate - " + endDate);
        List<Todo> filteredTodos;
        if (category != null || startDate != -1 || endDate != -1) {
            filteredTodos = getFilteredTodoList(category, startDate, endDate);
        } else {
            filteredTodos = getTodoList(); // 如果所有条件都为空，显示所有待办事项
        }
        for (Todo todo : filteredTodos) {
            Log.d("TodoActivity", "updateFilteredTodoList: Todo ID - " + todo.getId() + ", DueDate - " + todo.getDueDate());
        }
        todoAdapter.updateTodoList(filteredTodos);
    }


    private String getFormattedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    private void showEditTodoPopup(Todo todo) {
        View view = LayoutInflater.from(this).inflate(R.layout.popup_edit_todo, null);
        popupWindow = new PopupWindow(view, RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT, true);

        ColorDrawable background = new ColorDrawable(android.graphics.Color.WHITE);
        popupWindow.setBackgroundDrawable(background);
        btnDueDate = view.findViewById(R.id.btnDueDate);

        TimePicker timePickerDueTime = view.findViewById(R.id.timePickerDueTime);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextCategory = view.findViewById(R.id.editTextCategory);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        btnSave = view.findViewById(R.id.btnSave);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnReturn = view.findViewById(R.id.btnReturn);

        // 获取重复提醒的相关控件
        CheckBox checkBoxRepeating = view.findViewById(R.id.checkBoxRepeating);
        Spinner spinnerRepeatType = view.findViewById(R.id.spinnerRepeatType);

        // 设置控件的初始值
        checkBoxRepeating.setChecked(todo != null && todo.isRepeating());
        spinnerRepeatType.setSelection(todo != null ? todo.getRepeatType() : 0);

        // 设置按钮监听器，显示日期选择器
        btnDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog.show();
//                showDatePickerDialog();
            }
        });

        if (todo != null) {
            editTextTitle.setText(todo.getTitle());
            editTextCategory.setText(todo.getCategory());
            editTextDescription.setText(todo.getDescription());

            // 如果是编辑现有待办事项，则设置按钮上显示原有的日期
            btnDueDate.setText(getFormattedDate(new Date(todo.getDueDate())));

            // 设置 TimePicker 的值
            calendar.setTimeInMillis(todo.getDueDate());
            timePickerDueTime.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePickerDueTime.setMinute(calendar.get(Calendar.MINUTE));
            checkBoxRepeating.setChecked(todo != null && todo.isRepeating());
            spinnerRepeatType.setSelection(todo != null ? todo.getRepeatType() : 0);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取重复提醒的值
                boolean isRepeating = checkBoxRepeating.isChecked();
                int repeatType = spinnerRepeatType.getSelectedItemPosition();
                // 获取 TimePicker 的值
                int hour = timePickerDueTime.getHour();
                int minute = timePickerDueTime.getMinute();

                // 合并日期和时间
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
//                Log.d(TAG,"isReapting1 "+String.valueOf(todo.isRepeating()));
                saveOrUpdateTodo(todo,
                        editTextTitle.getText().toString().trim(),
                        editTextCategory.getText().toString().trim(),
                        editTextDescription.getText().toString().trim(),
                        calendar.getTimeInMillis(),
                        isRepeating,
                        repeatType);
//                Log.d(TAG,"isReapting2 "+String.valueOf(todo.isRepeating()));
                updateTodoList();
                popupWindow.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (todo != null) {
                    deleteTodo(todo.getId());
                    cancelReminderAlarm(todo.getId());
                    updateTodoList();
                }
                popupWindow.dismiss();
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }


    private void saveOrUpdateTodo(Todo todo, String title, String category, String description, long dueDate, boolean isRepeating, long repeatType) {
        Log.d("AlarmReceiver", String.valueOf(dueDate));
        if (todo == null) {
            Todo newTodo = new Todo(title, category, description, false, dueDate, true,dueDate,isRepeating, (int)repeatType);
            databaseHelper.addTodo(newTodo);
            long newTodoId = newTodo.getId();
            setRepeatingReminder(newTodoId, isRepeating,(int)repeatType, dueDate);
            Log.d("TodoActivity", "New Todo ID: " + newTodoId);
//            Log.d("alarmreceiver","111111"+String.valueOf(todo.getId()));
        } else {


            todo.setTitle(title);
            todo.setCategory(category);
            todo.setDescription(description);
            todo.setDueDate(dueDate);
            boolean isReminderSet=true;
            todo.setReminderSet(isReminderSet);
            todo.setReminderTime(dueDate);
            todo.setRepeating(isRepeating);
            todo.setRepeatType((int)repeatType);
            databaseHelper.updateTodo(todo);
            Log.d("alarmreceiver", String.valueOf(isReminderSet));
            // 如果提醒被设置，启动相应的提醒
            if (isReminderSet) {

                setRepeatingReminder(todo.getId(), isRepeating,(int)repeatType, dueDate);
//                setReminderAlarm(todo.getId(), reminderTime);
            } else {
                // 如果提醒被取消，取消相应的提醒
                cancelReminderAlarm(todo.getId());
            }
        }
    }

    private void setRepeatingReminder(long todoId,boolean isRepeating, int repeatType, long reminderTime) {
        Intent intent = new Intent(TodoActivity.this, AlarmReceiver.class);
        intent.setAction(ACTION_SET_REMINDER);
        intent.putExtra("TODO_ID", todoId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(TodoActivity.this, (int) todoId, intent, PendingIntent.FLAG_MUTABLE);


        long repeatInterval = calculateRepeatInterval(repeatType);

        // 将 reminderTime 加上八个小时
        Calendar calendar = Calendar.getInstance(   );
        calendar.setTimeInMillis(reminderTime);
//        calendar.add(Calendar.HOUR_OF_DAY, 8);
        long triggerTime = calendar.getTimeInMillis();
        long currentTime = System.currentTimeMillis();
        // 将 triggerTime 转换为可读的时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String triggerTimeStr = sdf.format(new Date(triggerTime));
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String triggerTimeStr2 = sdf2.format(new Date(currentTime));
        Log.d("AlarmReceiver", "remindertime " + String.valueOf(triggerTime) + ", formatted: " + triggerTimeStr);
        Log.d("AlarmReceiver", "remindertime2 " + String.valueOf(currentTime) + ", formatted: " + triggerTimeStr2);
        Log.d("AlarmReceiver", "Setting reminder for Todo ID: " + todoId);
        Log.d("AlarmReceiver", "Trigger time: " + triggerTime);
        if(isRepeating)
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, repeatInterval, pendingIntent);
        else
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

    }
    // 添加计算重复提醒间隔的方法
    private long calculateRepeatInterval(int repeatType) {
        switch (repeatType) {
            case 0: // 每天
                return AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            case 1: // 每周
                return AlarmManager.INTERVAL_DAY;
            case 2: // 每分钟
                return AlarmManager.INTERVAL_DAY * 7;
            default:
                return AlarmManager.INTERVAL_DAY;
        }
    }


    // 添加取消提醒的方法
    private void cancelReminder(long todoId) {
        Intent intent = new Intent(TodoActivity.this, AlarmReceiver.class);
        intent.setAction(ACTION_CANCEL_REMINDER);
        intent.putExtra("TODO_ID", todoId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(TodoActivity.this, (int) todoId, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
    }

    // 修改 deleteTodo 方法
    private void deleteTodo(long todoId) {
        // 先取消相应的提醒
        cancelReminder(todoId);
        databaseHelper.deleteTodo(todoId);
    }

    private void cancelReminderAlarm(long todoId) {
        Intent intent = new Intent(TodoActivity.this, AlarmReceiver.class);
        intent.setAction(ACTION_CANCEL_REMINDER);
        intent.putExtra("TODO_ID", todoId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(TodoActivity.this, (int) todoId, intent, PendingIntent.FLAG_IMMUTABLE); // 添加 FLAG_IMMUTABLE

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }


    private void updateTodoList() {
        todoAdapter.updateTodoList(getTodoList());
    }

    private List<Todo> getTodoList() {
        // 实现获取待办事项列表的逻辑
        return databaseHelper.getAllTodos();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showFilterDialog() {
//        Log.d("TodoActivity", "Showing filter dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择筛选方式");

        final CharSequence[] filterOptions = {"按种类", "按约定日期"};


        builder.setSingleChoiceItems(filterOptions, -1, (dialog, which) -> {
            dialog.dismiss();

            if (which == 0) {
                // 按种类筛选
                showCategoryFilterDialog();
            } else if (which == 1) {
                // 按约定日期筛选
                showDateFilterDialog();
            }
        });

        builder.show();
    }
    private void showCategoryFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择类别");

        List<String> allCategories = getAllCategories();
        final CharSequence[] categoriesArray = allCategories.toArray(new CharSequence[0]);

        builder.setSingleChoiceItems(categoriesArray, -1, (dialog, which) -> {
            // 用户选择了某个类别
            currentFilterCategory = categoriesArray[which].toString();
//            Log.d("TodoActivity", "Selected Category: " + currentFilterCategory); // 添加此行日志
            dialog.dismiss();

            // 筛选并更新待办事项列表
            updateFilteredTodoList(currentFilterCategory, -1, -1);
        });

        // 添加取消筛选的选项
        builder.setNegativeButton("取消筛选", (dialog, which) -> {
            // 用户点击取消筛选按钮
            currentFilterCategory = null;
            updateTodoList(); // 直接更新待办事项列表
            dialog.dismiss();
        });

        builder.show();
    }
    private void showDateFilterDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, monthOfYear, dayOfMonth);
            // 筛选并更新待办事项列表
            updateFilteredTodoList(null, selectedDate.getTimeInMillis(), -1);
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.setTitle("选择约定日期");

        // 添加取消筛选的选项
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消筛选", (dialog, which) -> {
            // 用户点击取消筛选按钮
            updateFilteredTodoList(null, -1, -1);
        });

        datePickerDialog.show();
    }




    private List<String> getAllCategories() {
        // 实现获取所有待办事项类别的逻辑
        // 从数据库查询不重复的类别，并返回一个List<String>
        return databaseHelper.getAllCategories();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.menu_clear_todo){
            clearTodoList();
            return true;
        }else if(item.getItemId()==R.id.menu_go_to_setting_todo) {
            goToSettingActivity();
            return true;
        }else{
                return super.onOptionsItemSelected(item);
        }

    }
    private void goToSettingActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private void clearTodoList() {
        List<Todo> allTodos = databaseHelper.getAllTodos();
        for (Todo todo : allTodos) {
            cancelReminder(todo.getId());
        }
        databaseHelper.clearAllTodos();
        updateTodoList();
    }
    private List<Todo> getFilteredTodoList(String category, long startDate, long endDate) {
//        Log.d("FilterDebug", "Entering updateFilteredTodoList");
        List<Todo> allTodos = databaseHelper.getAllTodos();
        List<Todo> filteredTodos = new ArrayList<>();

        for (Todo todo : allTodos) {
            // 类别筛选

            boolean category_empty=category == null || category.isEmpty();
            boolean categoryMatch = !category_empty && todo.getCategory().equalsIgnoreCase(category);
            boolean date_empty=startDate == -1;
            // 日期范围筛选
            boolean dateMatch = !date_empty &&
                    (todo.getDueDate() >= startDate && todo.getDueDate() <= endDate);

            // 将时间戳转换为年月日
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String todoDate = sdf.format(new Date(todo.getDueDate()));

            // 获取筛选条件的年月日
            String startDateString = sdf.format(new Date(startDate));
            String endDateString = sdf.format(new Date(endDate));

            // 年月日匹配
            boolean dateOnlyMatch = todoDate.equals(startDateString) || todoDate.equals(endDateString);

//            Log.d("FilterDebug", "Todo Date: " + todoDate);
//            Log.d("FilterDebug", "Start Date: " + startDateString);
//            Log.d("FilterDebug", "End Date: " + endDateString);
            Log.d("TodoActivity", "getFilteredTodoList: CategoryMatch - " + categoryMatch + ", DateMatch - " + dateMatch + ", DateOnlyMatch - " + dateOnlyMatch);

            if (categoryMatch && date_empty || category_empty &&  dateOnlyMatch) {
                filteredTodos.add(todo);
            }
        }

        return filteredTodos;
    }



}
