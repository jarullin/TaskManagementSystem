package taskmanagement.controllers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import taskmanagement.entities.Account;
import taskmanagement.entities.Task;
import taskmanagement.services.AccountService;
import taskmanagement.services.TaskService;
import taskmanagement.services.TaskUpdateException;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class TaskController {
    private final AccountService accountService;
    private final TaskService taskService;
    private final JwtEncoder jwtEncoder;

    public TaskController(AccountService accountService, TaskService taskService, JwtEncoder jwtEncoder) {
        this.accountService = accountService;
        this.taskService = taskService;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/accounts")
    public ResponseEntity<?> createAccount(@RequestParam String email, @RequestParam String password) {
        return accountService.createAccount(email, password) ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

//    @GetMapping()
//    public ResponseEntity<?> getTasks() {
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/tasks/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long taskId, @RequestParam @NotNull @NotBlank String text) {

        try {
            taskService.addComment(taskId, text);
        } catch (TaskUpdateException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@RequestParam @NotNull @NotBlank String title,
                                        @RequestParam @NotNull @NotBlank String description) {
        return ResponseEntity.ok(taskService.addTask(title, description));
    }

    @PutMapping("/tasks/{id}/assign")
    public ResponseEntity<?> assignTask(@PathVariable Long id,
                                        @RequestParam @NotNull @NotBlank String assignee) {
        try {
            return ResponseEntity.ok(taskService.setAssignee(id, assignee));
        } catch (TaskUpdateException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).build();
        }
    }

    @PutMapping("/tasks/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam @NotNull @NotBlank String newStatus) {
        if(!Set.of("CREATED","IN_PROGRESS","COMPLETED").contains(newStatus))
        {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(taskService.updateStatus(id, newStatus));
        } catch (TaskUpdateException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).build();
        }
    }


    @GetMapping("/tasks")
    public ResponseEntity<?> getAllTasks(@RequestParam String assignee, @RequestParam String author) {
        if(assignee== null && author==null){
            Iterable<Task> tasks = taskService.getAll();
            return ResponseEntity.ok(tasks);
        }
        else {
            if(author == null){
                Iterable<Task> tasks = taskService.getByAssignee(assignee);
                return ResponseEntity.ok(tasks);

            }
            if(assignee == null){
                Iterable<Task> tasks = taskService.getByAuthor(author);
                return ResponseEntity.ok(tasks);
            }
            Iterable<Task> tasks = taskService.getByAuthorAndAssignee(author, assignee);
            return ResponseEntity.ok(tasks);
        }
    }

    @PostMapping("/token")
    public String token(Authentication authentication) {
        var authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
        var claimsSet = org.springframework.security.oauth2.jwt.JwtClaimsSet.builder()
                .subject(authentication.getName())
                .issuedAt(java.time.Instant.now())
                .expiresAt(java.time.Instant.now().plusSeconds(1200))
                .claim("scope", authorities).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

}
