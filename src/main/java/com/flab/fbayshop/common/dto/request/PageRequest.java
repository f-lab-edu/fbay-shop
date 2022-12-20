package com.flab.fbayshop.common.dto.request;

import java.util.List;

import com.flab.fbayshop.common.model.Filter;
import com.flab.fbayshop.common.model.PageInfo;
import com.flab.fbayshop.common.model.Sort;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    private int page = 1;   // 페이지

    private String query;   // 검색어

    private Long cursor;    // 커서

    private int size = 10;  // 페이지 크기

    private List<Sort> sort;    // 데이터 정렬

    private List<Filter> filter;    // 필터

    public int getLimit() {
        return size + 1;
    }

    public static PageInfo of(int page, int size) {
        return new PageInfo(page, size);
    }

    public static PageInfo from(PageRequest pageRequest) {
        return new PageInfo(pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort(),
            pageRequest.getFilter());
    }

}
