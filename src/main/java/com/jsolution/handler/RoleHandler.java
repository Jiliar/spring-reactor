package com.jsolution.handler;

import com.jsolution.model.Role;
import com.jsolution.service.IRoleService;
import com.jsolution.validator.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class RoleHandler {

    @Autowired
    private IRoleService RoleService;

    @Autowired
    RequestValidator requestValidator;

    public Mono<ServerResponse> create(ServerRequest req) {

        Mono<Role> monoRole = req.bodyToMono(Role.class);
        return monoRole
                .flatMap(requestValidator::validate)
                .flatMap(RoleService::save)
                .flatMap(c -> ServerResponse.created(URI.create(req.uri().toString().concat("/").concat(c.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c))
                );
    }

    public Mono<ServerResponse> findAll(ServerRequest req){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(RoleService.findAll(), Role.class);

    }

    public Mono<ServerResponse> findById(ServerRequest req){
        String id = req.pathVariable("id");
        return RoleService.findById(id)
                .flatMap(c -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest req) {
        String id = req.pathVariable("id");

        Mono<Role> monoClient = req.bodyToMono(Role.class);
        Mono<Role> monoDB = RoleService.findById(id);

        return monoDB
                .flatMap(requestValidator::validate)
                .zipWith(monoClient, (db, cl) -> {
                    db.setId(id);
                    db.setNombre(cl.getNombre());
                    return db;
                })
                .flatMap(RoleService::update)
                .flatMap(c -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }


    public Mono<ServerResponse> delete(ServerRequest req){
        String id = req.pathVariable("id");
        return RoleService
                .findById(id)
                .flatMap(requestValidator::validate)
                .flatMap(c -> RoleService.delete(c.getId())
                        .then(ServerResponse.noContent().build())
                ).switchIfEmpty(ServerResponse.notFound().build());

    }
}
