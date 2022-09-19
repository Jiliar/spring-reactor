package com.jsolution.controller;

import com.jsolution.model.User;
import com.jsolution.security.AuthRequest;
import com.jsolution.security.AuthResponse;
import com.jsolution.security.ErrorLogin;
import com.jsolution.security.JWTUtil;
import com.jsolution.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private IUserService service;

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody AuthRequest ar){
        return service.searchByUserName(ar.getUsername())
                .map((userDetails) -> {
                    if(BCrypt.checkpw(ar.getPassword(), userDetails.getPassword())) {
                        String token = jwtUtil.generateToken(userDetails);
                        Date expiration = jwtUtil.getExpirationDateFromToken(token);

                        return ResponseEntity.ok(new AuthResponse(token, expiration));
                    }else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorLogin("credenciales incorrectas", new Date()));
                    }
                }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<User>> save(@RequestBody User User, final ServerHttpRequest request){
        return service.saveHash(User)
                .map(e->ResponseEntity
                        .created(URI.create(request.getURI()
                                .toString()
                                .concat("/")
                                .concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e));
    }


    @PatchMapping("/restorepassword/{id}")
    public Mono<ResponseEntity<User>> update(@PathVariable("id") String id,
                                             @RequestBody User user){

        return service.searchById(id)
                .flatMap(u-> {
                    u.setPassword(user.getPassword());
                    return service.saveHash(u);
                })
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
