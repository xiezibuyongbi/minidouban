package com.minidouban.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Book {
    private long bookId = -1;
    private String title;

    private String coverLink;
    private String authors;

    private Integer commentNum;
    private String brief;
    private String link;
    private Float rating;
    private String publisher;

    private Short pubYear;
    private Float price;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Book)) {
            return false;
        }
        return bookId == ((Book) obj).bookId;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", coverLink='" + coverLink + '\'' +
                ", authors='" + authors + '\'' +
                ", commentNum=" + commentNum +
                ", brief='" + brief + '\'' +
                ", link='" + link + '\'' +
                ", rating=" + rating +
                ", publisher='" + publisher + '\'' +
                ", pubYear=" + pubYear +
                ", price=" + price +
                '}';
    }
}
