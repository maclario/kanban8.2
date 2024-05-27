package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String title, String description, Integer epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String title, String description, TaskStatus status,
                   Duration duration, LocalDateTime startTime, Integer epicId) {
        super(id, title, description, status, duration, startTime);
        this.epicId = epicId;
    }


    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", epicId=" + epicId +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                '}';
    }
}
