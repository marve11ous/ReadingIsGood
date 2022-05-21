package com.readingisgood.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AbstractIT {

    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:14.3");
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

//    @Autowired
//    private void setupMockMvc(WebApplicationContext context) {
//        mvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
//    }
    @Autowired
    ResourceLoader resourceLoader;

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("DB_URL", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("DB_USERNAME", POSTGRESQL_CONTAINER::getUsername);
        registry.add("DB_PASSWORD", POSTGRESQL_CONTAINER::getPassword);
        registry.add("DB_PLATFORM", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @SneakyThrows
    ResultActions performByAdmin(MockHttpServletRequestBuilder requestBuilder, Object request) {
        return perform(requestBuilder.with(user("admin").roles("USER", "ADMIN")), request);
    }

    @SneakyThrows
    ResultActions perform(MockHttpServletRequestBuilder requestBuilder, Object request) {
        if (request != null) {
            requestBuilder
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request));
        }
        return mvc.perform(requestBuilder.accept(MediaType.APPLICATION_JSON_VALUE));
    }

    @SneakyThrows
    void assertResponse(MockHttpServletRequestBuilder requestBuilder, String resourcePath) {
        assertResponse(requestBuilder, null, resourcePath);
    }

    @SneakyThrows
    void assertResponse(MockHttpServletRequestBuilder requestBuilder, Object request, String resourcePath) {
        JSONAssert.assertEquals(
                new String(Files.readAllBytes(ResourceUtils.getFile("classpath:" + resourcePath).toPath())),
                performByAdmin(requestBuilder, request)
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8),
                new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
                        new Customization("date", (a, b) -> {
                            if (a.equals("$NOW")) {
                                return assertDateTimeNow(b);
                            } else if (b.equals("$NOW")) {
                                return assertDateTimeNow(a);
                            } else {
                                return Objects.equals(a, b);
                            }
                        })
                ));
    }

    private boolean assertDateTimeNow(Object dateTime) {
        return ChronoUnit.SECONDS.between(LocalDateTime.parse(dateTime.toString()), LocalDateTime.now()) == 0;
    }


    @SneakyThrows
    void assertMethodArgumentNotValid(MockHttpServletRequestBuilder requestBuilder,
                                      Object request,
                                      String fieldName,
                                      Object fieldValue,
                                      String message) {
        EXPRESSION_PARSER.parseExpression(fieldName).setValue(request, fieldValue);
        assertErrorResponse(requestBuilder, request, HttpStatus.BAD_REQUEST, MethodArgumentNotValidException.class,
                String.format("field '%s': rejected value \\[%s].*message \\[%s]",
                        Pattern.quote(fieldName), Pattern.quote(Objects.toString(fieldValue)), Pattern.quote(message)));
    }

    @SneakyThrows
    void assertErrorResponse(MockHttpServletRequestBuilder requestBuilder,
                             Object request,
                             HttpStatus status,
                             Class<? extends Exception> exceptionType,
                             String message) {
        performByAdmin(requestBuilder, request)
                .andExpect(status().is(status.value()))
                .andExpect(result -> {
                    var ex = result.getResolvedException();
                    assertNotNull(ex);
                    assertTrue(exceptionType.isInstance(ex));
                    assertTrue(ex.getMessage().matches(".*" + message + ".*"), ex::getMessage);
                });
    }
}