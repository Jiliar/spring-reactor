package com.jsolution.security;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//Clase S8
public class ErrorLogin {

    private String message;
    private Date timestamp;
}
