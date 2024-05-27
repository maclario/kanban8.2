package util;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class CSVFormatter {
    private static final String SEP = ",";

    public static String getHeader() {
        return "ID,TYPE,TITLE,STATUS,DESCRIPTION,DURATION,START_TIME,EPIC_ID(ONLY_FOR_SUBTASKS)";
    }

    public static String taskToString(Task task) {
        String epicId;
        Long duration;

        if (task.getType().equals(TaskType.SUBTASK)) {
            epicId = ((Subtask)task).getEpicId().toString();
        } else {
            epicId = "";
        }

        if (task.getDuration() != null) {
            duration = task.getDuration().toMinutes();
        } else {
            duration = null;
        }

        return task.getId() + SEP +
                task.getType() + SEP +
                task.getTitle() + SEP +
                task.getStatus() + SEP +
                task.getDescription() + SEP +
                duration + SEP +
                task.getStartTime() + SEP +
                epicId;
    }

    public static Task stringToTask(String str) {
        final String[] attributes = str.split(SEP);
        final int id = Integer.parseInt(attributes[0]);
        final TaskType taskType = TaskType.valueOf(attributes[1]);
        final String title = attributes[2];
        final TaskStatus status = TaskStatus.valueOf(attributes[3]);
        final String description = attributes[4];
        final Duration duration;
        final LocalDateTime startTime;

        if (attributes[5].equals("null")) {
            duration = null;
        } else {
            duration = Duration.ofMinutes(Long.parseLong(attributes[5]));
        }

        if (attributes[6].equals("null")) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(attributes[6]);
        }

        return switch (taskType) {
            case TASK -> new Task(id, title, description, status, duration, startTime);
            case EPIC -> new EpicTask(id, title, description, status, duration, startTime);
            case SUBTASK -> new Subtask(id, title, description, status, duration, startTime, Integer.parseInt(attributes[7]));
            default -> throw new IllegalArgumentException("Ошибка получения типа задачи.");
        };
    }

}
