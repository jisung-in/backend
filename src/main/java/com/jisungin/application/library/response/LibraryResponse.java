package com.jisungin.application.library.response;

import com.jisungin.domain.library.LibraryQueryEntity;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LibraryResponse {

    private Long id;
    private String bookIsbn;
    private String status;

    @Builder
    private LibraryResponse(Long id, String bookIsbn, String status) {
        this.id = id;
        this.bookIsbn = bookIsbn;
        this.status = status;
    }

    public static LibraryResponse of(Long id, String bookIsbn, String status) {
        return LibraryResponse.builder()
                .id(id)
                .bookIsbn(bookIsbn)
                .status(status)
                .build();
    }

    public static LibraryResponse of(LibraryQueryEntity library) {
        return LibraryResponse.builder()
                .id(library.getId())
                .bookIsbn(library.getBookIsbn())
                .status(library.getReadingStatus())
                .build();
    }

    public static List<LibraryResponse> fromList(List<LibraryQueryEntity> libraries) {
        return libraries.stream()
                .map(LibraryResponse::of)
                .toList();
    }

}
