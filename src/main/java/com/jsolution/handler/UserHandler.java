package com.jsolution.handler;

import com.jsolution.model.User;
import com.jsolution.service.IUserService;
import com.jsolution.validator.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class UserHandler {

    @Autowired
    private IUserService UserService;

    @Autowired
    RequestValidator requestValidator;

    public Mono<ServerResponse> create(ServerRequest req) {

        Mono<User> monoUser = req.bodyToMono(User.class);
        return monoUser
                .flatMap(requestValidator::validate)
                .flatMap(UserService::save)
                .flatMap(c -> ServerResponse.created(URI.create(req.uri().toString().concat("/").concat(c.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c))
                );
    }

    public Mono<ServerResponse> findAll(ServerRequest req){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(UserService.findAll(), User.class);

    }

    public Mono<ServerResponse> findById(ServerRequest req){
        String id = req.pathVariable("id");
        return UserService.findById(id)
                .flatMap(c -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest req) {
        String id = req.pathVariable("id");

        Mono<User> monoClient = req.bodyToMono(User.class);
        Mono<User> monoDB = UserService.findById(id);

        return monoDB
                .flatMap(requestValidator::validate)
                .zipWith(monoClient, (db, cl) -> {
                    db.setId(id);
                    db.setUsername(cl.getUsername());
                    db.setPassword(cl.getPassword());
                    db.setEmail(cl.getEmail());
                    db.setStatus(cl.getStatus());
                    db.setRoles(cl.getRoles());
                    return db;
                })
                .flatMap(UserService::update)
                .flatMap(c -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }


    public Mono<ServerResponse> delete(ServerRequest req){
        String id = req.pathVariable("id");
        return UserService
                .findById(id)
                .flatMap(requestValidator::validate)
                .flatMap(c -> UserService.delete(c.getId())
                        .then(ServerResponse.noContent().build())
                ).switchIfEmpty(ServerResponse.notFound().build());

    }
}
