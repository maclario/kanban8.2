package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class EpicTaskTest {
    private static EpicTask epic;
    private static Subtask sub;

    @BeforeEach
    public void beforeEach() {
        epic = new EpicTask("EpicTitle_4", "EpicDesc_4");
    }

    @Test
    public void titleOfCreatedEpicIsNotNull() {
        assertNotNull(epic.getTitle());
    }

    @Test
    public void descriptionOfCreatedEpicIsNotNull() {
        assertNotNull(epic.getDescription());
    }

    @Test
    public void epicTitleIsStringType() {
        assertEquals(String.class, epic.getTitle().getClass());
    }

    @Test
    public void taskDescriptionIsStringType() {
        assertEquals(String.class, epic.getDescription().getClass());
    }

    @Test
    public void idIsAssignedToEpic() {
        epic.setId(55);
        assertEquals(55, epic.getId());
    }

    @Test
    public void newTitleIsAssignedToEpic() {
        epic.setTitle("EpicTitle_new");
        assertEquals("EpicTitle_new", epic.getTitle());
    }

    @Test
    public void newDescriptionIsAssignedToEpic() {
        epic.setDescription("EpicDesc_new");
        assertEquals("EpicDesc_new", epic.getDescription());
    }

    @Test
    public void newEpicHasStatusNew() {
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void newEpicHasEmptySubtaskList() {
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    public void subtaskIsAddedToEpic() {
        epic.setId(2);
        sub = new Subtask("SubTitle_3", "SubDesc_3", 2);
        sub.setId(3);
        epic.addSubtask(3);
        assertFalse(epic.getSubtasks().isEmpty());
    }

}
