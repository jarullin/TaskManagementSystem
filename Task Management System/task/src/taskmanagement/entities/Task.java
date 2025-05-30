package taskmanagement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String status;
    private String author;
    private String assignee;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;

    @Transient
    private Integer totalComments;


    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setTask(this);
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }


    public Task(String title, String description, String author) {
        this.title = title;
        this.description = description;
        this.status = "CREATED";
        this.author = author.toLowerCase();
        this.assignee = "none";
    }

    public Task() {

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalComments() {
        return comments.size();
    }
}
