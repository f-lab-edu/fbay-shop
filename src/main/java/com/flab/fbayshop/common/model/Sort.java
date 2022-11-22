package com.flab.fbayshop.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Sort {

    public static final String ASC = "asc";

    public static final String DESC = "desc";

    private String sortBy;

    private String sortOrder;

}
