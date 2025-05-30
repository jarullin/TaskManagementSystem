package taskmanagement.services;

public class TaskUpdateException extends Exception {
    public TaskService.TaskErrorCode getErrorCode() {
        return errorCode;
    }

    private final TaskService.TaskErrorCode errorCode;

    public TaskUpdateException(TaskService.TaskErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }
}
