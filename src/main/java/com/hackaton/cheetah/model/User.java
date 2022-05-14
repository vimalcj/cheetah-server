package com.hackaton.cheetah.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long userId;
    private String name;
    private boolean admin;
    private String country;
    private String recordUrl;
    private String email;
    private String imageUrl;
}
