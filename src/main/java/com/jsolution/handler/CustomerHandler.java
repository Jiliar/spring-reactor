package com.jsolution.handler;

import com.jsolution.model.Customer;
import com.jsolution.service.ICustomerService;
import com.jsolution.validator.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class CustomerHandler {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    RequestValidator requestValidator;

    public Mono<ServerResponse> create(ServerRequest req) {

        Mono<Customer> monoCustomer = req.bodyToMono(Customer.class);
        return monoCustomer
                .flatMap(requestValidator::validate)
                .flatMap(customerService::save)
                .flatMap(c -> ServerResponse.created(URI.create(req.uri().toString().concat("/").concat(c.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c))
                );
    }

    public Mono<ServerResponse> findAll(ServerRequest req){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(customerService.findAll(), Customer.class);

    }

    public Mono<ServerResponse> findById(ServerRequest req){
        String id = req.pathVariable("id");
        return customerService.findById(id)
                .flatMap(c -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest req) {
        String id = req.pathVariable("id");

        Mono<Customer> monoClient = req.bodyToMono(Customer.class);
        Mono<Customer> monoDB = customerService.findById(id);

        return monoDB
                .flatMap(requestValidator::validate)
                .zipWith(monoClient, (db, cl) -> {
                    db.setId(id);
                    db.setNames(cl.getNames());
                    db.setLastnames(cl.getLastnames());
                    db.setBirthday(cl.getBirthday());
                    db.setUrl_picture(cl.getUrl_picture());
                    return db;
                })
                .flatMap(customerService::update)
                .flatMap(c -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }


    public Mono<ServerResponse> delete(ServerRequest req){
        String id = req.pathVariable("id");
        return customerService
                .findById(id)
                .flatMap(requestValidator::validate)
                .flatMap(c -> customerService.delete(c.getId())
                                             .then(ServerResponse.noContent().build())
                ).switchIfEmpty(ServerResponse.notFound().build());

    }

}
