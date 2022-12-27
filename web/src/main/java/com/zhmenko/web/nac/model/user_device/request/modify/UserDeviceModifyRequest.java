package com.zhmenko.web.nac.model.user_device.request.modify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeviceModifyRequest {
    private List<UserDeviceModifyDto> userDeviceModifyDtos;
}
