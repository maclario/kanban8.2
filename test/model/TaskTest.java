package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskTest {
    private static Task task;

    @BeforeEach
    public void beforeEach() {
        task = new Task("TaskTitle_1", "TaskDesc_1");
    }

    @Test
    public void titleOfCreatedTaskIsNotNull() {
        assertNotNull(task.getTitle());
    }

    @Test
    public void descriptionOfCreatedTaskIsNotNull() {
        assertNotNull(task.getDescription());
    }

    @Test
    public void taskTitleIsStringType() {
        assertEquals(String.class, task.getTitle().getClass());
    }

    @Test
    public void taskDescriptionIsStringType() {
        assertEquals(String.class, task.getDescription().getClass());
    }

    @Test
    public void idIsAssignedToTask() {
        task.setId(37);
        assertEquals(37, task.getId());
    }

    @Test
    public void newTitleIsAssignedToTask() {
        task.setTitle("TaskTitle_new");
        assertEquals("TaskTitle_new", task.getTitle());
    }

    @Test
    public void newDescriptionIsAssignedToTask() {
        task.setDescription("TaskDesc_new");
        assertEquals("TaskDesc_new", task.getDescription());
    }

    @Test
    public void newTaskHasStatusNew() {
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    public void newStatusIsAssignedToTask() {
        task.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

}
