package com.flab.fbayshop.common.dto.request;

import com.flab.fbayshop.common.model.PageInfo;
import com.flab.fbayshop.common.model.Sort;

import lombok.Getter;

@Getter
public class PageRequest {

    private int page = 1;

    private int size = 10;

    private String sortBy = "created_date";

    private String sortOrder = Sort.DESC;

    public static PageInfo of(int page, int size) {
        return new PageInfo(page, size);
    }

    public static PageInfo from(PageRequest pageRequest) {
        return new PageInfo(pageRequest.getPage(), pageRequest.getSize(),
            new Sort(pageRequest.getSortBy(), pageRequest.getSortOrder()));
    }
}
