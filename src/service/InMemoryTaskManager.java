package service;

import model.EpicTask;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> allTasks = new HashMap<>();
    protected HashMap<Integer, EpicTask> allEpicTasks = new HashMap<>();
    protected HashMap<Integer, Subtask> allSubtasks = new HashMap<>();
    protected HistoryManager history = Managers.getDefaultHistory();
    protected Integer taskId = 0;

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public Integer getId() {
        return taskId;
    }

    @Override
    public Integer generateId() {
        return ++taskId;
    }

    @Override
    public void createTask(Task task) {
        Integer newId = generateId();
        task.setId(newId);
        allTasks.put(newId, task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        Integer newId = generateId();
        subtask.setId(newId);
        EpicTask epicTask = allEpicTasks.get(subtask.getEpicId());
        epicTask.addSubtask(newId);
        allSubtasks.put(newId, subtask);
        updateEpicTaskStatus(subtask.getEpicId());
    }

    @Override
    public void createEpicTask(EpicTask epictask) {
        Integer newId = generateId();
        epictask.setId(newId);
        allEpicTasks.put(newId, epictask);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(allTasks.values());
    }

    @Override
    public ArrayList<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(allEpicTasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(allSubtasks.values());
    }

    @Override
    public Task getTask(Integer id) {
        Task requestedTask = allTasks.get(id);
        history.add(requestedTask);
        return requestedTask;
    }

    @Override
    public EpicTask getEpicTask(Integer id) {
        EpicTask requestedTask = allEpicTasks.get(id);
        history.add(requestedTask);
        return requestedTask;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask requestedTask = allSubtasks.get(id);
        history.add(requestedTask);
        return requestedTask;
    }

    @Override
    public void deleteAllTasks() {
        for (int id : allTasks.keySet()) {
            history.remove(id);
        }
        allTasks.clear();
    }

    @Override
    public void deleteAllEpicTasks() {
        for (int id : allSubtasks.keySet()) {
            history.remove(id);
        }

        for (int id : allEpicTasks.keySet()) {
            history.remove(id);
        }

        allSubtasks.clear();
        allEpicTasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (EpicTask epictask : allEpicTasks.values()) {
            epictask.deleteSubtasks();
            epictask.setStatus(TaskStatus.NEW);
        }

        for (int id : allSubtasks.keySet()) {
            history.remove(id);
        }

        allSubtasks.clear();
    }

    @Override
    public void deleteTask(Integer id) {
        allTasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteEpicTask(Integer id) {
        for (Integer subtaskId : allEpicTasks.get(id).getSubtasks()) {
            allSubtasks.remove(subtaskId);
            history.remove(subtaskId);
        }
        allEpicTasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteSubtask(Integer id) {
        int epicId = allSubtasks.get(id).getEpicId();
        allEpicTasks.get(epicId).removeLinkedSubtask(id);
        allSubtasks.remove(id);
        history.remove(id);
        updateEpicTaskStatus(epicId);
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(Integer id) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        EpicTask epictask = allEpicTasks.get(id);
        if (epictask != null) {
            for (Integer subtaskId : allEpicTasks.get(id).getSubtasks()) {
                subtasksOfEpic.add(allSubtasks.get(subtaskId));
            }
        }
        return subtasksOfEpic;
    }

    @Override
    public void updateTask(Task task) {
        if (allTasks.containsKey(task.getId())) {
            allTasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (allSubtasks.containsKey(subtask.getId())) {
            Integer epicID = subtask.getEpicId();
            allSubtasks.put(subtask.getId(), subtask);
            updateEpicTaskStatus(epicID);
        }
    }

    @Override
    public void updateEpicTask(EpicTask newEpictask) {
        if (allEpicTasks.containsKey(newEpictask.getId())) {
            EpicTask currEpicTask = allEpicTasks.get(newEpictask.getId());
            currEpicTask.setTitle(newEpictask.getTitle());
            currEpicTask.setDescription(newEpictask.getDescription());
        }
    }

    @Override
    public void updateEpicTaskStatus(Integer id) {
        EpicTask epictask = allEpicTasks.get(id);
        if (epictask != null) {
            ArrayList<Subtask> subtasksOfEpic = getSubtasksOfEpic(id);
            int subtasksOfEpicSize = subtasksOfEpic.size();
            int newSubtaskCounter = 0;
            int doneSubtaskCounter = 0;
            if (subtasksOfEpicSize == 0) {
                epictask.setStatus(TaskStatus.NEW);
            } else {
                for (Subtask subtask : subtasksOfEpic) {
                    if (subtask.getStatus() == TaskStatus.NEW) {
                        newSubtaskCounter++;
                    } else if (subtask.getStatus() == TaskStatus.DONE) {
                        doneSubtaskCounter++;
                    }
                }
                if (subtasksOfEpicSize == newSubtaskCounter) {
                    epictask.setStatus(TaskStatus.NEW);
                } else if (subtasksOfEpicSize == doneSubtaskCounter) {
                    epictask.setStatus(TaskStatus.DONE);
                } else {
                    epictask.setStatus(TaskStatus.IN_PROGRESS);
                }
            }
        }
    }

}