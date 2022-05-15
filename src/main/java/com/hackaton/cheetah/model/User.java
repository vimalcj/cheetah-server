package com.hackaton.cheetah.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class User {
    private String userId;
    private String name;
    private boolean admin;
    private String country;
    private String recordUrl;
    private String email;
    private String imageUrl;
    private Long empId;
    private Timestamp createdTs;
    private Timestamp modifiedTs;
}
