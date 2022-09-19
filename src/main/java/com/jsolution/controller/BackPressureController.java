package com.jsolution.controller;

import com.jsolution.model.Dish;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;


@RestController
@RequestMapping("/backpressures")
//Analisis de Contra Presión
public class BackPressureController {

    //Espera que todo termine para parsear el resultado como JSON, pero como nunca termina nunca muestra nada.
    //JSON.parse [{},{},{},{},{}]
    //El comportamiento varia en función al tipo de retorno
    //URL: http://localhost:8080/backpressures/json
    @GetMapping(value = "/json", produces = "application/json")
    public Flux<Dish> json() {
        return Flux.interval(Duration.ofMillis(100))
                .map(t -> new Dish("1", "Soda", 5.90, true));
    }

    //Este esta emitiendo objetos uno a uno
    //{}{}{}{}{}{}{}{}{}
    //El comportamiento varia en función al tipo de retorno
    //URL: http://localhost:8080/backpressures/streamjson
    @GetMapping(value = "/streamjson", produces = "application/stream+json")
    public Flux<Dish> streamjson() {
        return Flux.interval(Duration.ofMillis(100))
                .map(t -> new Dish("1", "Soda", 5.90, true));
    }

    //Espera que todo termine para parsear el resultado como JSON, pero como nunca termina nunca muestra nada.
    //JSON.parse [{},{},{},{},{}] : Los muestra porque son finitos
    //El comportamiento varia en función al tipo de retorno
    //URL: http://localhost:8080/backpressures/jsonFinito
    @GetMapping(value="/jsonFinito", produces="application/json")
    public Flux<Dish> jsonFinito(){
        return Flux.range(0, 5000)
                .map(t -> new Dish("1", "Soda", 5.90, true));
    }

    //Este esta emitiendo objetos uno a uno
    //{}{}{}{}{}{}{}{}{}
    //El comportamiento varia en función al tipo de retorno
    //URL: http://localhost:8080/backpressures/streamjsonFinito
    @GetMapping(value="/streamjsonFinito", produces="application/stream+json")
    public Flux<Dish> streamjsonFinito(){
        return Flux.range(0, 5000)
                .map(t -> new Dish("1", "Soda", 5.90, true));
    }

    @GetMapping(value="/buffer")
    public Flux<Integer> testLimitRate(){
        return Flux.range(1, 100)
                .log()
                .limitRate(10, 8)
                .delayElements(Duration.ofMillis(1));
    }
}
