package model;

import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<Integer> subtasks = new ArrayList<>();

    public EpicTask(String title, String description) {
        super(title, description);
    }

    public EpicTask(Integer id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
    }

    public TaskType getType() {
        return TaskType.EPIC;
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
                '}';
    }
}