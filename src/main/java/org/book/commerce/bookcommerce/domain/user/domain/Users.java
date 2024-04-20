package org.book.commerce.bookcommerce.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.book.commerce.bookcommerce.common.entity.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Table(name="users")
public class Users extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name="name")
    private String name;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;
    
    @Column(name="address")
    private String address;

    @Column(name="phone_num")
    private String phoneNum;

    @Column(name="registration_num")
    private String registrationNum;

    @Column(name="role")
    @Enumerated(EnumType.ORDINAL)
    private Role role;
}
