package com.nhnacademy.shop.review.entity;

import com.nhnacademy.shop.attachment.entity.Attachment;
import com.nhnacademy.shop.member.v2.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int star;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewAttachment> attachments;

    @Builder
    public Review(Long id, int star, String content, LocalDateTime createdAt, Member member, List<ReviewAttachment> attachments) {
        this.id = id;
        this.star = star;
        this.content = content;
        this.createdAt = createdAt;
        this.member = member;
        this.attachments = attachments;
    }

    public void addReviewAttachment(ReviewAttachment reviewAttachment) {
        if (this.attachments == null) {
            this.attachments = new ArrayList<>();
        }
        reviewAttachment.setReview(this);
        this.attachments.add(reviewAttachment);
    }
}
