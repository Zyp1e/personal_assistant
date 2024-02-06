package com.example.personalassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MemoActivity extends AppCompatActivity {

    private MemoDatabaseHelper databaseHelper;
    private RecyclerView recyclerViewMemo;
    private MemoAdapter memoAdapter;
    private FloatingActionButton fabAddMemo;
    private PopupWindow popupWindow; // 全局变量保存PopupWindow实例
    private EditText editTextTitle, editTextContent, editTextTags; // 全局变量保存EditText实例
    private String currentFilterTag = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        databaseHelper = new MemoDatabaseHelper(this);
        Toolbar toolbar = findViewById(R.id.memoToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 为返回箭头按钮设置点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });

        Button btnFilter = findViewById(R.id.btnFilter_memo);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        recyclerViewMemo = findViewById(R.id.recyclerViewMemo);
        recyclerViewMemo.setLayoutManager(new LinearLayoutManager(this));
        memoAdapter = new MemoAdapter(this, getMemoList(), new MemoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Memo memo) {
                showEditMemoPopup(memo);
            }
        });
        recyclerViewMemo.setAdapter(memoAdapter);

        fabAddMemo = findViewById(R.id.fabAddMemo);
        fabAddMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditMemoPopup(null);
            }
        });
    }


    private void saveOrUpdateMemo(Memo memo, String title, String content, String tags) {
        long createTime = System.currentTimeMillis();
        if (memo == null) {
            // 新建备忘录
            Memo newMemo = new Memo(title, content, createTime,tags);
            databaseHelper.addMemo(newMemo);
//            Log.d("MemoActivity", "New memo added: " + newMemo.toString());
        } else {
            // 更新备忘录
            memo.setTitle(title);
            memo.setContent(content);
            memo.setCreateTime(createTime);
            memo.setTags(tags);
            databaseHelper.updateMemo(memo);
//            Log.d("MemoActivity", "Memo updated: " + memo.toString());
        }
        updateMemoList();
    }

    private void showEditMemoPopup(Memo memo) {
        View view = LayoutInflater.from(this).inflate(R.layout.popup_edit_memo, null);
        popupWindow = new PopupWindow(view, RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT, true);

        ColorDrawable background = new ColorDrawable(android.graphics.Color.WHITE);
        popupWindow.setBackgroundDrawable(background);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextContent = view.findViewById(R.id.editTextContent);
        editTextTags = view.findViewById(R.id.editTextTags);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnReturn = view.findViewById(R.id.btnReturn);

        if (memo != null) {
            editTextTitle.setText(memo.getTitle());
            editTextContent.setText(memo.getContent());
            editTextTags.setText(memo.getTags());
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MemoActivity", "Save button clicked");
                // 保存备忘录
                saveOrUpdateMemo(memo, editTextTitle.getText().toString().trim(), editTextContent.getText().toString().trim(), editTextTags.getText().toString().trim());
                updateMemoList();
                popupWindow.dismiss();

                Log.d("MemoActivity", "Memo saved");
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除备忘录
                if (memo != null) {
                    databaseHelper.deleteMemo(memo.getId());
                    updateMemoList();
                }
                popupWindow.dismiss();
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回按钮，直接关闭PopupWindow
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_memo, menu);
        return true;
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // 如果需要关闭当前备忘录页面，可以调用 finish()
    }
    private void goToSettingActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
    private void sortMemosByDate() {
        // 按创建日期排序
        List<Memo> sortedMemos = new ArrayList<>(getMemoList());
        Collections.sort(sortedMemos, new Comparator<Memo>() {
            @Override
            public int compare(Memo memo1, Memo memo2) {
                return Long.compare(memo1.getCreateTime(), memo2.getCreateTime());
            }
        });
        memoAdapter.updateMemoList(sortedMemos);
    }

    private void sortMemosByTitle() {
        // 按标题排序
        List<Memo> sortedMemos = new ArrayList<>(getMemoList());
        Collections.sort(sortedMemos, new Comparator<Memo>() {
            @Override
            public int compare(Memo memo1, Memo memo2) {
                return memo1.getTitle().compareTo(memo2.getTitle());
            }
        });
        memoAdapter.updateMemoList(sortedMemos);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_clear_memo) {
            databaseHelper.clearMemos();
            updateMemoList();
            return true;
        } else if (item.getItemId() == R.id.menu_go_to_setting_todo) {
            goToSettingActivity();
            return true;
        } else if(item.getItemId() == R.id.menu_sort_by_date){
            sortMemosByDate();
            return true;
        } else if(item.getItemId() == R.id.menu_sort_by_title) {
            sortMemosByTitle();
            return true;
        }else if(item.getItemId() == R.id.menu_group_sort_by_tags){
            sortByTags();
            return true;
        }else{
                return super.onOptionsItemSelected(item);
        }

    }

    private void sortByTags() {
        // 获取所有备忘录的标签
        List<String> allTags = getAllTags();

        // 创建一个 Map，将每个标签与其对应的备忘录列表关联起来
        Map<String, List<Memo>> memoMap = new HashMap<>();
        for (String tag : allTags) {
            List<Memo> memosWithTag = getMemosWithTag(tag);
            memoMap.put(tag, memosWithTag);
        }

        // 构建按标签分类的备忘录列表
        List<Memo> sortedMemos = new ArrayList<>();
        for (String tag : allTags) {
            List<Memo> memosWithTag = memoMap.get(tag);
            sortedMemos.addAll(memosWithTag);
        }

        // 更新备忘录列表
        memoAdapter.updateMemoList(sortedMemos);
    }



    private List<Memo> getMemoList() {
        // 从数据库获取备忘录列表的方法
        return databaseHelper.getAllMemos();
    }

    private void updateMemoList() {
        // 更新备忘录列表的方法
        List<Memo> newMemoList = getMemoList();
        Collections.sort(newMemoList, new Comparator<Memo>() {
            @Override
            public int compare(Memo memo1, Memo memo2) {
                return memo1.getTitle().compareTo(memo2.getTitle());
            }
        });

        memoAdapter.updateMemoList(newMemoList);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择标签");

        List<String> allTags = getAllTags();
        final CharSequence[] tagsArray = allTags.toArray(new CharSequence[0]);

        builder.setSingleChoiceItems(tagsArray, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 用户选择了某个标签
                currentFilterTag = tagsArray[which].toString();
                filterMemosByTag(currentFilterTag);
                dialog.dismiss();
            }
        });

        // 添加取消筛选的选项
        builder.setNegativeButton("清除筛选", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentFilterTag = null; // 取消筛选
                updateMemoList();
                dialog.dismiss();
            }
        });

        builder.show();
    }


    private List<String> getAllTags() {
        // 从数据库获取所有备忘录的标签
        List<String> allTags = new ArrayList<>();
        List<Memo> allMemos = getMemoList();

        for (Memo memo : allMemos) {
            String tags = memo.getTags();
            if (tags != null && !tags.isEmpty()) {
                // 分割标签字符串，添加到标签列表
                String[] tagArray = tags.split(",");
                allTags.addAll(Arrays.asList(tagArray));
            }
        }

        // 去重
        Set<String> uniqueTags = new HashSet<>(allTags);
        allTags.clear();
        allTags.addAll(uniqueTags);

        return allTags;
    }

    private void filterMemosByTag(String tag) {
        List<Memo> filteredMemos = new ArrayList<>();

        for (Memo memo : getMemoList()) {
            String tags = memo.getTags();
            if (tags != null && tags.contains(tag)) {
                filteredMemos.add(memo);
            }
        }

        memoAdapter.updateMemoList(filteredMemos);
    }

    private List<Memo> getMemosWithTag(String tag) {
        // 获取具有特定标签的备忘录列表
        List<Memo> memosWithTag = new ArrayList<>();

        for (Memo memo : getMemoList()) {
            String tags = memo.getTags();
            if (tags != null && tags.contains(tag)) {
                memosWithTag.add(memo);
            }
        }

        return memosWithTag;
    }

}
