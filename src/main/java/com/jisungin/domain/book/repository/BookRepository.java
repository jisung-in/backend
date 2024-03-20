package com.jisungin.domain.book.repository;

import com.jisungin.domain.book.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    Boolean existsBookByIsbn(String isbn);

}
