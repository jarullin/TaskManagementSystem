package taskmanagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Comment {
    @Id
    private Long id;
    private String text;
    @ManyToOne
    private Task task;
    private String author;

    public Comment(String text, Task task, String author) {
        this.text = text;
        this.task = task;
        this.author = author;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
