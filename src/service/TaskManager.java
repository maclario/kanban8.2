package service;

import model.EpicTask;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    Integer getId();

    Integer generateId();

    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void createEpicTask(EpicTask epictask);

    ArrayList<Task> getAllTasks();

    ArrayList<EpicTask> getAllEpicTasks();

    ArrayList<Subtask> getAllSubtasks();

    Task getTask(Integer id);

    EpicTask getEpicTask(Integer id);

    Subtask getSubtask(Integer id);

    void deleteAllTasks();

    void deleteAllEpicTasks();

    void deleteAllSubtasks();

    void deleteTask(Integer id);

    void deleteEpicTask(Integer id);

    void deleteSubtask(Integer id);

    ArrayList<Subtask> getSubtasksOfEpic(Integer id);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpicTask(EpicTask newEpictask);

    void updateEpicTaskStatus(Integer id);

    List<Task> getPrioritizedTasks();
}
