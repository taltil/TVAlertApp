package com.example.tvalert.DTO;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EpisodeLinks {
    @SerializedName("first")
    @Expose
    private Integer first;
    @SerializedName("last")
    @Expose
    private Integer last;
    @SerializedName("next")
    @Expose
    private Object next;
    @SerializedName("prev")
    @Expose
    private Object prev;

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getLast() {
        return last;
    }

    public void setLast(Integer last) {
        this.last = last;
    }

    public Object getNext() {
        return next;
    }

    public void setNext(Object next) {
        this.next = next;
    }

    public Object getPrev() {
        return prev;
    }

    public void setPrev(Object prev) {
        this.prev = prev;
    }
}
