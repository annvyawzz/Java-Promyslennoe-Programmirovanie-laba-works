package com.example.springbootxml.controller;

import com.example.springbootxml.model.Book;
import com.example.springbootxml.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reader")
// Убрали @PreAuthorize временно
public class ReaderController {

    private final BookService bookService;

    public ReaderController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalBooks", bookService.getAllBooks().size());
        return "reader/dashboard";
    }

    @GetMapping("/books")
    public String getAllBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "reader/books";
    }

    @GetMapping("/search")
    public String showSearchForm() {
        return "reader/search";
    }

    @PostMapping("/search")
    public String searchBooks(@RequestParam String type,
                              @RequestParam String query,
                              Model model) {
        List<Book> results = List.of();

        switch (type) {
            case "author" -> results = bookService.searchByAuthor(query);
            case "year" -> {
                try {
                    results = bookService.searchByYear(Integer.parseInt(query));
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
            case "category" -> results = bookService.searchByCategory(query);
            case "title" -> results = bookService.searchByTitle(query);
        }

        model.addAttribute("results", results);
        model.addAttribute("type", type);
        model.addAttribute("query", query);
        return "reader/search";
    }

    @GetMapping("/account")
    public String viewAccount() {
        return "reader/account";
    }
}