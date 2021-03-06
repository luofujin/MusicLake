package com.cyl.musiclake.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 功能：本地歌单
 * 作者：yonglong on 2016/9/13 21:59
 * 邮箱：643872807@qq.com
 * 版本：2.5
 */
public class Playlist extends DataSupport implements Serializable {
    //歌单id
    private String id;
    //歌单名
    private String name;
    //歌单名
    private int count;
    //创建日期
    private long date;
    private String order;
    private String coverUri;

    public Playlist() {
    }

    public Playlist(String id, String name, int count, long date, String order) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.date = date;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
    }

    @Override
    public String toString() {
        return name;
    }
}
