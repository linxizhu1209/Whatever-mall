package org.book.commerce.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.productservice.repository.ImageRepository;
import org.book.commerce.productservice.domain.Image;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ImageUploadService {

    private final ImageRepository imageRepository;

    public void upload(Long productId,String imgName, String imgUrl){
        Image image = Image.builder().imgUrl(imgUrl).productId(productId).name(imgName).build();
        imageRepository.save(image);
        log.info("이미지 저장 완료");
//        String orginName = productImage.getOriginalFilename();
//        String ext = orginName.substring(orginName.lastIndexOf("."));
//        String uuid = UUID.randomUUID().toString()+ext;
//        String imageUrl = orginName.
    }
}
