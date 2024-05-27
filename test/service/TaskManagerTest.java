package service;

import exceptions.InvalidReceivedTimeException;
import model.EpicTask;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected Task task;
    protected Subtask sub;
    protected EpicTask epic;
    protected Subtask subWithStatusDone;
    protected Subtask subWithStatusInProgress;

    @BeforeEach
    public void beforeEach() {
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
    public void idShouldIncreaseBy1AfterExecutingMethodGenerateId() {
        Integer startingId = manager.getId();
        manager.generateId();
        Integer newId = manager.getId();
        assertEquals(startingId + 1, newId);
    }

    @Test
    public void newTaskAddedToAllTasksList() {
        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    public void newSubtaskAddedToAllSubtasksList() {
        assertEquals(1, manager.getAllSubtasks().size());
    }

    @Test
    public void newEpicTaskAddedToAllSubtasksList() {
        assertEquals(1, manager.getAllEpicTasks().size());
    }

    @Test
    public void allTasksListIsEmptyWhenDeleteAllTasks() {
        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void allSubtasksListIsEmptyWhenDeleteAllSubtasks() {
        manager.deleteAllSubtasks();
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void allEpictasksListIsEmptyWhenDeleteAllEpictasks() {
        manager.deleteAllEpicTasks();
        assertTrue(manager.getAllEpicTasks().isEmpty());
    }

    @Test
    public void allSubtasksListIsEmptyWhenDeleteAllEpictasks() {
        manager.deleteAllEpicTasks();
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void methodGetTaskReturnTask() {
        Integer taskId = task.getId();
        assertNotNull(manager.getTask(taskId), "Получен null.");
    }

    @Test
    public void AnyTaskAddedToHistoryAfterGetTask() {
        manager.getTask(task.getId());
        manager.getSubtask(sub.getId());
        manager.getEpicTask(epic.getId());
        assertEquals(3, manager.getHistory().size());
    }

    @Test
    public void subtaskRemovedFromEpicWhenDelete() {
        manager.deleteSubtask(sub.getId());
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    public void idSubtaskEqualsIdSubtaskInEpicSubtasks() {
        assertEquals(sub.getId(), epic.getSubtasks().getFirst());
    }

    @Test
    public void epicHasStatusNewIfEpicHasNotSubtasks() {
        EpicTask emptyEpic = new EpicTask("EmptyEpicTitle", "EmptyEpicDesc");
        assertEquals(TaskStatus.NEW, emptyEpic.getStatus());
    }

    @Test
    public void epicHasStatusDoneIfAllSubtasksHasStatusDone() {
        manager.updateSubtask(subWithStatusDone);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void epicStatusIsInProgressIfEpicSubsListHasSubtaskWithStatusInProgress() {
        manager.updateSubtask(subWithStatusDone);
        manager.createSubtask(sub);
        manager.updateSubtask(subWithStatusInProgress);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void epicStatusIsInProgressIfEpicSubsListHasSubtaskWithStatusNew() {
        manager.updateSubtask(subWithStatusDone);
        manager.createSubtask(sub);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldThrowsExceptionIfTimeIntersectionDetected() {
        Duration time = Duration.ofMinutes(30);
        LocalDateTime start1 = LocalDateTime.of(2024, Month.MAY, 1,  12, 0);
        LocalDateTime start2 = start1.minusMinutes(15);
        Task task2 = new Task(101, "title", "desc", TaskStatus.NEW, time, start1);
        Task task3 = new Task(102, "title", "desc", TaskStatus.NEW, time, start2);
        manager.createTask(task2);
        final int oldTasksCounter = manager.getAllTasks().size();

        assertThrows(InvalidReceivedTimeException.class, () -> manager.createTask(task3),
                "Новая задача пересекается по времени с существующей задачей. " +
                "\nДанный сценарий должен вызвать выброс исключения InvalidReceivedTimeException.");

        final int newTasksCounter = manager.getAllTasks().size();
        assertEquals(oldTasksCounter, newTasksCounter, "Новая задача не должна была добавиться в список.");
    }

    @Test
    public void getPrioritizedTasksTest() {
        Task[] correctManualSortedArray = new Task[3];
        LocalDateTime minStartTime = LocalDateTime.of(2024, Month.MAY, 1,  9, 0);
        LocalDateTime midStartTime = minStartTime.plusHours(1).plusMinutes(15);
        LocalDateTime maxStartTime = midStartTime.plusHours(2).plusMinutes(30);

        Task task2 = new Task(102, "t2", "d2", TaskStatus.NEW, Duration.ofMinutes(30), minStartTime);
        Task task3 = new Task(103, "t3", "d3", TaskStatus.NEW, Duration.ofMinutes(30), midStartTime);
        Task task4 = new Task(104, "t4", "d4", TaskStatus.NEW, Duration.ofMinutes(30), maxStartTime);

        manager.createTask(task3);
        manager.createTask(task4);
        manager.createTask(task2);
        correctManualSortedArray[0] = task2;
        correctManualSortedArray[1] = task3;
        correctManualSortedArray[2] = task4;

        ArrayList<Task> prioritizedTasks = (ArrayList<Task>) manager.getPrioritizedTasks();
        assertEquals(3, prioritizedTasks.size(), "Размеры массива и списка должны быть равны.");

        for (int i = 0; i < prioritizedTasks.size(); i++) {
            assertEquals(correctManualSortedArray[i], prioritizedTasks.get(i), "Отсортированные результаты не совпадают.");
        }
    }

}
