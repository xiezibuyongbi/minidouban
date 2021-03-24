package com.minidouban.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Setter;

import java.util.List;

@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Page<T> {
    private List<T> content;
    private PageInfo pageInfo;

    public Page() {
    }

    public Page(PageInfo pageInfo, List<T> content) {
        this.pageInfo = pageInfo;
        this.content = content;
    }

    public List<T> getContent() {
        return content;
    }

    public boolean isEmpty() {
        return getContent().isEmpty();
    }
    /*public Slice<T> recover() {
        SliceImpl<T> slice = new SliceImpl<T>(new ArrayList<>());
        Class<?> clazz = slice.getClass();
        try {
            Field contentField = clazz.getSuperclass().getDeclaredField("content");
            contentField.setAccessible(true);
            contentField.set(slice, content);
            Field pageableField = clazz.getDeclaredField("pageable");
            pageableField.setAccessible(true);
            pageableField.set(slice, PageRequest.of(page, size));
            Field hasNextField = clazz.getDeclaredField("hasNext");
            hasNextField.setAccessible(true);
            hasNextField.set(slice, hasNext);
        } catch (NoSuchFieldException |
                IllegalAccessException e) {
            e.printStackTrace();
        }
        return slice;
    }*/
}
