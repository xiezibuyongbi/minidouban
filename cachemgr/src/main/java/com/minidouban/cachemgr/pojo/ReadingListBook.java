package com.minidouban.cachemgr.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReadingListBook {
    private long listId;

    private long bookId;

    public static String getTableName() {
        return "ReadingList_Book";
    }
}
