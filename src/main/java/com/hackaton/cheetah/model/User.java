package com.hackaton.cheetah.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String userId;
    private String name;
    private boolean admin;
    private String country;
    private String recordUrl;
}
