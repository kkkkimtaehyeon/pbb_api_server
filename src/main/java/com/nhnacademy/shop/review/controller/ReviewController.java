package com.nhnacademy.shop.review.controller;

import com.nhnacademy.shop.common.security.MemberDetail;
import com.nhnacademy.shop.review.dto.ReviewPostRequest;
import com.nhnacademy.shop.review.dto.ReviewResponse;
import com.nhnacademy.shop.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/reviews")
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getReviews(Pageable pageable) {
        Page<ReviewResponse> reviews = reviewService.getReviews(pageable);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> postReview(@AuthenticationPrincipal MemberDetail memberDetail,
                                        @RequestPart("files") List<MultipartFile> multipartFiles,
                                        @RequestPart("data") ReviewPostRequest request) {
        Long memberId = memberDetail.getMemberId();
        Long reviewId = reviewService.postReview(memberId, multipartFiles, request);
        return ResponseEntity.ok(reviewId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@AuthenticationPrincipal MemberDetail memberDetail,
                                          @PathVariable("id") Long reviewId) {
        Long memberId = memberDetail.getMemberId();
        reviewService.removeReview(memberId, reviewId);
        return ResponseEntity.noContent().build();
    }
}
