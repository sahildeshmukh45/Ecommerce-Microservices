package com.app.ecom.mapper;

import com.app.ecom.dto.address.AddressDto;
import com.app.ecom.dto.productDtos.ProductRequest;
import com.app.ecom.dto.userDtos.UserRequest;
import com.app.ecom.dto.userDtos.UserResponse;
import com.app.ecom.entity.Address;
import com.app.ecom.entity.Product;
import com.app.ecom.entity.User;

public class Mappers {

    public static User mapToUser(UserRequest userRequest){
        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setEmail(userRequest.getEmail());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(userRequest.getPhoneNumber());



        return user;
    }
    public static Address toAddress(AddressDto addressDto) {

            Address address1 = new Address();
            address1.setZipcode(addressDto.getZipcode());
            address1.setStreet(addressDto.getStreet());
            address1.setCity(addressDto.getCity());
            address1.setCountry(addressDto.getCountry());
            address1.setState(addressDto.getState());

            return address1;
    }

    public static AddressDto toAddressDto(Address addresses){

                    AddressDto address1 = new AddressDto();
                    address1.setZipcode(addresses.getZipcode());
                    address1.setStreet(addresses.getStreet());
                    address1.setCity(addresses.getCity());
                    address1.setCountry(addresses.getCountry());
                    address1.setState(addresses.getState());
                    return address1;
    }

    public static UserResponse mapToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getUserRole());

        return userResponse;
    }


    public static Product mapToProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setId(productRequest.getId());
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(productRequest.getCategory());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setImageUrl(productRequest.getImageUrl());
        return product;
    }


}
