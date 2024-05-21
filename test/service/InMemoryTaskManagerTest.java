package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryTaskManagerTest extends TaskManagerTest {

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

}
