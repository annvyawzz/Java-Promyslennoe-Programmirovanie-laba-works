package com.psyche.xml;

public class Book {
    private int id;
    private String title;
    private String author;
    private int year;
    private double price;
    private String category;
    private int copies;
    private int available;

    // Конструктор по умолчанию
    public Book() {
    }

    // Конструктор со всеми параметрами
    public Book(int id, String title, String author, int year, double price,
                String category, int copies, int available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.price = price;
        this.category = category;
        this.copies = copies;
        this.available = available;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public int getCopies() {
        return copies;
    }

    public int getAvailable() {
        return available;
    }

    // Сеттеры
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", copies=" + copies +
                ", available=" + available +
                '}';
    }
}