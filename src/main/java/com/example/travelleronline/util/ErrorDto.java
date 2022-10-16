package com.example.travelleronline.util;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorDto {

    private int status;
    private LocalDateTime time;
    private String message;

}