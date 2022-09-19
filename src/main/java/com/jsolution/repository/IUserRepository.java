package com.jsolution.repository;

import com.jsolution.model.User;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IUserRepository extends IGenericRepository<User, String>{

    Mono<User> findOneByUsername(String username);
    Mono<User> findOneById(String id);

}
