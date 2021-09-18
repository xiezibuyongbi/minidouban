package com.minidouban.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Accessors(chain = true, fluent = false)
@Getter
@Setter
//TODO 级联删除缓存
public class MQDelCacheMsg {
    public static Map<String, BusinessName> businessNameMap;

    static {
        BusinessName[] businessNames = BusinessName.values();
        businessNameMap = new HashMap<>(businessNames.length);
        for (BusinessName businessName : businessNames) {
            businessNameMap.put(businessName.name(), businessName);
        }
    }

    private BusinessName businessName;
    private CacheOperation operation;
    private String entryId;

    public enum CacheOperation {
        DELETE {
            @Override
            public String toString() {
                return "delete";
            }
        }
    }

    public enum BusinessName {

        USER {
            public String getName() {
                return User.getTableName();
            }
        },

        READING_LIST {
            public String getName() {
                return ReadingList.getTableName();
            }
        },

        READING_LIST_BOOK {
            public String getName() {
                return ReadingListBook.getTableName();
            }
        }
    }
}
