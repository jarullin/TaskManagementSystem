package taskmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import taskmanagement.entities.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Iterable<Comment> findByTask(Long taskId);
}
