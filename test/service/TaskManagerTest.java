package service;

import model.EpicTask;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {
    protected TaskManager manager;
    protected Task task;
    protected Subtask sub;
    protected EpicTask epic;
    protected Subtask subWithStatusDone;
    protected Subtask subWithStatusInProgress;

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();

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
}
