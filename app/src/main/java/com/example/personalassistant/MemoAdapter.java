package com.example.personalassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

    private final List<Memo> memoList;
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Memo memo);
    }

    public MemoAdapter(Context context, List<Memo> memoList, OnItemClickListener listener) {
        this.context = context;
        this.memoList = memoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.memo_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Memo memo = memoList.get(position);

        holder.textViewTitle.setText(memo.getTitle());
        holder.textViewContent.setText(memo.getContent());
        holder.textViewCreateTime.setText(DateTimeUtils.formatDateTime(memo.getCreateTime()));
        holder.textViewTags.setText(memo.getTags());

        // 检查前一个备忘录的标签是否相同，如果相同，则隐藏标签
        if (position > 0 && memo.hasSameTags(memoList.get(position - 1).getTags())) {
            holder.textViewTags.setVisibility(View.GONE);
        } else {
            holder.textViewTags.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(memo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewContent;
        TextView textViewCreateTime;
        TextView textViewTags;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            textViewCreateTime = itemView.findViewById(R.id.textViewCreateTime);
            textViewTags = itemView.findViewById(R.id.textViewTags);
        }
    }

    // 添加方法来更新备忘录列表
    public void updateMemoList(List<Memo> newMemoList) {
        memoList.clear();
        memoList.addAll(newMemoList);
        notifyDataSetChanged();
    }
}
