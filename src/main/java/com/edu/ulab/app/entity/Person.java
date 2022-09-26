package com.edu.ulab.app.entity;



import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


@Entity
@Data
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Full name is mandatory")
    private String fullName;
    @NotBlank(message = "Title is mandatory")
    private String title;
    @Min(value = 0, message = "Age must be more or equal 0")
    private int age;
}
