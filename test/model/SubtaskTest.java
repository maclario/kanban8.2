package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubtaskTest {
    private static Subtask sub;
    private static EpicTask epic;

    @BeforeEach
    public void beforeEach() {
        epic = new EpicTask("EpicTitle_1", "EpicDesc_1");
        epic.setId(1);
        sub = new Subtask("SubTitle_2", "SubDesc_2", 1);
    }

    @Test
    public void titleOfCreatedSubtaskIsNotNull() {
        assertNotNull(sub.getTitle());
    }

    @Test
    public void descriptionOfCreatedSubtaskIsNotNull() {
        assertNotNull(sub.getDescription());
    }

    @Test
    public void subtaskTitleIsStringType() {
        assertEquals(String.class, sub.getTitle().getClass());
    }

    @Test
    public void subtaskDescriptionIsStringType() {
        assertEquals(String.class, sub.getDescription().getClass());
    }

    @Test
    public void idIsAssignedToSubtask() {
        sub.setId(42);
        assertEquals(42, sub.getId());
    }

    @Test
    public void newTitleIsAssignedToSubtask() {
        sub.setTitle("SubTitle_new");
        assertEquals("SubTitle_new", sub.getTitle());
    }

    @Test
    public void newDescriptionIsAssignedToSubtask() {
        sub.setDescription("SubDesc_new");
        assertEquals("SubDesc_new", sub.getDescription());
    }

    @Test
    public void newSubtaskHasStatusNew() {
        assertEquals(TaskStatus.NEW, sub.getStatus());
    }

    @Test
    public void newStatusIsAssignedToSubtask() {
        sub.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, sub.getStatus());
    }

    @Test
    public void epicIdOfSubtaskInNotNull() {
        assertNotNull(sub.getEpicId());
    }

}
