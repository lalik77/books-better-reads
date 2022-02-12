package com.mami.betterreads.controller;

import com.mami.betterreads.book.Book;
import com.mami.betterreads.book.BookRepository;
import com.mami.betterreads.exception.ApiSubmissionException;
import com.mami.betterreads.exception.BetterReadsException;
import com.mami.betterreads.user.BooksByUser;
import com.mami.betterreads.user.BooksByUserRepository;
import com.mami.betterreads.userbooks.UserBooks;
import com.mami.betterreads.userbooks.UserBooksPrimaryKey;
import com.mami.betterreads.userbooks.UserBooksRepository;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserBooksController {

  private Logger logger = LoggerFactory.getLogger(UserBooksController.class);


  private BookRepository bookRepository;

  private BooksByUserRepository booksByUserRepository;

  private UserBooksRepository userBooksRepository;

  public UserBooksController(BookRepository bookRepository,
      BooksByUserRepository booksByUserRepository,
      UserBooksRepository userBooksRepository) {
    this.bookRepository = bookRepository;
    this.booksByUserRepository = booksByUserRepository;
    this.userBooksRepository = userBooksRepository;
  }

  @PostMapping("/addUserBook")
  public ModelAndView addBookForUser(@RequestBody MultiValueMap<String, String> formData,
      @AuthenticationPrincipal OAuth2User principal, Model model) {

    if(principal== null || principal.getAttribute("login") == null) {
      return null;
    }
    UserBooks userBooks = new UserBooks();
    UserBooksPrimaryKey key = new UserBooksPrimaryKey();

    key.setUserId(principal.getAttribute("login"));
    String bookId = formData.getFirst("bookId");

    // added in missed part of video 15
    Optional<Book> optionalBook = bookRepository.findById(bookId);

    if(!optionalBook.isPresent()) {
      return new ModelAndView("redirect:/");
    }
    Book book = optionalBook.get();
    // till here
     LocalDate startedDate=null;
     LocalDate completedDate=null;

    try {
      startedDate = LocalDate.parse(formData.getFirst("startedDate"));
      completedDate = LocalDate.parse(formData.getFirst("completedDate"));
      userBooks.setStartedDate(startedDate);
      userBooks.setCompletedDate(completedDate);
    }catch (DateTimeParseException e) {

      logger.error("DateTimeParse Exception");
      throw new BetterReadsException(e.getMessage(),e.getParsedString(),e.getErrorIndex());

    }
    catch (RuntimeException e) {
     logger.error("Runtime Exception");
    }

    //TODO if completed date < start date -> throw an exception
    final long start = startedDate.toEpochDay();
    final long completed = completedDate.toEpochDay();
    if (completed<start) {
      throw new ApiSubmissionException("Oops, completed date cannot be earlier than started!");
      //throw new RuntimeException("Oops, completed date cannot be earlier than started!");
    }

    logger.info("Start Date : " + startedDate + "  -  " + start);
    logger.info("Completed Date : " + completedDate + "  -  " + completed );


    key.setBookId(bookId); // tricky -> create a hidden form in book.html to intercept this id
    userBooks.setKey(key);
    userBooks.setRating(Integer.parseInt(formData.getFirst("rating")));
    userBooks.setReadingStatus(formData.getFirst("readingStatus"));
    userBooksRepository.save(userBooks);

    // This part is missed in the video 15

    final int rating  = Integer.parseInt(formData.getFirst("rating"));

    String userId = principal.getAttribute("login");
    BooksByUser booksByUser = new BooksByUser();
    booksByUser.setId(userId);
    booksByUser.setBookId(bookId);
    booksByUser.setBookName(book.getName());
    booksByUser.setCoverIds(book.getCoverIds());
    booksByUser.setAuthorNames(book.getAuthorNames());
    booksByUser.setReadingStatus(formData.getFirst("readingStatus"));
    booksByUser.setRating(rating);

    booksByUserRepository.save(booksByUser);

   // return new ModelAndView("redirect:/books/"+ bookId);
    return new ModelAndView("redirect:/");
  }

}
