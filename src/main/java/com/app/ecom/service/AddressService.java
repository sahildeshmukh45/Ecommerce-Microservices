package com.app.ecom.service;

import com.app.ecom.dto.address.AddressDto;
import com.app.ecom.entity.Address;
import com.app.ecom.entity.User;
import com.app.ecom.exception.UserNotFoundException;
import com.app.ecom.mapper.AddressMapper;
import com.app.ecom.repository.AddressRepo;
import com.app.ecom.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AddressService {

    private final AddressRepo addressRepo;
    private final UserRepo userRepo;
    private final AddressMapper addressMapper;

    public AddressDto addAddress(AddressDto addressDto, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not found"));

        Address address = addressMapper.toEntity(addressDto);
        address.setUser(user);

        Address savedAddress = addressRepo.save(address);

        return addressMapper.toDto(savedAddress);
    }

    public List<AddressDto> getAllAddressesByUserId(Long userId) {
        userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not found"));

        List<Address> addresses = addressRepo.findByUserId(userId);

        return addresses.stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }


    public AddressDto updateAddress(AddressDto addressDto, Long userId, Long addressId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not found"));

        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Address does not belong to this user");
        }

        // Use MapStruct to map all fields
        Address updatedAddress = addressMapper.toEntity(addressDto);
        updatedAddress.setId(addressId);
        updatedAddress.setUser(user);

        Address saved = addressRepo.save(updatedAddress);
        return addressMapper.toDto(saved);
    }

    public AddressDto patchAddress(Long userId, Long addressId, AddressDto addressDto) {
        userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Address does not belong to this user");
        }

        // Use MapStruct for partial updates (PATCH operation)
        addressMapper.updateAddressFromDto(addressDto, address);

        Address updated = addressRepo.save(address);
        return addressMapper.toDto(updated);
    }


    public void deleteAddress(Long userId, Long addressId) {

        User user = userRepo.findById(userId).orElseThrow(()-> new UserNotFoundException("User Not found"));

        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if(address.getUser().getId().equals(user.getId())) {
            addressRepo.delete(address);
        }else{
            throw new RuntimeException("Address does not belong to this user");
        }

    }

}
