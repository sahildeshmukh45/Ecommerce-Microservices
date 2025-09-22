package com.app.ecom.repository;

import com.app.ecom.entity.Address;
import com.app.ecom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepo extends JpaRepository<Address,Long> {


    List<Address> findByUserId(Long userId);

    List<Address> findByUserIdIn(List<Long> userIds);

    Long user(User user);
}
