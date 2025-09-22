package com.app.ecom.controller;


import com.app.ecom.dto.address.AddressDto;
import com.app.ecom.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address/")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("addAddress/{userId}")
    public ResponseEntity<AddressDto> addAddress(@RequestBody AddressDto addressDto,@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.addAddress(addressDto,userId));
    }


    @GetMapping("getAllAddressForUser/{userId}")
    public ResponseEntity<List<AddressDto>> getAllAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getAllAddressesByUserId(userId));
    }

    @PutMapping("updateAddress/{userId}/{addressId}")
    public ResponseEntity<AddressDto> updateAddress
            (@RequestBody AddressDto addressDto,@PathVariable Long userId,@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.updateAddress(addressDto,userId,addressId));
    }

    @PatchMapping("updateAddressFields/{userId}/{addressId}")
    public ResponseEntity<AddressDto> updateAddressFiled
            (@RequestBody AddressDto addressDto,@PathVariable Long userId,@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.patchAddress(userId, addressId, addressDto));
    }

    @DeleteMapping("/deleteAddressById/{userId}/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long userId,@PathVariable Long addressId) {
        addressService.deleteAddress(userId,addressId);
        return ResponseEntity.ok("Address Deleted Successfully");
    }

}
