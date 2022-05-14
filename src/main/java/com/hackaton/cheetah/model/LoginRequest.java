package com.hackaton.cheetah.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {
    @NotNull
    @NotBlank
    private Long username;
    @NotNull
    @NotBlank
    private String password;

}
