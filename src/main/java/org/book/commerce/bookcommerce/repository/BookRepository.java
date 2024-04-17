package org.book.commerce.bookcommerce.repository;

import org.book.commerce.bookcommerce.repository.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
