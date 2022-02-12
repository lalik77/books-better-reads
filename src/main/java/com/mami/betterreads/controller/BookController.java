package com.mami.betterreads.controller;

import com.mami.betterreads.author.AuthorRepository;
import com.mami.betterreads.book.Book;
import com.mami.betterreads.book.BookRepository;
import com.mami.betterreads.userbooks.UserBooks;
import com.mami.betterreads.userbooks.UserBooksPrimaryKey;
import com.mami.betterreads.userbooks.UserBooksRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BookController {

  private static final String COVER_IMAGE_ROOT = "https://covers.openlibrary.org/b/id/";

  private BookRepository bookRepository;
  private UserBooksRepository userBooksRepository;
  //private AuthorRepository authorRepository;


  @Autowired // Not Required
  public BookController(BookRepository bookRepository,
      UserBooksRepository userBooksRepository) {
    this.bookRepository = bookRepository;
    this.userBooksRepository = userBooksRepository;
  }

  @GetMapping(value = "/books/{bookId}")
  public String getBook(@PathVariable String bookId, Model model,@AuthenticationPrincipal
      OAuth2User principal) {

    Optional<Book> optionalBookById = bookRepository.findById(bookId);

    if (optionalBookById.isPresent()) {
      Book book = optionalBookById.get();
      String coverImageUrl = "/images/no-image.png";

      if (book.getCoverIds() != null && book.getCoverIds().size() > 0) {

        coverImageUrl = COVER_IMAGE_ROOT + book.getCoverIds().get(0) + "-L.jpg";

      }
      model.addAttribute("coverImage", coverImageUrl);
      model.addAttribute("book", book);

      //if principal not null add to the model attribute
      if(principal != null && principal.getAttribute("login") != null) {
        String userId = principal.getAttribute("login");
        model.addAttribute("loginId",userId);

        UserBooksPrimaryKey key = new UserBooksPrimaryKey();
        key.setBookId(bookId);
        key.setUserId(userId);

        Optional<UserBooks> optionalUserBooks = userBooksRepository.findById(key);

        if(optionalUserBooks.isPresent()) {
          model.addAttribute("userBooks",optionalUserBooks.get());
        } else {
          model.addAttribute("userBooks",new UserBooks());
        }
      }

      return "book";
    }
    return "book-not-found";
  }
}
