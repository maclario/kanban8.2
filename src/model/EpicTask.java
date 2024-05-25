package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<Integer> subtasks = new ArrayList<>();

    public EpicTask(String title, String description) {
        super(title, description);
    }

    public EpicTask(Integer id, String title, String description,
                    TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(id, title, description, status, duration, startTime);
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void addSubtask(Integer id) {
        subtasks.add(id);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void removeLinkedSubtask(Integer id) {
        subtasks.remove(id);
    }

    public void deleteSubtasks() {
        subtasks.clear();
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}