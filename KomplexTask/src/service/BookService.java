package com.example.springbootxml.service;

import com.example.springbootxml.model.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private final XMLService xmlService;
    private List<Book> books;

    public BookService(XMLService xmlService) {
        this.xmlService = xmlService;
        loadBooks();
    }

    private void loadBooks() {
        books = xmlService.loadBooks();
        if (books == null) {
            books = new ArrayList<>();
        }
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public Book getBookById(Integer id) {
        return books.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void addBook(Book book) {
        int maxId = books.stream()
                .mapToInt(Book::getId)
                .max()
                .orElse(0);
        book.setId(maxId + 1);

        if (book.getAvailable() == null) {
            book.setAvailable(book.getCopies());
        }

        books.add(book);
        saveBooks();
    }

    public void updatePrice(Integer id, Double newPrice) {
        Book book = getBookById(id);
        if (book != null) {
            book.setPrice(newPrice);
            saveBooks();
        }
    }

    public void borrowBook(Integer id) {
        Book book = getBookById(id);
        if (book != null && book.getAvailable() > 0) {
            book.setAvailable(book.getAvailable() - 1);
            saveBooks();
        }
    }

    public void returnBook(Integer id) {
        Book book = getBookById(id);
        if (book != null && book.getAvailable() < book.getCopies()) {
            book.setAvailable(book.getAvailable() + 1);
            saveBooks();
        }
    }

    // Используем XPath для поиска
    public List<Book> searchByAuthor(String author) {
        return xmlService.searchByAuthor(author);
    }

    public List<Book> searchByYear(Integer year) {
        return xmlService.searchByYear(year);
    }

    public List<Book> searchByCategory(String category) {
        return xmlService.searchByCategory(category);
    }

    public List<Book> searchByTitle(String title) {
        return xmlService.searchByTitle(title);
    }

    private void saveBooks() {
        xmlService.saveBooks(books);
    }
}
