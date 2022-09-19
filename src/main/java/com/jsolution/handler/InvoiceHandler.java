package com.jsolution.handler;

import com.jsolution.model.Invoice;
import com.jsolution.service.IInvoiceService;
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
public class InvoiceHandler {
    
    @Autowired
    private IInvoiceService InvoiceService;

    @Autowired
    RequestValidator requestValidator;

    public Mono<ServerResponse> create(ServerRequest req) {

        Mono<Invoice> monoInvoice = req.bodyToMono(Invoice.class);
        return monoInvoice
                .flatMap(requestValidator::validate)
                .flatMap(InvoiceService::save)
                .flatMap(c -> ServerResponse.created(URI.create(req.uri().toString().concat("/").concat(c.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c))
                );
    }

    public Mono<ServerResponse> findAll(ServerRequest req){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(InvoiceService.findAll(), Invoice.class);

    }

    public Mono<ServerResponse> findById(ServerRequest req){
        String id = req.pathVariable("id");
        return InvoiceService.findById(id)
                .flatMap(c -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest req) {
        String id = req.pathVariable("id");

        Mono<Invoice> monoClient = req.bodyToMono(Invoice.class);
        Mono<Invoice> monoDB = InvoiceService.findById(id);

        return monoDB
                .flatMap(requestValidator::validate)
                .zipWith(monoClient, (db, inv) -> {
                    db.setId(id);
                    db.setCustomer(inv.getCustomer());
                    db.setUser_create(inv.getUser_create());
                    db.setDescription(inv.getDescription());
                    db.setObservation(inv.getObservation());
                    db.setDate_create(inv.getDate_create());
                    db.setInvoice_items(inv.getInvoice_items());
                    return db;
                })
                .flatMap(InvoiceService::update)
                .flatMap(c -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }


    public Mono<ServerResponse> delete(ServerRequest req){
        String id = req.pathVariable("id");
        return InvoiceService
                .findById(id)
                .flatMap(requestValidator::validate)
                .flatMap(c -> InvoiceService.delete(c.getId())
                        .then(ServerResponse.noContent().build())
                ).switchIfEmpty(ServerResponse.notFound().build());

    }
}
