package com.flab.fbayshop.common.dto.response;

import static java.lang.Math.*;

import java.io.Serializable;
import java.util.List;

import com.flab.fbayshop.common.model.PageInfo;

import lombok.Getter;

@Getter
public class PageDto<T> implements Serializable {

    private final List<T> data;

    private final int page;

    private final int size;

    private final int totalPages;

    private final boolean first;

    private final boolean last;

    public PageDto(List<T> data, int totalCnt, PageInfo pageInfo) {
        this.data = data;
        this.page = pageInfo.getPage();
        this.size = pageInfo.getSize();
        this.totalPages = (int)ceil(totalCnt / (double)size);
        this.first = page > 1;
        this.last = page == totalPages;
    }

}
