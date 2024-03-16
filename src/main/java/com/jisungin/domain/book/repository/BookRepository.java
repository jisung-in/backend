package com.jisungin.domain.book.repository;

import com.jisungin.domain.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    Boolean existsBookByIsbn(String isbn);

}
