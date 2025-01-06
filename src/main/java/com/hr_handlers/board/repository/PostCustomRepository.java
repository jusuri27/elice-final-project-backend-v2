package com.hr_handlers.board.repository;

import com.hr_handlers.board.entity.Post;
import com.hr_handlers.board.enums.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCustomRepository {
    Page<Post> findActivePostsByType(PostType postType, Pageable pageable);
}
