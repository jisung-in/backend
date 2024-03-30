package com.jisungin.application.user;

import com.jisungin.application.PageResponse;
import com.jisungin.application.review.response.RatingFindAllResponse;
import com.jisungin.application.user.request.UserRatingGetAllServiceRequest;
import com.jisungin.domain.review.repository.ReviewRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jisungin.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    private final ReviewRepository reviewRepository;

    public PageResponse<RatingFindAllResponse> getUserRatings(Long userId, UserRatingGetAllServiceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        return reviewRepository.findAllRatingOrderBy(user.getId(), request.getOrderType(), request.getSize(), request.getOffset());
    }

}
