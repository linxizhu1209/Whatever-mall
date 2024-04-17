package org.book.commerce.bookcommerce.repository;

import org.book.commerce.bookcommerce.repository.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users,Long> {

}
