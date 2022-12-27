package com.zhmenko.web.nac.model.user_device.response;

import com.zhmenko.web.nac.model.user_device.UserDeviceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeviceResponse {
    private List<UserDeviceDto> devices;
}
