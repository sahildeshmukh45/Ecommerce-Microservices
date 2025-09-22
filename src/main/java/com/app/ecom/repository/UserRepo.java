package com.app.ecom.repository;

import com.app.ecom.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {


    Page<User> findAll(Pageable pageable);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    boolean existsByPhoneNumber(String phoneNumber);
}
