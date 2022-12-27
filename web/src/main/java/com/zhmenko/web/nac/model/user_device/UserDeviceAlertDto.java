package com.zhmenko.web.nac.model.user_device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDeviceAlertDto {
    private int messageId;

    private String alertMessage;
}
