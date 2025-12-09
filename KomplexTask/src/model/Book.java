package com.example.springbootxml.model;

import lombok.Data;

@Data
public class Book {
    private Integer id;
    private String title;
    private String author;
    private Integer year;
    private Double price;
    private String category;
    private Integer copies;
    private Integer available;
}
