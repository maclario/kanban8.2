package service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {
    private static HistoryManager historyManager;
    private static List<Task> history;
    private static Task task1;
    private static Task task2;
    private static Task task3;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Title of task1", "Description of task1");
        task1.setId(1);
        task2 = new Task("Title of task 2", "Description of task2");
        task2.setId(2);
        task3 = new Task("Title of task3", "Description of task3");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
    }

    @Test
    public void shouldAddTasksToHistory() {
        history = historyManager.getHistory();

        assertEquals(3, history.size(), "Размер истории не совпадает.");
        assertEquals(task1, history.get(0), "Задача task1 не добавлена в историю.");
        assertEquals(task2, history.get(1), "Задача task2 не добавлена в историю.");
        assertEquals(task3, history.get(2), "Задача task3 не добавлена в историю.");
    }

    @Test
    public void historyShouldNotContainsDuplicates() {
        int originalHistorySize = historyManager.getHistory().size();
        historyManager.add(task2);
        history = historyManager.getHistory();

        assertEquals(originalHistorySize, history.size(), "Размер истории изменился.");
    }

    @Test
    public void historyShouldContainOriginalTasksAfterUpdate() {
        Task savedTask1 = new Task(task1.getTitle(), task1.getDescription());
        savedTask1.setId(task1.getId());
        Task savedTask2 = new Task(task2.getTitle(), task2.getDescription());
        savedTask2.setId(task2.getId());
        Task savedTask3 = new Task(task3.getTitle(), task3.getDescription());
        savedTask3.setId(task3.getId());

        task1.setTitle("Обновленный Title of task1");
        task1.setDescription("Обновленный Description of task1");
        task1.setStatus(TaskStatus.DONE);
        task2.setTitle("Обновленный Title of task2");
        task2.setDescription("Обновленный Description of task2");
        task2.setStatus(TaskStatus.DONE);
        task3.setTitle("Обновленный Title of task3");
        task3.setDescription("Обновленный Description of task3");
        task3.setStatus(TaskStatus.DONE);

        history = historyManager.getHistory();

        assertEquals(savedTask1, history.get(0), "Атрибуты задачи id: 1, сохраненной в истории, изменились.");
        assertEquals(savedTask2, history.get(1), "Атрибуты задачи id: 2, сохраненной в истории, изменились.");
        assertEquals(savedTask3, history.get(2), "Атрибуты задачи id: 3, сохраненной в истории, изменились.");
    }

    @Test
    public void shouldRemoveTaskFromHistoryById() {
        int randomTaskId = new Random().nextInt(1, 4);
        historyManager.remove(randomTaskId);
        history = historyManager.getHistory();

        assertEquals(2, history.size(), "Размер истории не уменьшился на единицу.");
    }

    @Test
    public void shouldRemoveTaskFromStartHistory() {
        historyManager.remove(1);
        history = historyManager.getHistory();

        assertEquals(2, history.size(), "Размер истории не уменьшился на единицу.");
        assertEquals(task2, history.get(0), "Первым элементом должна стать задача c id: 2.");
        assertEquals(task3, history.get(1), "Вторым и последним элементом должна стать задача с id: 3.");
    }

    @Test
    public void shouldRemoveTaskFromMidOfHistory() {
        historyManager.remove(2);
        history = historyManager.getHistory();

        assertEquals(2, history.size(), "Размер истории не уменьшился на единицу.");
        assertEquals(task1, history.get(0), "Первым элементом должна остаться задача с id: 1.");
        assertEquals(task3, history.get(1), "Вторым и последним элементом истории должна быть задача с id: 3.");
    }

    @Test
    public void shouldRemoveTaskFromEndOfHistory() {
        historyManager.remove(3);
        history = historyManager.getHistory();

        assertEquals(2, history.size(), "Резмер истории не уменьшился на единицу.");
        assertEquals(task1, history.get(0), "Первым элементом должна остаться задача с id: 1.");
        assertEquals(task2, history.get(1), "Вторым и последним элементом истории должна быть задача с id: 2.");
    }

    @Test
    public void historyShouldBeEmptyAfterRemoveAllTasks() {
        historyManager.remove(1);
        historyManager.remove(2);
        historyManager.remove(3);
        history = historyManager.getHistory();

        assertTrue(history.isEmpty(), "История должна стать пустой.");
    }

}
