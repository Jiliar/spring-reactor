package com.jsolution.service;
import com.jsolution.model.User;
import com.jsolution.security.AuthUser;
import reactor.core.publisher.Mono;


public interface IUserService extends ICRUD<User, String>{

    Mono<User> saveHash(User user);
    Mono<AuthUser> searchByUserName(String user);
    Mono<User> searchById(String IS);

}
