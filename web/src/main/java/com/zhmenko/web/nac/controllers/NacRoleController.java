package com.zhmenko.web.nac.controllers;

import com.zhmenko.web.nac.exceptions.not_found.RoleNotFoundException;
import com.zhmenko.web.nac.model.nacrole.request.insert.NacRolesInsertRequest;
import com.zhmenko.web.nac.model.nacrole.request.modify.NacRolesModifyRequest;
import com.zhmenko.web.nac.model.nacrole.response.NacRoleDto;
import com.zhmenko.web.nac.model.nacrole.response.NacRolesResponse;
import com.zhmenko.web.nac.services.NacRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/nac-role")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NacRoleController {
    private final NacRoleService nacRoleService;

    @DeleteMapping("/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteRole(@PathVariable("roleName") String roleName) {
        log.info("Delete role with name:" + roleName);
        boolean result = nacRoleService.deleteRoleByName(roleName);
        if (!result) throw new RoleNotFoundException(roleName);
        return new ResponseEntity<>("Успешно удалена роль с именем " + roleName, HttpStatus.OK);
    }

    @PutMapping(
            consumes = {"application/json"}
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateRoles(@RequestBody @Valid NacRolesModifyRequest nacRolesInsertRequest) {
        log.info("update roles: " + nacRolesInsertRequest);
        nacRoleService.updateRoles(nacRolesInsertRequest.getRoles());
        return new ResponseEntity<>("Обновление ролей прошло успешно!", HttpStatus.OK);
    }

    @PostMapping(
            consumes = {"application/json"}
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> postRoles(@RequestBody @Valid NacRolesInsertRequest nacRolesInsertRequest) {
        log.info("Add roles: " + nacRolesInsertRequest);
        nacRoleService.addRoles(nacRolesInsertRequest.getRoles());
        return new ResponseEntity<>("Вставка ролей прошла успешно!", HttpStatus.CREATED);
    }

    @GetMapping(
            path = "/{roleName}",
            produces = {"application/json"}
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NacRoleDto> getRoleByName(@PathVariable("roleName") String roleName) {
        log.info("Get role by name: " + roleName);
        NacRoleDto role = nacRoleService.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName));
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @GetMapping(produces = {"application/json"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NacRolesResponse> getAllRoles() {
        log.info("Get all roles request");
        return ResponseEntity.ok(new NacRolesResponse(nacRoleService.findAll()));
    }
}
