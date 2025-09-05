package com.goorm.derere.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "restaurant_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "restaurant_id"})
)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private Boolean liked = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 생성자
    public Like(User user, Restaurant restaurant, Boolean liked) {
        validateLikeData(user, restaurant, liked);
        this.user = user;
        this.restaurant = restaurant;
        this.liked = liked;
    }

    // 좋아요 상태 변경
    public void toggleLike(Boolean liked) {
        this.liked = liked;
    }

    // 유효성 검증
    private void validateLikeData(User user, Restaurant restaurant, Boolean liked) {
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보가 필요합니다.");
        }
        if (restaurant == null) {
            throw new IllegalArgumentException("음식점 정보가 필요합니다.");
        }
        if (liked == null) {
            throw new IllegalArgumentException("좋아요 상태가 필요합니다.");
        }
    }
}