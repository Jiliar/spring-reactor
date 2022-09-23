package com.jsolution.repository;

import com.jsolution.model.Menu;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface IMenuRepository extends IGenericRepository<Menu, String>{
    @Query("{'roles': {$in: ?0}}")
    Flux<Menu> getMenus(String[] roles);
}
