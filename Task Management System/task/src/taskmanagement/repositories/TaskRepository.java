package taskmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import taskmanagement.entities.Task;

import java.util.List;

public interface TaskRepository  extends JpaRepository<Task, Long> {
    List<Task> findByAssignee(String assignee);

    Iterable<Task> findByAuthor(String author);

    Iterable<Task> findByAuthorAndAssignee(String author, String assignee);
}
