package org.book.commerce.productservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.book.commerce.common.entity.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Table(name="image")
public class Image extends BaseEntity {
    // todo 이후에 s3에 파일을 올려서 해당 파일을 보여줄 수 있도록 수정 예정

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imgId;

    @Column(name="imageName")
    private String name;

    @Column(name="imgUrl")
    private String imgUrl;

    @Column(name="productId")
    private Long productId;

}
