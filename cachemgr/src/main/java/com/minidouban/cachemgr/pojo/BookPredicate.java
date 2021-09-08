package com.minidouban.cachemgr.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

// To query for tuples whose attributes are in [attr, attr1]
@Setter
@Getter
public class BookPredicate extends Book {
    private Integer commentNum1;
    private Float rating1;
    private Short pubYear1;
    private Float price1;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BookPredicate that = (BookPredicate) o;
        return Objects.equals(commentNum1, that.commentNum1) &&
                Objects.equals(rating1, that.rating1) &&
                Objects.equals(pubYear1, that.pubYear1) &&
                Objects.equals(price1, that.price1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentNum1, rating1, pubYear1, price1);
    }

    public BookPredicate adjust() {
        if (getTitle() != null && !"".equals(getTitle()))
            setTitle(getTitle().trim());
        if (getAuthors() != null && !"".equals(getAuthors()))
            setAuthors(getAuthors().trim());
        if (getPublisher() != null && !"".equals(getPublisher()))
            setPublisher(getPublisher().trim());
        if (getBrief() != null && !"".equals(getBrief()))
            setBrief(getBrief().trim());
        if (getCommentNum() != null || commentNum1 != null) {
            setCommentNum(getCommentNum());
            setCommentNum1(commentNum1);
            if (getCommentNum() == null) {
                setCommentNum(Integer.MIN_VALUE);
            }
            if (commentNum1 == null) {
                setCommentNum1(Integer.MAX_VALUE);
            }
        }
        if (getRating() != null || rating1 != null) {
            setRating(getRating());
            setRating1(rating1);
            if (getRating() == null) {
                setRating((float) 0);
            }
            if (rating1 == null) {
                setRating1((float) 10);
            }
        }
        if (getPubYear() != null || pubYear1 != null) {
            setPubYear(getPubYear());
            setPubYear1(pubYear1);
            if (getPubYear() == null) {
                setPubYear((short) 0);
            }
            if (pubYear1 == null) {
                setPubYear1((short) 3000);
            }
        }
        if (getPrice() != null || price1 != null) {
            setPrice(getPrice());
            setPrice1(price1);
            if (getPrice() == null) {
                setPrice(Float.MIN_VALUE);
            }
            if (price1 == null) {
                setPrice1(Float.MAX_VALUE);
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return "BookPredicate{" +
                "commentNum1=" + commentNum1 +
                ", rating1=" + rating1 +
                ", pubYear1=" + pubYear1 +
                ", price1=" + price1 +
                "} " + super.toString();
    }
}
