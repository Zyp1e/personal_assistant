package com.example.personalassistant;

public class Memo {
    private long id;
    private String title;
    private String content;
    private long createTime;
    private String tags;

    public Memo() {
        // 默认构造函数
    }

    public Memo(String title, String content, long createTime, String tags) {
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.tags = tags;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
    public boolean hasSameTags(String otherTags) {
        if (tags == null || otherTags == null) {
            return false;
        }
        return tags.equals(otherTags);
    }
}
