package com.zhmenko.web.controllers.util;

import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;

@Component
public class JwtTokenHeaderMockMvcBuilderCustomizer implements MockMvcBuilderCustomizer {

    @Override
    public void customize(ConfigurableMockMvcBuilder<?> builder) {
        String token = TokenAuthenticationService.createToken("admin");
        RequestBuilder apiKeyRequestBuilder = MockMvcRequestBuilders.post("http://localhost/nac-role")
                .header("Authorization", token);
        builder.defaultRequest(apiKeyRequestBuilder);
    }

}