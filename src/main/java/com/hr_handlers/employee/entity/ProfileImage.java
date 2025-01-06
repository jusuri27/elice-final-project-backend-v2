package com.hr_handlers.employee.entity;

import com.hr_handlers.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProfileImage extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(name = "profile_image_url", length = 1000)
    @Comment(value = "프로필 이미지 URL")
    private String profileImageUrl;

    @OneToOne(mappedBy = "profileImage", fetch = FetchType.LAZY)
    private Employee employee;
}