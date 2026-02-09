package com.nhnacademy.shop.review.service;

import com.nhnacademy.shop.attachment.dto.FileUploadDto;
import com.nhnacademy.shop.attachment.entity.Attachment;
import com.nhnacademy.shop.attachment.repository.AttachmentRepository;
import com.nhnacademy.shop.cloudStorage.CloudStorageService;
import com.nhnacademy.shop.member.v2.entity.Member;
import com.nhnacademy.shop.member.v2.service.MemberService;
import com.nhnacademy.shop.review.dto.ReviewPostRequest;
import com.nhnacademy.shop.review.dto.ReviewResponse;
import com.nhnacademy.shop.review.entity.Review;
import com.nhnacademy.shop.review.entity.ReviewAttachment;
import com.nhnacademy.shop.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberService memberService;
    private final CloudStorageService cloudStorageService;
    private final AttachmentRepository attachmentRepository;

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAll(pageable);
        return reviews.map(review -> new ReviewResponse(
                review.getMember().getName(),
                review.getContent(),
                review.getStar(),
                review.getAttachments().stream().
                        map(reviewAttachment -> reviewAttachment.getAttachment().getFileUrl())
                        .toList()
        ));

    }

    @Transactional
    public Long postReview(Long memberId, List<MultipartFile> multipartFiles, ReviewPostRequest request) {
        Member member = memberService.validateMember(memberId);
        List<FileUploadDto> fileUrls = cloudStorageService.uploadFiles(multipartFiles);

        // review 저장
        Review review = Review.builder()
                .member(member)
                .star(request.getStar())
                .content(request.getContent())
                .build();
        reviewRepository.save(review);
        // attachment 업로드 및 저장
        List<Attachment> attachments = fileUrls.stream()
                .map(fileUrl -> new Attachment(fileUrl.getFileUrl(), fileUrl.getFileType()))
                .toList();
        attachmentRepository.saveAll(attachments);
        // review에 attachment 추가
        for (Attachment attachment : attachments) {
            ReviewAttachment reviewAttachment = new ReviewAttachment(attachment);
            review.addReviewAttachment(reviewAttachment);
        }
        reviewRepository.save(review);
        return review.getId();
    }

    @Transactional
    public void removeReview(Long memberId, Long reviewId) {
        List<String> fileUrls = reviewRepository.findFileUrls(reviewId, memberId);
        reviewRepository.deleteById(reviewId);
        cloudStorageService.removeFiles(fileUrls);
    }
}
