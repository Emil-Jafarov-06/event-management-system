package com.example.eventmanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPage <T>{

    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private List<T> content;

    public MyPage(Page<T> page) {
        this.currentPage=page.getNumber();
        this.pageSize=page.getSize();
        this.totalPages=page.getTotalPages();
        this.totalElements=page.getTotalElements();
        this.content=page.getContent();
    }

}
