package org.book.commerce.bookcommerce.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class MyPageDto {
    private String email;
    private String address;
    private String phonenum;

}
