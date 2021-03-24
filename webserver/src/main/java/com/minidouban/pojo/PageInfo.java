package com.minidouban.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PageInfo {
    private int pageNum;
    private int pageSize;

    private PageInfo() {
    }

    private PageInfo(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getOffset() {
        return getPageNum() * getPageSize();
    }

    void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public static PageInfo of(int pageNum, int pageSize) {
        return new PageInfo(pageNum, pageSize);
    }

    public PageInfo next() {
        setPageNum(getPageNum() + 1);
        return this;
    }

    public PageInfo previousOrFirst() {
        if (getPageNum() == 0) {
            setPageNum(0);
        } else {
            setPageNum(getPageNum() - 1);
        }
        return this;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }
}
