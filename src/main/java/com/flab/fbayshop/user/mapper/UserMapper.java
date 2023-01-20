package com.flab.fbayshop.user.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.flab.fbayshop.user.model.Address;
import com.flab.fbayshop.user.model.User;

@Mapper
public interface UserMapper {

    int insertUser(User user);
    boolean isExistsEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);

    int updateUser(User user);

    void deleteByEmail(String email);

    Optional<Address> findAddressById(Long addressId);

    List<Address> selectAddressList(Long userId);

    int insertAddress(Address address);
}
