package com.flab.fbayshop.product.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Category {

    private Long categoryId;

    private String categoryName;

    private Integer categoryDepth;

}
