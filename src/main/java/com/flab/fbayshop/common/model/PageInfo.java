package com.flab.fbayshop.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageInfo {

    private int offset;

    private int size = 10;

    private int page = 0;

    private Sort sort;

    public PageInfo(int page, int size) {
        this.offset = (page - 1) * size;
        this.size = size;
    }

    public PageInfo(int page, int size, Sort sort) {
        this(page, size);
        this.sort = sort;
    }
}

