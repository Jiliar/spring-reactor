package com.jsolution.handler;

import com.jsolution.model.Dish;
import com.jsolution.service.IDishService;
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
public class DishHandler {

    @Autowired
    private IDishService DishService;

    @Autowired
    RequestValidator requestValidator;

    public Mono<ServerResponse> create(ServerRequest req) {

        Mono<Dish> monoDish = req.bodyToMono(Dish.class);
        return monoDish
                .flatMap(requestValidator::validate)
                .flatMap(DishService::save)
                .flatMap(c -> ServerResponse.created(URI.create(req.uri().toString().concat("/").concat(c.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c))
                );
    }

    public Mono<ServerResponse> findAll(ServerRequest req){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(DishService.findAll(), Dish.class);

    }

    public Mono<ServerResponse> findById(ServerRequest req){
        String id = req.pathVariable("id");
        return DishService.findById(id)
                .flatMap(c -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest req) {
        String id = req.pathVariable("id");

        Mono<Dish> monoClient = req.bodyToMono(Dish.class);
        Mono<Dish> monoDB = DishService.findById(id);

        return monoDB
                .flatMap(requestValidator::validate)
                .zipWith(monoClient, (db, cl) -> {
                    db.setId(id);
                    db.setName(cl.getName());
                    db.setPrice(cl.getPrice());
                    db.setStatus(cl.getStatus());
                    return db;
                })
                .flatMap(DishService::update)
                .flatMap(c -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }


    public Mono<ServerResponse> delete(ServerRequest req){
        String id = req.pathVariable("id");
        return DishService
                .findById(id)
                .flatMap(requestValidator::validate)
                .flatMap(c -> DishService.delete(c.getId())
                        .then(ServerResponse.noContent().build())
                ).switchIfEmpty(ServerResponse.notFound().build());

    }
}
