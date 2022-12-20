package com.flab.fbayshop.common.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.flab.fbayshop.common.dto.request.PageRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SliceDto<T> implements Serializable {

    private List<T> items;

    private boolean hasNext;

    private Long nextCursor;

    public static <T extends PageResponse> SliceDto<T> of(List<T> data, PageRequest request) {

        if (data == null || data.size() == 0) {
            return new SliceDto<>(new ArrayList<>(), false, null);
        }

        boolean hasNext = false;
        Long nextCursor = null;

        if (data.size() > request.getSize()) {
            T removed = data.remove(request.getSize());
            hasNext = true;
            nextCursor = removed.getId();
        }

        return new SliceDto<>(data, hasNext, nextCursor);
    }
}
