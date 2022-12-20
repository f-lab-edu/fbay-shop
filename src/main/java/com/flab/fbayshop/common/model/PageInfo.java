package com.flab.fbayshop.common.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageInfo {

    private int offset;

    private int size = 10;

    private int page = 0;

    private List<Sort> sort;

    private List<Filter> filter;

    public PageInfo(int page, int size) {
        this.offset = (page - 1) * size;
        this.size = size;
    }

    public PageInfo(int page, int size, List<Sort> sort) {
        this(page, size);
        this.sort = sort;
    }

    public PageInfo(int page, int size, List<Sort> sort, List<Filter> filter) {
        this(page, size);
        this.sort = sort;
        this.filter = filter;
    }
}

