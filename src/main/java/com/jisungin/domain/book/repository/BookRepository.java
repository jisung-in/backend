package com.jisungin.domain.book.repository;

import com.jisungin.domain.book.Book;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String>, BookRepositoryCustom {

    Boolean existsBookByIsbn(String isbn);

    @Query("SELECT b.isbn FROM Book b WHERE b.isbn IN :isbns")
    Set<String> findExistIsbns(@Param("isbns") List<String> isbns);

}
