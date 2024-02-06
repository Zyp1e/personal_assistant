package com.example.personalassistant;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TodoDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo_database4";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TODO = "todo";
    private static final String COLUMN_TODO_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_COMPLETED = "is_completed";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_REMINDER_SET = "is_reminder_set";
    private static final String COLUMN_REMINDER_TIME = "reminder_time";
    private static final String COLUMN_IS_REPEATING = "is_repeating";
    private static final String COLUMN_REPEAT_TYPE = "repeat_type";
    private static final String COLUMN_REPEAT_VALUE = "repeat_value";

    public TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_TODO + " (" +
                COLUMN_TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_COMPLETED + " INTEGER, " +
                COLUMN_DUE_DATE + " INTEGER, " +
                COLUMN_REMINDER_SET + " INTEGER, " +
                COLUMN_REMINDER_TIME + " INTEGER,"+
                COLUMN_IS_REPEATING + " INTEGER, " +
                COLUMN_REPEAT_TYPE + " INTEGER, " +
                COLUMN_REPEAT_VALUE + " INTEGER)";
        db.execSQL(createTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    public void addTodo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, todo.getTitle());
        values.put(COLUMN_CATEGORY, todo.getCategory());
        values.put(COLUMN_DESCRIPTION, todo.getDescription());
        values.put(COLUMN_COMPLETED, todo.isCompleted() ? 1 : 0);
        values.put(COLUMN_DUE_DATE, todo.getDueDate());
        values.put(COLUMN_REMINDER_SET, todo.isReminderSet() ? 1 : 0);
        values.put(COLUMN_REMINDER_TIME, todo.getReminderTime());
        values.put(COLUMN_IS_REPEATING, todo.isRepeating() ? 1 : 0);
        values.put(COLUMN_REPEAT_TYPE, todo.getRepeatType());
        values.put(COLUMN_REPEAT_VALUE, todo.getRepeatValue());
        db.insert(TABLE_TODO, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public List<Todo> getAllTodos() {
        List<Todo> todoList = new ArrayList<>();
        String[] columns = {
                COLUMN_TODO_ID,
                COLUMN_TITLE,
                COLUMN_CATEGORY,
                COLUMN_DESCRIPTION,
                COLUMN_COMPLETED,
                COLUMN_DUE_DATE,
                COLUMN_REMINDER_SET,
                COLUMN_REMINDER_TIME,
                COLUMN_IS_REPEATING,
                COLUMN_REPEAT_TYPE,
                COLUMN_REPEAT_VALUE
        };

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_TODO, columns, null, null, null, null, COLUMN_DUE_DATE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Todo todo = new Todo();
                // 更新代码以确保正确检索每一列
                todo.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_TODO_ID)));
                todo.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                todo.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
                todo.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                todo.setCompleted(cursor.getInt(cursor.getColumnIndex(COLUMN_COMPLETED)) == 1);
                todo.setDueDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DUE_DATE)));
                todo.setReminderSet(cursor.getInt(cursor.getColumnIndex(COLUMN_REMINDER_SET)) == 1);
                todo.setReminderTime(cursor.getLong(cursor.getColumnIndex(COLUMN_REMINDER_TIME)));
                todo.setRepeating(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_REPEATING)) == 1);
                todo.setRepeatType(cursor.getInt(cursor.getColumnIndex(COLUMN_REPEAT_TYPE)));
                todo.setRepeatValue(cursor.getInt(cursor.getColumnIndex(COLUMN_REPEAT_VALUE)));
                // 添加日志输出
//                Log.d("TodoDatabaseHelper", "从数据库中读取：" + todo);

                todoList.add(todo);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return todoList;
    }


    public void deleteTodo(long todoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, COLUMN_TODO_ID + " = ?", new String[]{String.valueOf(todoId)});
        db.close();
    }
    public void updateTodo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, todo.getTitle());
        values.put(COLUMN_CATEGORY, todo.getCategory());
        values.put(COLUMN_DESCRIPTION, todo.getDescription());
        values.put(COLUMN_COMPLETED, todo.isCompleted() ? 1 : 0);
        values.put(COLUMN_DUE_DATE, todo.getDueDate());
        values.put(COLUMN_REMINDER_SET, todo.isReminderSet() ? 1 : 0);
        values.put(COLUMN_REMINDER_TIME, todo.getReminderTime());
        values.put(COLUMN_IS_REPEATING, todo.isRepeating() ? 1 : 0);
        values.put(COLUMN_REPEAT_TYPE, todo.getRepeatType());
        db.update(TABLE_TODO, values, COLUMN_TODO_ID + " = ?", new String[]{String.valueOf(todo.getId())});
        db.close();
    }
    public void clearAllTodos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, null, null);
        db.close();
    }

    @SuppressLint("Range")
    List<String> getAllCategories() {
        // 实现获取所有待办事项类别的逻辑
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> categories = new ArrayList<>();

        // 查询不重复的类别
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COLUMN_CATEGORY + " FROM " + TABLE_TODO, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return categories;
    }
    @SuppressLint("Range")
    public List<Todo> getFilteredTodos(List<String> categories, long selectedDate) {
        List<Todo> todoList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String[] columns = {
                COLUMN_TODO_ID,
                COLUMN_TITLE,
                COLUMN_CATEGORY,
                COLUMN_DESCRIPTION,
                COLUMN_COMPLETED,
                COLUMN_DUE_DATE,
                COLUMN_REMINDER_SET,
                COLUMN_REMINDER_TIME,
                COLUMN_IS_REPEATING,
                COLUMN_REPEAT_TYPE,
                COLUMN_REPEAT_VALUE
        };

        String selection = null;
        String[] selectionArgs = null;

        if (categories != null && !categories.isEmpty()) {
            StringBuilder categorySelection = new StringBuilder();
            categorySelection.append(COLUMN_CATEGORY).append(" IN (");
            for (int i = 0; i < categories.size(); i++) {
                categorySelection.append("?");
                if (i < categories.size() - 1) {
                    categorySelection.append(",");
                }
            }
            categorySelection.append(")");
            selection = categorySelection.toString();
            selectionArgs = categories.toArray(new String[0]);
        }

        if (selectedDate > 0) {
            String dateCondition = COLUMN_DUE_DATE + " >= ? AND " + COLUMN_DUE_DATE + " < ?";
            if (selection != null && !selection.isEmpty()) {
                selection += " AND " + dateCondition;
            } else {
                selection = dateCondition;
            }

            long startOfDay = getStartOfDay(selectedDate);
            long endOfDay = getEndOfDay(selectedDate);
            selectionArgs = selectionArgs != null ? Arrays.copyOf(selectionArgs, selectionArgs.length + 2) :
                    new String[2];
            selectionArgs[selectionArgs.length - 2] = String.valueOf(startOfDay);
            selectionArgs[selectionArgs.length - 1] = String.valueOf(endOfDay);
        }

        Cursor cursor = db.query(TABLE_TODO, columns, selection, selectionArgs, null, null, COLUMN_DUE_DATE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Todo todo = new Todo();
                // 更新代码以确保正确检索每一列
                todo.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_TODO_ID)));
                todo.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                todo.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
                todo.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                todo.setCompleted(cursor.getInt(cursor.getColumnIndex(COLUMN_COMPLETED)) == 1);
                todo.setDueDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DUE_DATE)));
                todo.setReminderSet(cursor.getInt(cursor.getColumnIndex(COLUMN_REMINDER_SET)) == 1);
                todo.setReminderTime(cursor.getLong(cursor.getColumnIndex(COLUMN_REMINDER_TIME)));
                todo.setRepeating(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_REPEATING)) == 1);
                todo.setRepeatType(cursor.getInt(cursor.getColumnIndex(COLUMN_REPEAT_TYPE)));
                todo.setRepeatValue(cursor.getInt(cursor.getColumnIndex(COLUMN_REPEAT_VALUE)));
//                Log.d("TodoDatabaseHelper", "从数据库中读取：" + todo);

                todoList.add(todo);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return todoList;
    }

    private long getStartOfDay(long timestamp) {
        // 获取一天的开始时间戳
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getEndOfDay(long timestamp) {
        // 获取一天的结束时间戳
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
    @SuppressLint("Range")
    public Todo getTodoById(long todoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Todo todo = null;

        String[] columns = {
                COLUMN_TODO_ID,
                COLUMN_TITLE,
                COLUMN_CATEGORY,
                COLUMN_DESCRIPTION,
                COLUMN_COMPLETED,
                COLUMN_DUE_DATE,
                COLUMN_REMINDER_SET,
                COLUMN_REMINDER_TIME,
                COLUMN_IS_REPEATING,
                COLUMN_REPEAT_TYPE,
                COLUMN_REPEAT_VALUE
        };

        String selection = COLUMN_TODO_ID + " = ?";
        String[] selectionArgs = {String.valueOf(todoId)};

        Cursor cursor = db.query(TABLE_TODO, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            todo = new Todo();
            todo.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_TODO_ID)));
            todo.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            todo.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
            todo.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
            todo.setCompleted(cursor.getInt(cursor.getColumnIndex(COLUMN_COMPLETED)) == 1);
            todo.setDueDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DUE_DATE)));
            todo.setReminderSet(cursor.getInt(cursor.getColumnIndex(COLUMN_REMINDER_SET)) == 1);
            todo.setReminderTime(cursor.getLong(cursor.getColumnIndex(COLUMN_REMINDER_TIME)));
            todo.setRepeating(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_REPEATING)) == 1);
            todo.setRepeatType(cursor.getInt(cursor.getColumnIndex(COLUMN_REPEAT_TYPE)));
            todo.setRepeatValue(cursor.getInt(cursor.getColumnIndex(COLUMN_REPEAT_VALUE)));
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return todo;
    }

}