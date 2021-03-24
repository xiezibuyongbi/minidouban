package com.minidouban.dao;

import com.minidouban.pojo.ReadingList;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadingListRepository {
    public List<ReadingList> findByUserId(@Param("userId") long userId);

    public int insert(@Param("userId") long userId, @Param("listName") String listName);

    public ReadingList findByUserIdAndListId(@Param("userId") long userId, @Param("listId") long listId);

    public ReadingList findByUserIdAndListName(@Param("userId") long userId, @Param("listName") String listName);

    public int updateListName(@Param("userId") long userId, @Param("oldListName") String oldListName, @Param("desiredListName") String desiredListName);

    public int deleteByUserIdAndListId(@Param("userId") long userId, @Param("listId") long listId);

    public int deleteAllByUserId(@Param("userId") long userId);


    public boolean existsById(@Param("listId") long listId);
}
