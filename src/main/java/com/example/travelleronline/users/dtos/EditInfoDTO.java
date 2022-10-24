package com.example.travelleronline.users.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EditInfoDTO {

    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate dateOfBirth;
    private char gender;

}