package mappers;

import model.*;

public class CSVFormatter {

    public static String getHeader() {
        return "ID,TYPE,TITLE,STATUS,DESCRIPTION,EPIC_ID(ONLY_FOR_SUBTASKS)";
    }

    public static String taskToString(Task task) {
        String epicId;

        if (task.getType().equals(TaskType.SUBTASK)) {
            epicId = ((Subtask) task).getEpicId().toString();
        } else {
            epicId = "";
        }

        return task.getId() + "," + task.getType() + "," + task.getTitle() + "," +
                task.getStatus() + "," + task.getDescription() + "," + epicId;
    }

    public static Task stringToTask(String str) {
        final String[] attributes = str.split(",");
        final int id = Integer.parseInt(attributes[0]);
        final TaskType taskType = TaskType.valueOf(attributes[1]);
        final String title = attributes[2];
        final TaskStatus status = TaskStatus.valueOf(attributes[3]);
        final String description = attributes[4];

        return switch (taskType) {
            case TASK -> new Task(id, title, description, status);
            case EPIC -> new EpicTask(id, title, description, status);
            case SUBTASK -> new Subtask(id, title, description, status, Integer.parseInt(attributes[5]));
            default -> throw new IllegalArgumentException("Ошибка получения типа задачи.");
        };
    }

}
