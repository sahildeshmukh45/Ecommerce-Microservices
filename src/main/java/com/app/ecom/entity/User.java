package com.app.ecom.entity;

import com.app.ecom.utility.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
import java.util.List;

@Table(name = "Users")
@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

//    @Enumerated(EnumType.STRING)
    private UserRole userRole= UserRole.CUSTOMER;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Address> address;

//    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
//    private List<CartItem> cartItem;
}
