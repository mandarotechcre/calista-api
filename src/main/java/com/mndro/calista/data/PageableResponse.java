package com.mndro.calista.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Setter
@Getter
@Builder(toBuilder = true)
public class PageableResponse<T> {
    private Long totalElements;
    private Integer totalPage;
    private Boolean isFirst;
    private Boolean isLast;
    private Integer pageSize;
    private Integer pageNumber;
    private Boolean isEmpty;
    private List<T> data;

    public static <T> PageableResponse<T> fromPage(Page<T> page) {
        return PageableResponse.<T>builder()
                .totalElements(page.getTotalElements())
                .totalPage(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .isEmpty(page.isEmpty())
                .data(page.getContent())
                .build();
    }
}
