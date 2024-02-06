package com.example.personalassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .commit();

    }
    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//            setPreferencesFromResource(R.xml.preferences, rootKey);
            addPreferencesFromResource(R.xml.preferences);// 获取备忘录分类设置项
            EditTextPreference memoCategoryPreference = findPreference("memo_category");// 设置备忘录分类的变化监听器
            memoCategoryPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {// 在这里处理备忘录分类的变化
                    String newMemoCategory = (String) newValue;
                    return true; // 返回 true 表示允许保存变化
                }
            });

            // 获取待办事项分类设置项
            EditTextPreference todoCategoryPreference = findPreference("todo_category");// 设置待办事项分类的变化监听器
            todoCategoryPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {// 在这里处理待办事项分类的变化
                    String newTodoCategory = (String) newValue;
                    return true;
                }
            });

            // 获取记账分类设置项
            EditTextPreference expenseCategoryPreference = findPreference("expense_category");// 设置记账分类的变化监听器
            expenseCategoryPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newExpenseCategory = (String) newValue;
                    return true;
                }
            });
        }
    }
}
