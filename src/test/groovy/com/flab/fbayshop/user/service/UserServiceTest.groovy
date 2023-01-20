package com.flab.fbayshop.user.service

import com.flab.fbayshop.user.dto.request.UserModifyRequest
import com.flab.fbayshop.user.dto.request.UserSignupRequest
import com.flab.fbayshop.user.exception.AddressNotFoundException
import com.flab.fbayshop.user.exception.UserDuplicatedException
import com.flab.fbayshop.user.exception.UserNotFoundException
import com.flab.fbayshop.user.mapper.UserMapper
import com.flab.fbayshop.user.model.Address
import com.flab.fbayshop.user.model.User
import org.spockframework.spring.SpringBean
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UserServiceTest extends Specification {

    @SpringBean
    private UserMapper userMapper = Mock()

    @SpringBean
    private PasswordEncoder passwordEncoder = Mock()

    private UserService userService = new UserService(userMapper, passwordEncoder)

    def "회원가입 - 성공"() {

        given:
        UserSignupRequest request = new UserSignupRequest("test@t.c", "1234", "테스트1", "테스트1")
        String encodedPassword = "QWENIQWOENOQIWNE"
        userMapper.isExistsEmail(request.getEmail()) >> false
        passwordEncoder.encode(request.getPassword()) >> encodedPassword
        userMapper.insertUser(_) >> 1

        when:
        User user = userService.signupUser(request)

        then:
        println user
        user != null
        user.getName() == request.getName()
        user.getPassword() == encodedPassword
        user.getEmail() == request.getEmail()
        user.getNickname() == request.getNickname()

    }

    def "회원가입 - 실패 (이메일 중복)"() {

        given:
        UserSignupRequest request = new UserSignupRequest("test@t.c", "1234", "테스트1", "테스트1")
        userMapper.isExistsEmail(request.getEmail()) >> true

        when:
        userService.signupUser(request)

        then:
        thrown(UserDuplicatedException)

    }

    def "사용자 프로필 조회 - 성공"() {
        given:
        String email = "test@t.c"
        userMapper.findById(_) >> Optional.of(User.builder().email(email).build())

        when:
        User user = userService.getUserById(1L)

        then:
        user.getEmail() == email
    }

    def "사용자 프로필 조회 - 실패(사용자 없음)"() {
        given:
        userMapper.findById(_) >> Optional.empty()

        when:
        userService.getUserById(1L)

        then:
        thrown(UserNotFoundException)
    }

    def "사용자 프로필 수정 - 성공"() {
        given:
        Long userId = 1L
        User savedUser = User.builder().userId(userId).email("test@c.m").nickname("1234").build()
        UserModifyRequest request = new UserModifyRequest("afterName", "afterNickname")
        userMapper.findById(_) >> Optional.of(savedUser)
        userMapper.updateUser(_) >> 1

        when:
        User modifyUser = userService.modifyUser(userId, request)

        then:
        modifyUser.getUserId() == userId
        modifyUser.getName() == request.getName()
        modifyUser.getNickname() == request.getNickname()
        modifyUser.getPassword() == savedUser.getPassword()

    }

    def "사용자 프로필 수정 - 실패(사용자 없음)"() {
        given:
        userMapper.findById(_) >> Optional.empty()

        when:
        userService.modifyUser(1L, new UserModifyRequest("afterName", "afterNickname"))

        then:
        thrown(UserNotFoundException)
    }

    def "사용자 주소 리스트 조회 - 성공"() {
        given:
        Long userId = 1L
        List<Address> savedAddressList = List.of(
                Address.builder()
                        .userId(userId)
                        .addressId(1L)
                        .roadAddress("도로명 주소1")
                        .jibunAddress("지번 주소1")
                        .addressDetail("주소 상세1")
                        .receiverName("주문자명1")
                        .receiverContact("010-0000-0000")
                        .zoneCode(1001)
                        .build(),
                Address.builder()
                        .userId(userId)
                        .addressId(2L)
                        .roadAddress("도로명 주소2")
                        .jibunAddress("지번 주소2")
                        .addressDetail("주소 상세2")
                        .receiverName("주문자명2")
                        .receiverContact("010-0000-0000")
                        .zoneCode(1002)
                        .build())

        when:
        userMapper.selectAddressList(userId) >> savedAddressList
        List<Address> addressList = userService.selectAddressList(userId)

        then:
        addressList == savedAddressList
    }

    def "사용자 주소 등록 - 성공"() {
        given:
        int addressId = 1L
        Address saveAddress = Address.builder()
                .userId(1L)
                .addressId(addressId)
                .roadAddress("도로명 주소")
                .jibunAddress("지번 주소")
                .addressDetail("주소 상세")
                .receiverName("주문자명")
                .receiverContact("010-0000-0000")
                .zoneCode(1000)
                .build()
        when:
        userMapper.findById(_) >> Optional.of(User.builder().build())
        userMapper.insertAddress(saveAddress) >> 1
        Address address = userService.registAddress(saveAddress)

        then:
        address.getAddressId() == addressId
        address.getUserId() == saveAddress.getUserId()
        address.getRoadAddress() == saveAddress.getRoadAddress()
        address.getJibunAddress() == saveAddress.getJibunAddress()
        address.getAddressDetail() == saveAddress.getAddressDetail()
        address.getZoneCode() == saveAddress.getZoneCode()
        address.getReceiverName() == saveAddress.getReceiverName()
        address.getReceiverContact() == saveAddress.getReceiverContact()
    }

    def "사용자 주소 등록 - 실패(사용자 정보없음)"() {
        given:
        userMapper.findById(_) >> Optional.empty()

        when:
        userService.registAddress(Address.builder().build())

        then:
        thrown(UserNotFoundException)
    }

    def "사용자 주소 조회 - 성공"() {
        given:
        int addressId = 1L
        Address storedAddress = Address.builder()
                .userId(1L)
                .addressId(addressId)
                .roadAddress("도로명 주소")
                .jibunAddress("지번 주소")
                .addressDetail("주소 상세")
                .receiverName("주문자명")
                .receiverContact("010-0000-0000")
                .zoneCode(1000)
                .build()
        userMapper.findAddressById(addressId) >> Optional.of(storedAddress)

        when:
        Address address = userService.findAddressById(addressId)

        then:
        address == storedAddress
    }

    def "사용자 주소 조회 - 실패(주소 정보없음)"() {
        given:
        userMapper.findAddressById(_) >> Optional.empty()

        when:
        userService.findAddressById(1L)

        then:
        thrown(AddressNotFoundException)
    }

}
