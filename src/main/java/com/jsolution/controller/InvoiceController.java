package com.jsolution.controller;

import com.jsolution.model.Invoice;
import com.jsolution.service.IInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;
import static reactor.function.TupleUtils.function;


@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private IInvoiceService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Invoice>>>findAll() {
        Flux<Invoice> fx = service.findAll();
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Invoice>> findById(@PathVariable("id") String id){
        return service.findById(id)
                .map(e->ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Invoice>> save(@RequestBody Invoice Invoice, final ServerHttpRequest request){
        return service.save(Invoice)
                .map(e->ResponseEntity
                        .created(URI.create(request.getURI()
                                .toString()
                                .concat("/")
                                .concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Invoice>> update(@PathVariable("id") String id, @RequestBody Invoice Invoice){

        Mono<Invoice> monoBody = Mono.just(Invoice);
        Mono<Invoice> monoDB = service.findById(id);

        return monoDB.zipWith(monoBody, (bd, d)->{
                    bd.setId(id);
                    bd.setCustomer(d.getCustomer());
                    bd.setDescription(d.getDescription());
                    bd.setObservation(d.getObservation());
                    bd.setUser_create(d.getUser_create());
                    bd.setDate_create(d.getDate_create());
                    bd.setInvoice_items(d.getInvoice_items());
                    return bd;
                })
                .flatMap(e->service.update(e)) //service::update
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id){
        return service.findById(id)
                .flatMap(e->service.delete(e.getId())
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                        //.thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    //private Invoice InvoiceHateoas;

    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel<Invoice>> getHateoasById(@PathVariable("id") String id){
        Mono<Link> link1 = linkTo(methodOn(InvoiceController.class).findById(id)).withSelfRel().toMono();
        //Practica No recomendada: Debe usar variable global y esta puede ser modificada desde otro metodo.
        /*return service.findById(id)
                .flatMap(Invoice -> {
                    this.InvoiceHateoas = Invoice;
                    return link1;
                })
                .map(link -> EntityModel.of(InvoiceHateoas, link));*/

        //Practica Intermedia: Se podria incurrir con callbacks dentro de otros callbacks
        /*return service.findById(id)
                .flatMap(Invoice -> {
                    return link1.map(lk -> EntityModel.of(Invoice, lk));
                });*/

        // Practica Mejorada
        /*return service.findById(id)
                .zipWith(link1, (Invoice, lk)->EntityModel.of(Invoice, lk));*/

        //Practica simplificada

        /*return service.findById(id)
                .zipWith(link1, EntityModel::of);*/

        // Practica con reactor-extra: Uniendo dos links
        Mono<Link> link2 = linkTo(methodOn(InvoiceController.class).findAll()).withSelfRel().toMono();
        return link1
                .zipWith(link2)
                .map(function((lk1, lk2) -> Links.of(lk1, lk2)))
                .zipWith(service.findById(id), (lk3, Invoice) -> EntityModel.of(Invoice, lk3));

    }

    @GetMapping("generateReport/{id}")
    public Mono<ResponseEntity<byte[]>> generateReport(@PathVariable("id") String id){
        Mono<byte[]> monoReport = service.generateReport(id);
        return monoReport.map(bytes -> ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes)
        ).defaultIfEmpty(ResponseEntity.noContent().build());
    }


}
