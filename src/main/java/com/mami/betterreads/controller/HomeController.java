package com.mami.betterreads.controller;

import com.mami.betterreads.user.BooksByUser;
import com.mami.betterreads.user.BooksByUserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

  private static final String COVER_IMAGE_ROOT = "https://covers.openlibrary.org/b/id/";
  private static final String LOGO = "/images/logo.png";

  private BooksByUserRepository booksByUserRepository;

  public HomeController(BooksByUserRepository booksByUserRepository) {
    this.booksByUserRepository = booksByUserRepository;
  }

  @GetMapping("/")
  public String home(@AuthenticationPrincipal OAuth2User principal, Model model) {

    model.addAttribute("logo",LOGO);

    if (principal == null || principal.getAttribute("login") == null) {
      System.out.println("login null");
      return "index.html";
    }

    String userId = principal.getAttribute("login");

    Slice<BooksByUser> booksSlice = booksByUserRepository
        .findAllById(userId, CassandraPageRequest.of(0, 100));

    List<BooksByUser> booksByUser = booksSlice.getContent();

    booksByUser = booksByUser.stream()
        .distinct()
        .map( book -> {
          String coverImageUrl = "/images/no-image.png";
          if(book.getCoverIds() != null && book.getCoverIds().size() > 0) {
            coverImageUrl = COVER_IMAGE_ROOT + book.getCoverIds().get(0) + "-M.jpg";
          }
          book.setCoverUrl(coverImageUrl);
          return book;
        })
        .collect(Collectors.toList());

    model.addAttribute("books", booksByUser);

    return "home";
  }

  @PostMapping("/delete")
  public String removeUserBook(@RequestParam String bookId,
      @AuthenticationPrincipal OAuth2User principal) {

    final String userId = principal.getAttribute("login");

    booksByUserRepository.deleteByIdAndBookId(userId, bookId);

    return "redirect:/";

  }
}
