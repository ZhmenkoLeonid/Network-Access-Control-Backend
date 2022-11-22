package com.zhmenko.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zhmenko.data.nac.repository.NacRoleRepository;
import com.zhmenko.data.nac.repository.NetworkResourcesRepository;
import com.zhmenko.data.security.models.SecurityRoleEntity;
import com.zhmenko.data.security.models.SecurityUserEntity;
import com.zhmenko.data.security.repository.SecurityRoleRepository;
import com.zhmenko.data.security.repository.SecurityUserRepository;
import com.zhmenko.web.controllers.util.JwtTokenHeaderMockMvcBuilderCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

@SpringBootTest(classes = {TestSpringApplication.class, JwtTokenHeaderMockMvcBuilderCustomizer.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AbstractTest {
    @LocalServerPort
    private int port;
    protected final String baseApiUrl = "http://localhost:" + port;
    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private SecurityUserRepository securityUserRepository;

    @Autowired
    private NacRoleRepository nacRoleRepository;

    @Autowired
    private NetworkResourcesRepository networkResourcesRepository;

    @MockBean
    private SecurityRoleRepository mockSecurityRoleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    protected static ObjectMapper objectMapper;

    @BeforeEach
    public void mockAdminSecurityUser() {
        SecurityRoleEntity roleAdmin = SecurityRoleEntity.builder().name("ROLE_ADMIN").build();
        Mockito.when(mockSecurityRoleRepository.findByName("ROLE_ADMIN"))
                .thenReturn(Optional.of(roleAdmin));

        SecurityUserEntity adminUser = SecurityUserEntity.builder()
                .securityRoles(Set.of(roleAdmin))
                .username("admin")
                .password(passwordEncoder.encode("changeit"))
                .nacUserEntities(Collections.emptySet())
                .build();

        Mockito.when(securityUserRepository.findByUsername("admin"))
                .thenReturn(Optional.of(adminUser));
    }

    @AfterEach
    public void databaseClear() {
        nacRoleRepository.deleteAll();
        networkResourcesRepository.deleteAll();
    }

    @BeforeAll
    public static void init() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
