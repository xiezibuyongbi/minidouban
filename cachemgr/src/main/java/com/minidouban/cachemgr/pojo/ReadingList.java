package com.minidouban.cachemgr.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReadingList {
    private long listId;

    private String listName;

    private long userId;
}
