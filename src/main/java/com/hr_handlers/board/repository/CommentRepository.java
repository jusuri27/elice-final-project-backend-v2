package com.hr_handlers.board.repository;

import com.hr_handlers.board.entity.Comment;
import com.hr_handlers.board.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostAndParentIsNullAndIsDelete(Post post, String isDelete, Pageable pageable);
    List<Comment> findByPostAndIsDelete(Post post, String isDelete);
}
