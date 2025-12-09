package com.example.springbootxml.controller;

import com.example.springbootxml.model.Book;
import com.example.springbootxml.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/librarian")
// Убрали @PreAuthorize временно
public class LibrarianController {

    private final BookService bookService;

    public LibrarianController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalBooks", bookService.getAllBooks().size());
        return "librarian/dashboard";
    }

    @GetMapping("/books")
    public String getAllBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "librarian/books";
    }

    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "librarian/add-book";
    }

    @PostMapping("/books/add")
    public String addBook(@ModelAttribute Book book) {
        bookService.addBook(book);
        return "redirect:/librarian/books";
    }

    @GetMapping("/books/{id}/update-price")
    public String showUpdatePriceForm(@PathVariable Integer id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "librarian/update-price";
    }

    @PostMapping("/books/{id}/update-price")
    public String updatePrice(@PathVariable Integer id, @RequestParam Double price) {
        bookService.updatePrice(id, price);
        return "redirect:/librarian/books";
    }

    @PostMapping("/books/{id}/borrow")
    public String borrowBook(@PathVariable Integer id) {
        bookService.borrowBook(id);
        return "redirect:/librarian/books";
    }
}