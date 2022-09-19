package com.jsolution.controller;

import com.jsolution.model.Dish;
import com.jsolution.repository.IDishRepository;
import com.jsolution.service.impl.DishServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;


@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = DishController.class)
@Import(DishServiceImpl.class)
public class DishControllerTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private IDishRepository repository;

    @MockBean
    private Resources resources;

    @Test
    void findAll(){

        Dish dish1 = new Dish();
        dish1.setId("1");
        dish1.setName("Soda");
        dish1.setPrice(29.9);
        dish1.setStatus(true);

        Dish dish2 = new Dish();
        dish2.setId("2");
        dish2.setName("Pizza");
        dish2.setPrice(49.9);
        dish2.setStatus(true);

        List<Dish> list = new ArrayList<>();
        list.add(dish1);
        list.add(dish2);

        Mockito.when(repository.findAll()).thenReturn(Flux.fromIterable(list));

        client.get()
                .uri("/dishes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Dish.class)
                .hasSize(2);
    }

    @Test
    void createTest(){
        Dish dish1 = new Dish();
        dish1.setId("1");
        dish1.setName("Soda");
        dish1.setPrice(29.9);
        dish1.setStatus(true);

        Mockito.when(repository.save(any())).thenReturn(Mono.just(dish1));

        client.post()
                .uri("/dishes")
                .body(Mono.just(dish1), Dish.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.price").isNumber()
                .jsonPath("$.status").isBoolean();

    }

    @Test
    void updateTest(){
        Dish dish1 = new Dish();
        dish1.setId("1");
        dish1.setName("Soda");
        dish1.setPrice(29.9);
        dish1.setStatus(true);

        Mockito.when(repository.findById("1")).thenReturn(Mono.just(dish1));
        Mockito.when(repository.save(any())).thenReturn(Mono.just(dish1));

        client.put()
                .uri("/dishes/"+dish1.getId())
                .body(Mono.just(dish1), Dish.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.price").isNumber()
                .jsonPath("$.status").isBoolean();

    }


    @Test
    void deleteTest(){

        Dish dish1 = new Dish();
        dish1.setId("1");
        dish1.setName("Soda");
        dish1.setPrice(29.9);
        dish1.setStatus(true);

        Mockito.when(repository.findById("1")).thenReturn(Mono.just(dish1));
        Mockito.when(repository.deleteById("1")).thenReturn(Mono.empty());

        client.delete()
                .uri("/dishes/"+dish1.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();

    }



}
