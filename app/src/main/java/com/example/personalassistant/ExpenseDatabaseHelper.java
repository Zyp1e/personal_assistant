// ExpenseDatabaseHelper.java

package com.example.personalassistant;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpenseDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expense_database";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_EXPENSE = "expense";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_TIMESTAMP = "timestamp";

//    public enum TimeRange {
//        TODAY,
//        MONTH,
//        YEAR
//    }
    public ExpenseDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_EXPENSE + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_AMOUNT + " REAL, "
                + COLUMN_CATEGORY + " TEXT, "
                + COLUMN_TIMESTAMP + " INTEGER"
                + ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        onCreate(db);
    }

    public void addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, expense.getTitle());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_CATEGORY, expense.getCategory());
        values.put(COLUMN_TIMESTAMP, expense.getTimestamp());
        db.insert(TABLE_EXPENSE, null, values);
        db.close();
    }

    public void updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, expense.getTitle());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_CATEGORY, expense.getCategory());
        values.put(COLUMN_TIMESTAMP, expense.getTimestamp());
        db.update(TABLE_EXPENSE, values, COLUMN_ID + " = ?", new String[]{String.valueOf(expense.getId())});
        db.close();
    }

    public void deleteExpense(long expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENSE, COLUMN_ID + " = ?", new String[]{String.valueOf(expenseId)});
        db.close();
    }

    @SuppressLint("Range")
    public List<Expense> getAllExpenses() {
        List<Expense> expenseList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                expense.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT)));
                expense.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
                expense.setTimestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenseList;
    }
    @SuppressLint("Range")
    public List<Expense> getExpensesByTimeRange(TimeRange timeRange) {
        List<Expense> expenseList = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        long startTime;

        switch (timeRange) {
            case TODAY:
                startTime = getStartOfDay(currentTime);
                break;
            case MONTH:
                startTime = getStartOfMonth(currentTime);
                break;
            case YEAR:
                startTime = getStartOfYear(currentTime);
                break;
            default:
                startTime = 0;
                break;
        }

        String selectQuery = "SELECT * FROM " + TABLE_EXPENSE +
                " WHERE " + COLUMN_TIMESTAMP + " >= " + startTime +
                " AND " + COLUMN_TIMESTAMP + " <= " + currentTime;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                expense.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT)));
                expense.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
                expense.setTimestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenseList;
    }

    // 辅助方法，获取一天的起始时间戳
    private long getStartOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    // 辅助方法，获取一个月的起始时间戳
    private long getStartOfMonth(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    // 辅助方法，获取一年的起始时间戳
    private long getStartOfYear(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }
    public double getTotalAmountByTimeRange(TimeRange timeRange) {
        long currentTime = System.currentTimeMillis();
        long startTime;

        switch (timeRange) {
            case TODAY:
                startTime = getStartOfDay(currentTime);
                break;
            case MONTH:
                startTime = getStartOfMonth(currentTime);
                break;
            case YEAR:
                startTime = getStartOfYear(currentTime);
                break;
            default:
                startTime = 0;
                break;
        }

        String selectQuery = "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_EXPENSE +
                " WHERE " + COLUMN_TIMESTAMP + " >= " + startTime +
                " AND " + COLUMN_TIMESTAMP + " <= " + currentTime;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        double totalAmount = 0;
        if (cursor.moveToFirst()) {
            totalAmount = cursor.getDouble(0);
        }

        cursor.close();
        db.close();
        return totalAmount;
    }
    public void clearExpenses() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_EXPENSE);
        db.close();
    }
    public double getTotalAmountBySelectedDate(Date selectedDate) {
        long startTime = getStartOfDay(selectedDate.getTime());
        long endTime = getEndOfDay(selectedDate.getTime());

        String selectQuery = "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_EXPENSE +
                " WHERE " + COLUMN_TIMESTAMP + " >= " + startTime +
                " AND " + COLUMN_TIMESTAMP + " <= " + endTime;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        double totalAmount = 0;
        if (cursor.moveToFirst()) {
            totalAmount = cursor.getDouble(0);
        }

        cursor.close();
        db.close();
        return totalAmount;
    }

    @SuppressLint("Range")
    public List<Expense> getExpensesBySelectedDate(Date selectedDate) {
        List<Expense> expenseList = new ArrayList<>();
        long startTime = getStartOfDay(selectedDate.getTime());
        long endTime = getEndOfDay(selectedDate.getTime());

        String selectQuery = "SELECT * FROM " + TABLE_EXPENSE +
                " WHERE " + COLUMN_TIMESTAMP + " >= " + startTime +
                " AND " + COLUMN_TIMESTAMP + " <= " + endTime;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                expense.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT)));
                expense.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
                expense.setTimestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenseList;
    }
    // 获取一天的结束时间戳
    private long getEndOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTimeInMillis();
    }
}
