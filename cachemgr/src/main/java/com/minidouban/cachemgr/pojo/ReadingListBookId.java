package com.minidouban.cachemgr.pojo;

import java.io.Serializable;
import java.util.Objects;

public class ReadingListBookId implements Serializable {
    private long listId;

    private long bookId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ReadingListBookId that = (ReadingListBookId) o;
        return listId == that.listId && bookId == that.bookId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(listId, bookId);
    }
}
