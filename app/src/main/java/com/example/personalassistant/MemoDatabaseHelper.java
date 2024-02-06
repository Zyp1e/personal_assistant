package com.example.personalassistant;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MemoDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "memo_database";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_MEMO = "memo";
    private static final String COLUMN_MEMO_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_CREATE_TIME = "create_time";
    private static final String COLUMN_TAGS = "tags"; // 添加标签列


    public MemoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_MEMO + " (" +
                COLUMN_MEMO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_TAGS + " TEXT, " +
                COLUMN_CREATE_TIME + " INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // 如果版本号小于2，执行升级操作
            String upgradeQuery = "ALTER TABLE " + TABLE_MEMO + " ADD COLUMN " + COLUMN_TAGS + " TEXT";
            db.execSQL(upgradeQuery);
        }
    }

    public void addMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, memo.getTitle());
        values.put(COLUMN_CONTENT, memo.getContent());
        values.put(COLUMN_TAGS, memo.getTags()); // 添加标签数据
        values.put(COLUMN_CREATE_TIME, memo.getCreateTime());
        db.insert(TABLE_MEMO, null, values);
        db.close();
    }

    public void updateMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, memo.getTitle());
        values.put(COLUMN_CONTENT, memo.getContent());
        values.put(COLUMN_TAGS, memo.getTags()); // 更新标签数据
        values.put(COLUMN_CREATE_TIME, memo.getCreateTime());
        db.update(TABLE_MEMO, values, COLUMN_MEMO_ID + " = ?", new String[]{String.valueOf(memo.getId())});
        db.close();
    }

    public void deleteMemo(long memoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEMO, COLUMN_MEMO_ID + " = ?", new String[]{String.valueOf(memoId)});
        db.close();
    }

    public void clearMemos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MEMO);
        db.close();
    }

    @SuppressLint("Range")
    public List<Memo> getAllMemos() {
        List<Memo> memoList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEMO + " ORDER BY " + COLUMN_CREATE_TIME + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Memo memo = new Memo();
                memo.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_MEMO_ID)));
                memo.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                memo.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
                memo.setTags(cursor.getString(cursor.getColumnIndex(COLUMN_TAGS))); // 获取标签数据
                memo.setCreateTime(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATE_TIME)));
                memoList.add(memo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return memoList;
    }
}
