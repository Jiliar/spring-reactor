package com.jsolution.service;

import com.jsolution.model.Menu;
import reactor.core.publisher.Flux;

public interface IMenuService extends ICRUD<Menu, String> {
    Flux<Menu> getMenus(String[] roles);
}
