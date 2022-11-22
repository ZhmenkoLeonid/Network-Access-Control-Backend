package com.zhmenko.web.controllers.util;

import lombok.experimental.UtilityClass;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class StringUtils {
    public static String mapStringBody(MvcResult result) throws UnsupportedEncodingException {
        return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
}
