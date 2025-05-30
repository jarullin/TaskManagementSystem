package taskmanagement.services;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import taskmanagement.entities.Account;
import taskmanagement.entities.Comment;
import taskmanagement.entities.Task;
import taskmanagement.repositories.CommentRepository;
import taskmanagement.repositories.TaskRepository;

import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final AccountService accountService;
    private final CommentRepository commentRepository;
    private final String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails()).getUsername();
    public TaskService(TaskRepository taskRepository, AccountService accountService, CommentRepository commentRepository) {
        this.taskRepository = taskRepository;
        this.accountService = accountService;
        this.commentRepository = commentRepository;
    }

    public Iterable<Task> getByAssignee(String assignee) {
        return taskRepository.findByAssignee(assignee);
    }

    public Iterable<Task> getByAuthor(String author) {
        return taskRepository.findByAuthor(author);
    }

    public Iterable<Task> getByAuthorAndAssignee(String author, String assignee) {
        return taskRepository.findByAuthorAndAssignee(author, assignee);
    }

    public void addComment(Long taskId, String text) throws TaskUpdateException {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskUpdateException(TaskErrorCode.NOT_FOUND));
        task.addComment(new Comment(text, task, username));
        taskRepository.saveAndFlush(task);
    }

    public enum TaskErrorCode {
        BAD_REQUEST(400),
        FORBIDDEN(403),
        NOT_FOUND(404);

        private final int httpStatus;

        TaskErrorCode(int httpStatus) {
            this.httpStatus = httpStatus;
        }

        public int getHttpStatus() {
            return httpStatus;
        }
    }


    public Task addTask(String title, String description) {
       return taskRepository.save(new Task(title, description, username));
    }

    public Iterable<Task> getAll() {
        return taskRepository.findAll();
    }

    public Task updateStatus(Long id, String newStatus) throws TaskUpdateException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskUpdateException(TaskErrorCode.NOT_FOUND));
        if (username.equals(task.getAuthor()) || username.equals(task.getAssignee())) {
            task.setStatus(newStatus);
            return taskRepository.saveAndFlush(task);
        } else {
            throw new TaskUpdateException(TaskErrorCode.FORBIDDEN);
        }
    }

    public Task setAssignee(Long id, String assignee) throws TaskUpdateException {

        String emailPattern = "^(.+)@(\\S+)$";
        if (assignee.equals("none") || assignee.matches(emailPattern)) {
            if (accountService.existsByEmail(assignee)) {
                Task task = taskRepository.findById(id).orElseThrow(() -> new TaskUpdateException(TaskErrorCode.NOT_FOUND));
                if (task.getAuthor().equals(username)) {
                    task.setAssignee(assignee);
                    return taskRepository.saveAndFlush(task);
                } else {
                    throw new TaskUpdateException(TaskErrorCode.FORBIDDEN);
                }
            } else {
                throw new TaskUpdateException(TaskErrorCode.NOT_FOUND);
            }
        } else {
            throw new TaskUpdateException(TaskErrorCode.BAD_REQUEST);
        }
    }
}
