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

    private List<T> data;

    private boolean hasNext;

    public static <T> SliceDto of(List<T> data, PageRequest request) {

        if (data == null || data.size() == 0) {
            return new SliceDto<>(new ArrayList<>(), false);
        }

        boolean hasNext = false;

        if (data.size() > request.getSize()) {
            data.remove(request.getSize());
            hasNext = true;
        }

        return new SliceDto<>(data, hasNext);
    }
}
