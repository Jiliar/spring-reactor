package com.jsolution.controller;

import com.jsolution.model.Customer;
import com.jsolution.model.User;
import com.jsolution.pagination.PageSupport;
import com.jsolution.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;
import static reactor.function.TupleUtils.function;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<User>>>findAll() {
        Flux<User> fx = service.findAll();
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> findById(@PathVariable("id") String id){
        return service.findById(id)
                .map(e->ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> update(@PathVariable("id") String id, @RequestBody User User){

        Mono<User> monoBody = Mono.just(User);
        Mono<User> monoDB = service.findById(id);

        return monoDB.zipWith(monoBody, (bd, d)->{
                    bd.setId(id);
                    bd.setUsername(d.getUsername());
                    bd.setPassword(d.getPassword());
                    bd.setEmail(d.getEmail());
                    bd.setRoles(d.getRoles());
                    bd.setStatus(d.getStatus());
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

    //private User UserHateoas;

    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel<User>> getHateoasById(@PathVariable("id") String id){
        Mono<Link> link1 = linkTo(methodOn(UserController.class).findById(id)).withSelfRel().toMono();
        Mono<Link> link2 = linkTo(methodOn(UserController.class).findAll()).withSelfRel().toMono();
        return link1
                .zipWith(link2)
                .map(function((lk1, lk2) -> Links.of(lk1, lk2)))
                .zipWith(service.findById(id), (lk3, User) -> EntityModel.of(User, lk3));

    }

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<User>>> getPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size
    ){
        return service.getPage(PageRequest.of(page, size))
                .map(pag -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(pag))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


}
