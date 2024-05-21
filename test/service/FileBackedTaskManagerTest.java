package service;

import exceptions.LoadingFromFileException;
import model.EpicTask;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    private Path backupFile;

    private String[] readBackupFile(Path backupFile) {
        final String content;
        try {
            content = Files.readString(backupFile);
        } catch (IOException e) {
            throw new LoadingFromFileException("Ошибка чтения/загрузки из файла.", e);
        }
        return content.split(System.lineSeparator());
    }

    @BeforeEach
    public void BeforeEach() {
        try {
            backupFile = Files.createTempFile(Paths.get("test_resources"), "backupFileTest", ".csv");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании временного файла backupFileTest.", e);
        }
        backupFile.toFile().deleteOnExit();

        manager = new FileBackedTaskManager(backupFile.toFile());

        task = new Task("TaskTitle_1", "TaskDesc_1");
        manager.createTask(task);

        epic = new EpicTask("EpicTitle_2", "EpicDesc_2");
        manager.createEpicTask(epic);
        Integer epicId = epic.getId();

        sub = new Subtask("SubtaskTitle_3", "SubtaskDesc_3", epicId);
        manager.createSubtask(sub);

        subWithStatusDone = new Subtask("newSubTitle", "newSubDesc", epicId);
        subWithStatusDone.setId(sub.getId());
        subWithStatusDone.setStatus(TaskStatus.DONE);

        subWithStatusInProgress = new Subtask("newSubTitle2", "newSubDesc2", epicId);
        subWithStatusInProgress.setId(sub.getId() + 1);
        subWithStatusInProgress.setStatus(TaskStatus.IN_PROGRESS);
    }

    @Test
    public void tasksOfNewManagerShouldBeEqualsTasksOfOldManager() {
        Task task5 = new Task("TaskTitle_5", "TaskDesc_5");
        manager.createTask(task5);

        EpicTask epic6 = new EpicTask("EpicTitle_6", "EpicDesc_6");
        manager.createEpicTask(epic6);

        Subtask sub7 = new Subtask("SubTitle_7", "SubDesc_7", epic6.getId());
        manager.createSubtask(sub7);

        TaskManager newManager = FileBackedTaskManager.loadFromFile(backupFile.toFile());

        assertEquals(manager.getAllTasks().size(), newManager.getAllTasks().size(),
                "Количество задач в менеджерах не совпадают.");
        assertEquals(manager.getAllEpicTasks().size(), manager.getAllEpicTasks().size(),
                "Количество подзадач в менеджерах не совпадают.");
        assertEquals(newManager.getAllSubtasks().size(), newManager.getAllSubtasks().size(),
                "Количество эпиков в менеджерах не совпадают.");

        for (Task t : manager.getAllTasks()) {
            int currId = t.getId();
            assertEquals(manager.getTask(currId), newManager.getTask(currId),
                    "Задачи в менеджерах с одинаковым id не совпадают.");
        }

        for (Subtask s : manager.getAllSubtasks()) {
            int currId = s.getId();
            assertEquals(manager.getSubtask(currId), newManager.getSubtask(currId),
                    "Подзадачи в менеджерах с одинаковым id не совпадают.");
        }

        for (EpicTask e : manager.getAllEpicTasks()) {
            int currId = e.getId();
            assertEquals(manager.getEpicTask(currId), newManager.getEpicTask(currId),
                    "Эпики в менеджерах с одинаковым id не совпадают.");
        }
    }

}