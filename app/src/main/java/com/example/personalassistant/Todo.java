package com.example.personalassistant;

public class Todo {
    private long id;
    private String title;
    private String category;
    private String description;
    private boolean isCompleted;
    private long dueDate;
    private boolean isReminderSet;
    private long reminderTime;

    private boolean isRepeating;  // 是否重复提醒
    private int repeatType;       // 重复提醒类型，例如每天、每周等
    private int repeatValue;      // 重复提醒的值，例如重复提醒每周的星期几

    public Todo() {
        // 默认构造函数
    }

    public Todo(String title, String category, String description, boolean isCompleted, long dueDate, boolean isReminderSet, long reminderTime,boolean isRepeating,int repeatType) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
        this.isReminderSet = isReminderSet;
        this.reminderTime = reminderTime;
        this.isRepeating=isRepeating;
        this.repeatType=repeatType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isReminderSet() {
        return isReminderSet;
    }

    public void setReminderSet(boolean reminderSet) {
        isReminderSet = reminderSet;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.reminderTime = reminderTime;
    }
    public boolean isRepeating() {
        return isRepeating;
    }

    public void setRepeating(boolean repeating) {
        isRepeating = repeating;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    public int getRepeatValue() {
        return repeatValue;
    }

    public void setRepeatValue(int repeatValue) {
        this.repeatValue = repeatValue;
    }
}
