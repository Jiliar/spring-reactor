package com.jsolution.config;

import com.jsolution.handler.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routesCustomer(CustomerHandler handler){
        return route(GET("/v2/customers"), handler::findAll)
                .andRoute(GET("/v2/customers/{id}"), handler::findById)
                .andRoute(POST("/v2/customers"), handler::create)
                .andRoute(PUT("/v2/customers/{id}"), handler::update)
                .andRoute(DELETE("/v2/customers/{id}"), handler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> routesDish(DishHandler handler){
        return route(GET("/v2/dishes"), handler::findAll)
                .andRoute(GET("/v2/dishes/{id}"), handler::findById)
                .andRoute(POST("/v2/dishes"), handler::create)
                .andRoute(PUT("/v2/dishes/{id}"), handler::update)
                .andRoute(DELETE("/v2/dishes/{id}"), handler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> routesInvoice(InvoiceHandler handler){
        return route(GET("/v2/invoices"), handler::findAll)
                .andRoute(GET("/v2/invoices/{id}"), handler::findById)
                .andRoute(POST("/v2/invoices"), handler::create)
                .andRoute(PUT("/v2/invoices/{id}"), handler::update)
                .andRoute(DELETE("/v2/invoices/{id}"), handler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> routesUser(UserHandler handler){
        return route(GET("/v2/users"), handler::findAll)
                .andRoute(GET("/v2/users/{id}"), handler::findById)
                .andRoute(POST("/v2/users"), handler::create)
                .andRoute(PUT("/v2/users/{id}"), handler::update)
                .andRoute(DELETE("/v2/users/{id}"), handler::delete);
    }


    @Bean
    public RouterFunction<ServerResponse> routeMenu(MenuHandler handler){
        return route(GET("/v2/menus"), handler::findAll)
                .andRoute(GET("/v2/menus/{id}"), handler::findById)
                .andRoute(POST("/v2/menus"), handler::create)
                .andRoute(PUT("/v2/menus/{id}"), handler::update)
                .andRoute(DELETE("/v2/menus/{id}"), handler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> routeRole(RoleHandler handler){
        return route(GET("/v2/roles"), handler::findAll)
                .andRoute(GET("/v2/roles/{id}"), handler::findById)
                .andRoute(POST("/v2/roles"), handler::create)
                .andRoute(PUT("/v2/roles/{id}"), handler::update)
                .andRoute(DELETE("/v2/roles/{id}"), handler::delete);
    }
}
