package com.zhmenko.web.nac.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Error
 */
@AllArgsConstructor
@Data
public class Error {
  private Integer code;
  private String message;
}

