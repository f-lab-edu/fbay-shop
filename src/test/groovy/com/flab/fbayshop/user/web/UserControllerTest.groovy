package com.flab.fbayshop.user.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.flab.fbayshop.common.dto.response.ApiResponse
import com.flab.fbayshop.common.dto.response.ErrorResponse
import com.flab.fbayshop.error.dto.ErrorType
import com.flab.fbayshop.user.controller.UserController
import com.flab.fbayshop.user.dto.request.UserModifyRequest
import com.flab.fbayshop.user.dto.request.UserSignupRequest
import com.flab.fbayshop.user.model.User
import com.flab.fbayshop.user.service.UserService
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import javax.servlet.Filter
import javax.servlet.http.HttpSession
import java.nio.charset.StandardCharsets

import static com.flab.fbayshop.error.dto.ErrorType.INVALID_PARAMETER
import static com.flab.fbayshop.error.dto.ErrorType.UNAUTHORIZED
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Slf4j
@Transactional
@SpringBootTest
class UserControllerTest extends Specification {

    @Autowired
    private UserController userController

    @Autowired
    private UserService userService

    private MockMvc mockMvc

    @Autowired
    private WebApplicationContext ctx

    @Autowired
    private ObjectMapper objectMapper

    @Autowired
    private Filter springSecurityFilterChain

    private User user

    def setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .defaultRequest(get("/").with(testSecurityContext()))
                .addFilters(springSecurityFilterChain)
                .build()

        UserSignupRequest request = new UserSignupRequest("test@t.c", "qwer1234!", "테스트", "테스트")
        userService.deleteByEmail(request.getEmail())
        this.user = userService.signupUser(request)
    }

    def "이메일 중복체크 - 성공"() {
        given:
        UserSignupRequest request = new UserSignupRequest("test1@t.c", "qwer1234!", "테스트", "테스트")
        userService.deleteByEmail(email)
        userService.deleteByEmail(request.getEmail())
        user = userService.signupUser(request)

        expect:
        String contentStr = mockMvc.perform(get("/api/v1/user/email-exists")
                .queryParam("email", email))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString()
        objectMapper.readValue(contentStr, ApiResponse.class).getData() == result
        println contentStr

        where:
        email       | result
        "test1@t.c" | true
        "test2@t.c" | false
    }

    def "이메일 중복체크 - 실패(유효성검증)"() {

        given:
        def param1 = get("/api/v1/user/email-exists").queryParam("email", "")

        when:
        def actions = mockMvc.perform(param1)

        then:
        actions.andExpect(status().is4xxClientError()).andDo(MockMvcResultHandlers.print())

    }

    def "회원가입 - 성공"() {

        given:
        String param1 = "{ \"email\": \"test22@t.c\"," +
                "\"password\": \"qwer1234!\"," +
                "\"name\": \"테스트2\"," +
                "\"nickname\": \"테스트2\"" +
                "}"

        when:
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(param1))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()

        then:
        String contentStr = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
        objectMapper.readValue(contentStr, ApiResponse.class).getCode() == HttpStatus.CREATED.value()

    }

    def "회원가입 - 실패(이메일 중복)"() {
        given:
        String param1 = "{ \"email\": \"" + user.getEmail() + "\"," +
                "\"password\": \"qwer1234!\"," +
                "\"name\": \"테스트2\"," +
                "\"nickname\": \"테스트2\"" +
                "}"

        when:
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(param1))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()

        then:
        String contentStr = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
        objectMapper.readValue(contentStr, ErrorResponse.class).getCode() == ErrorType.USER_DUPLICATED.getCode()
    }

    def "회원가입 - 실패(유효성 검증)"() {
        given:
        String param1 = "{ \"email\": \"tsetst\"," +
                "\"password\": \"q\"," +
                "\"name\": \"테스트2\"," +
                "\"nickname\": \"테스트2\"" +
                "}"

        when:
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(param1))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()

        then:
        String contentStr = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
        objectMapper.readValue(contentStr, ErrorResponse.class).getCode() == ErrorType.INVALID_PARAMETER.getCode()
    }

    def "이메일 로그인 - 성공"() {
        given:
        String requestStr = "{ \"email\": \"" + user.getEmail() + "\", \"password\": \"qwer1234!\"}"

        when:
        HttpSession session = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr))
                .andReturn().getRequest().getSession()
        then:
        session.getAttribute("SPRING_SECURITY_CONTEXT") != null

    }

    def "이메일 로그인 - 실패(비밀번호 오류)"() {
        given:
        String requestStr = "{ \"email\": \"" + user.getEmail() + "\", \"password\": \"qwer1234!123\"}"

        when:
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()

        then:
        String contentStr = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
        objectMapper.readValue(contentStr, ErrorResponse.class).getCode() == ErrorType.USER_LOGIN_FAIL.getCode()

    }

    @WithMockUser(username = "test@t.c", password = "1234")
    def "사용자 프로필 조회 - 성공"() {
        when:
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()

        then:
        String contentStr = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
        Map<String, Object> dataMap = objectMapper.readValue(contentStr, ApiResponse.class).getData()
        dataMap.get("email") == user.getEmail()
        dataMap.get("name") == user.getName()
    }

    def "사용자 프로필 조회 - 실패(사용자 권한)"() {
        when:
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user"))
                .andExpect(status().is4xxClientError()).andReturn()

        then:
        String contentStr = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
        objectMapper.readValue(contentStr, ErrorResponse.class).getCode() == UNAUTHORIZED.getCode()
    }

    @WithMockUser(username = "test@t.c", password = "qwer1234!")
    def "사용자 프로필 수정 - 성공"() {
        given:
        String requestStr = "{\"name\":\"이름변경\", \"nickname\":\"닉네임변경\"}"
        def request = objectMapper.readValue(requestStr, UserModifyRequest.class)

        when:
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user").content(requestStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn()

        then:
        String contentStr = result.getResponse().getContentAsString(StandardCharsets.UTF_8)
        Map<String, Object> resultMap = objectMapper.readValue(contentStr, ApiResponse.class).getData()
        resultMap.get("name") == request.getName()
        resultMap.get("nickname") == request.getNickname()
    }

    @WithMockUser(username = "test@t.c", password = "qwer1234!")
    def "사용자 프로필 수정 - 실패(유효성 검증)"() {

        given:
        String requestStr = "{\"name\":\"" + name + "\", \"nickname\":\"" + nickname + "\"}"

        when:
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user").content(requestStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn()

        then:
        String contentStr = result.getResponse().getContentAsString(StandardCharsets.UTF_8)
        objectMapper.readValue(contentStr, ErrorResponse.class).getCode() == INVALID_PARAMETER.getCode()

        where:
        name | nickname
        ""   | "닉네임"
        "이름" | ""

    }

}
