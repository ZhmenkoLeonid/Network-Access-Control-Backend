package com.zhmenko.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zhmenko.ids.data.security.entity.SecurityRoleEntity;
import com.zhmenko.router.SSH;
import com.zhmenko.entity.SecurityRoleEntityTestBuilder;
import com.zhmenko.entity.SecurityUserEntityTestBuilder;
import com.zhmenko.util.TestDBFacade;
import com.zhmenko.web.controllers.util.JwtTokenHeaderMockMvcBuilderCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(classes = {TestSpringApplication.class, JwtTokenHeaderMockMvcBuilderCustomizer.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestDBFacade.Config.class)
public class AbstractTest {
    @LocalServerPort
    private int port;
    protected final String baseApiUrl = "http://localhost:" + port;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected TestDBFacade db;
    @Autowired
    private PasswordEncoder passwordEncoder;

    protected static ObjectMapper objectMapper;

    private static final UUID uuid = UUID.randomUUID();

    @MockBean
    private SSH ssh;

    @BeforeEach
    public void adminSecurityUser() {
        SecurityRoleEntity roleAdmin = db.save(SecurityRoleEntityTestBuilder.aSecurityRoleEntity().withName("ROLE_ADMIN"));
        db.save(
                SecurityUserEntityTestBuilder.aSecurityUserEntity()
                        .withId(uuid)
                        .withUsername("admin")
                        .withPassword(passwordEncoder.encode("changeit"))
                        .withSecurityRoles(Set.of(roleAdmin))
        );
    }

    @BeforeEach
    public void sshMockRequest() {
        Mockito
                .when(ssh.connectAndExecuteCommand(anyString()))
                .thenAnswer((Answer<String>) invocationOnMock -> {
                    String argument = invocationOnMock.getArgument(0, String.class);
                    return argument.contains("no") ? "Rule deleted" : "Rule accepted";
                });
    }

    @BeforeEach
    public void databaseClear() {
        db.cleanDatabase();
/*        nacRoleRepository.deleteAll();
        networkResourcesRepository.deleteAll();
        securityUserRepository.deleteAll();
        securityRoleRepository.deleteAll();*/
    }

    @BeforeAll
    public static void init() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
