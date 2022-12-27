package com.zhmenko.entity;

import com.zhmenko.util.TestBuilder;
import com.zhmenko.web.nac.model.nacrole.request.insert.NacRoleInsertDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aNacRoleInsertDto")
@With
public class NacRoleInsertDtoTestBuilder implements TestBuilder<NacRoleInsertDto> {
    private String name = "role name";
    private List<Integer> networkResources = new ArrayList<>();

    @Override
    public NacRoleInsertDto build() {
        final var server = new NacRoleInsertDto();
        server.setName(name);
        server.setNetworkResources(networkResources);
        return server;
    }
}
