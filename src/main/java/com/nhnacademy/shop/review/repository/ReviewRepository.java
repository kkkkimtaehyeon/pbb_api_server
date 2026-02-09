package com.nhnacademy.shop.review.repository;

import com.nhnacademy.shop.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("""
                select a.fileUrl
                from ReviewAttachment ra
                join ra.attachment a
                where ra.review.id = :reviewId
                  and ra.review.member.id = :memberId
            """)
    List<String> findFileUrls(@Param("reviewId") Long reviewId, @Param("memberId") Long memberId);
}
