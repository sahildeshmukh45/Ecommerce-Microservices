package com.app.ecom.service;

import com.app.ecom.dto.address.AddressDto;
import com.app.ecom.entity.Address;
import com.app.ecom.entity.User;
import com.app.ecom.exception.UserNotFoundException;
import com.app.ecom.mapper.Mappers;
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

    public AddressDto addAddress(AddressDto addressDto,Long userId) {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User Not found"));

            // converting addressDto to address and assigning to one address
            Address address = Mappers.toAddress(addressDto);

            // also we have to set user for that address
            address.setUser(user);

            Address savedAddress = addressRepo.save(address);

            return Mappers.toAddressDto(savedAddress);
    }

    public List<AddressDto> getAllAddressesByUserId(Long userId){

        userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not found"));

        List<Address> addresses = addressRepo.findByUserId(userId);

        return addresses.stream()
                .map(Mappers::toAddressDto)
                .collect(Collectors.toList());
    };


    // put mapping  fully replace
    public AddressDto updateAddress(AddressDto addressDto, Long userId, Long addressId) {

        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not found"));

        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Address does not belong to this user");
        }

        address.setId(addressId);
        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setZipcode(addressDto.getZipcode());
        address.setCountry(addressDto.getCountry());

        address.setUser(user);

        Address updated =  addressRepo.save(address);
        return Mappers.toAddressDto(updated);
    }

    public AddressDto patchAddress(Long userId, Long addressId, AddressDto addressDto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Address does not belong to this user");
        }

        // Only update non-null fields by this logic we are only updating those field whose value
        //  we are getting like here if state value is nonnull that means use try to update state value
        if (addressDto.getStreet() != null) address.setStreet(addressDto.getStreet());
        if (addressDto.getCity() != null) address.setCity(addressDto.getCity());
        if (addressDto.getState() != null) address.setState(addressDto.getState());
        if (addressDto.getZipcode() != null) address.setZipcode(addressDto.getZipcode());
        if (addressDto.getCountry() != null) address.setCountry(addressDto.getCountry());

        Address updated = addressRepo.save(address);
        return Mappers.toAddressDto(updated);
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
