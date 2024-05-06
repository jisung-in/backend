package com.jisungin.application.book;

import com.jisungin.application.PageResponse;
import com.jisungin.application.SearchServiceRequest;
import com.jisungin.application.book.request.BookCreateServiceRequest;
import com.jisungin.application.book.request.BookServicePageRequest;
import com.jisungin.application.book.response.BookRelatedTalkRoomPageResponse;
import com.jisungin.application.book.response.BookRelatedTalkRoomResponse;
import com.jisungin.application.book.response.BookResponse;
import com.jisungin.application.book.response.SimpleBookResponse;
import com.jisungin.application.talkroom.response.TalkRoomQueryResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.rating.repository.RatingRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRoleRepository;
import com.jisungin.domain.talkroomlike.repository.TalkRoomLikeRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import com.jisungin.infra.crawler.Crawler;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final Crawler crawler;
    private final BookRepository bookRepository;
    private final TalkRoomRepository talkRoomRepository;
    private final TalkRoomRoleRepository talkRoomRoleRepository;
    private final TalkRoomLikeRepository talkRoomLikeRepository;
    private final RatingRepository ratingRepository;

    public BookResponse getBook(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        Double averageRating = ratingRepository.findAverageRatingByBookId(book.getIsbn());

        return BookResponse.of(book, averageRating);
    }

    public PageResponse<SimpleBookResponse> getBooks(SearchServiceRequest params) {
        return bookRepository.getBooks(params.getOffset(), params.getSize(), params.getOrder());
    }

    public BookRelatedTalkRoomPageResponse getBookRelatedTalkRooms(String isbn, BookServicePageRequest request,
                                                                   Long userId
    ) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        List<TalkRoomQueryResponse> talkRooms = talkRoomRepository.findTalkRoomsRelatedBook(book.getIsbn(),
                request.getOffset(), request.getSize());

        List<Long> talkRoomIds = extractTalkRoomIds(talkRooms);

        Map<Long, List<ReadingStatus>> readingStatuses = talkRoomRoleRepository.findTalkRoomRoleByIds(talkRoomIds);

        List<BookRelatedTalkRoomResponse> responses = BookRelatedTalkRoomResponse.create(talkRooms, readingStatuses);

        long totalCount = talkRoomRepository.countTalkRoomsRelatedBook(isbn);

        List<Long> likeTalkRoomIds = (userId != null)
                ? talkRoomLikeRepository.findLikeTalkRoomIdsByUserId(userId, talkRoomIds)
                : Collections.emptyList();

        return BookRelatedTalkRoomPageResponse.of(PageResponse.of(request.getSize(), totalCount, responses),
                likeTalkRoomIds);
    }

    @Transactional
    public BookResponse createBook(BookCreateServiceRequest request) {
        if (bookRepository.existsBookByIsbn(request.getIsbn())) {
            throw new BusinessException(ErrorCode.BOOK_ALREADY_EXIST);
        }

        BookCreateServiceRequest newServiceRequest = crawler.crawlBook(request.getIsbn()).toServiceRequest();

        return BookResponse.of(bookRepository.save(newServiceRequest.toEntity()));
    }

    @Transactional
    public void addNewBooks(List<BookCreateServiceRequest> requests) {
        requests.stream()
                .filter(request -> !bookRepository.existsBookByIsbn(request.getIsbn()))
                .map(BookCreateServiceRequest::toEntity)
                .forEach(bookRepository::save);
    }

    private List<Long> extractTalkRoomIds(List<TalkRoomQueryResponse> talkRooms) {
        return talkRooms.stream()
                .map(TalkRoomQueryResponse::getId)
                .toList();
    }

}
