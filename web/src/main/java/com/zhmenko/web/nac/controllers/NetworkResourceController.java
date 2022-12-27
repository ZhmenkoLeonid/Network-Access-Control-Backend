package com.zhmenko.web.nac.controllers;

import com.zhmenko.web.nac.exceptions.not_found.NetworkResourceNotFoundException;
import com.zhmenko.web.nac.model.NetworkResourceDto;
import com.zhmenko.web.nac.model.networkresource.request.NetworkResourceRequest;
import com.zhmenko.web.nac.model.networkresource.response.NetworkResourceResponse;
import com.zhmenko.web.nac.services.NetworkResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/network-resource")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NetworkResourceController {
    private final NetworkResourceService networkResourceService;

    @DeleteMapping("/{port}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteNetworkResource(@PathVariable("port") @Min(0) @Max(65535) int port) {
        log.info("Delete resource with port: " + port);
        boolean result = networkResourceService.deleteNetworkResourceByPort(port);
        if (!result) throw new NetworkResourceNotFoundException(port);
        return new ResponseEntity<>("Успешно удалён ресурс с именем " + port, HttpStatus.OK);
    }

    @PutMapping(
            consumes = {"application/json"}
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateNetworkResource(@RequestBody @Valid NetworkResourceDto networkResourceDto) {
        log.info("update resource: " + networkResourceDto);
        networkResourceService.updateNetworkResource(networkResourceDto);
        return new ResponseEntity<>("Обновление ресурсов прошло успешно!", HttpStatus.OK);
    }

    @PostMapping(
            consumes = {"application/json"}
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> postNetworkResources(@RequestBody @Valid NetworkResourceRequest networkResourceRequest) {
        log.info("Add resources: " + networkResourceRequest);
        networkResourceService.addNetworkResources(networkResourceRequest.getResources());
        return new ResponseEntity<>("Вставка ресурсов прошла успешно!", HttpStatus.CREATED);
    }

    @GetMapping(
            path = "/{port}",
            produces = {"application/json"}
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NetworkResourceDto> getNetworkResourceByPort(@PathVariable("port") int port) {
        log.info("Get resource by port: " + port);
        NetworkResourceDto networkResourceDto = networkResourceService.findByPort(port)
                .orElseThrow(() -> new NetworkResourceNotFoundException(port));
        return new ResponseEntity<>(networkResourceDto, HttpStatus.OK);
    }

    @GetMapping(produces = {"application/json"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NetworkResourceResponse> getAllNetworkResources() {
        log.info("Get all network resources request");
        return ResponseEntity.ok(
                new NetworkResourceResponse(networkResourceService.findAll())
        );
    }
}
