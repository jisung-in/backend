package com.jisungin.domain.library.repository;

import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.library.Library;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LibraryRepository extends JpaRepository<Library, Long>, LibraryRepositoryCustom {

    @Query(
            "SELECT l.status FROM Library l JOIN l.user u JOIN l.book b WHERE u.id = :id AND b.isbn = :isbn"
    )
    Optional<ReadingStatus> findReadingStatusByUserId(@Param("id") Long userId, @Param("isbn") String isbn);

    @Query(
            "SELECT l FROM Library l "
                    + "JOIN FETCH l.book "
                    + "JOIN FETCH l.user "
                    + "WHERE l.id = :id"
    )
    Optional<Library> findByIdWithBookAndUser(@Param("id") Long id);

}
