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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
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

    @Override
    @BeforeEach
    public void beforeEach() {
        try {
            backupFile = Files.createTempFile(Paths.get("test_resources"), "backupFileTest", ".csv");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании временного файла backupFileTest.", e);
        }

        backupFile.toFile().deleteOnExit();
        manager = new FileBackedTaskManager(backupFile.toFile());
        super.beforeEach();
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

    @Test
    public void prioritizedTasksOfOldManagerEqualsPrioritizedTasksOfNewManager() {
        LocalDateTime minStartTime = LocalDateTime.of(2024, Month.MAY, 1,  9, 0);
        LocalDateTime midStartTime = minStartTime.plusHours(1).plusMinutes(15);
        LocalDateTime maxStartTime = midStartTime.plusHours(2).plusMinutes(30);

        Task task2 = new Task(102, "t2", "d2", TaskStatus.NEW, Duration.ofMinutes(30), minStartTime);
        Task task3 = new Task(103, "t3", "d3", TaskStatus.NEW, Duration.ofMinutes(30), midStartTime);
        Task task4 = new Task(104, "t4", "d4", TaskStatus.NEW, Duration.ofMinutes(30), maxStartTime);

        manager.createTask(task3);
        manager.createTask(task4);
        manager.createTask(task2);

        TaskManager newManager = FileBackedTaskManager.loadFromFile(backupFile.toFile());
        ArrayList<Task> oldTasksList = manager.getPrioritizedTasks();
        ArrayList<Task> newTasksList = (ArrayList<Task>) newManager.getPrioritizedTasks();

        assertEquals(oldTasksList.size(), newTasksList.size(), "Размеры списков должны быть равны.");

        for (int i = 0; i < oldTasksList.size(); i++) {
            assertEquals(oldTasksList.get(i), newTasksList.get(i),
                    "Задачи c индексом " + i + "не совпадают.");
        }

    }

}