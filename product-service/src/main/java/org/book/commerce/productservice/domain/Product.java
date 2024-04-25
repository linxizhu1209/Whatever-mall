package org.book.commerce.productservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.book.commerce.common.entity.BaseEntity;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Table(name="product")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_id")
    private Long productId;

    @Column(name = "name")
    private String name;

    @Column(name = "stock")
    private int stock;

    @Column(name = "price")
    private long price;

    @Column(name = "description")
    private String description;

    @Column(name="thumbnail_url")
    private String thumbnailUrl;

    @Column(name="thumbnail_name")
    private String thumbnailName; // todo 이미지 엔티티에 대표여부를 넣는게 더 나은지는 생각해볼것

}
