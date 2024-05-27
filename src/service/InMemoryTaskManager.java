package service;

import exceptions.InvalidReceivedTimeException;
import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected Integer taskId = 0;
    protected Map<Integer, Task> allTasks = new HashMap<>();
    protected Map<Integer, EpicTask> allEpicTasks = new HashMap<>();
    protected Map<Integer, Subtask> allSubtasks = new HashMap<>();
    protected HistoryManager history = Managers.getDefaultHistory();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
        if (isTimeIntervalBooked(task, getPrioritizedTasks())) {
            throw new InvalidReceivedTimeException("Данное время занято другой задачей.");
        }
        Integer newId = generateId();
        task.setId(newId);
        allTasks.put(newId, task);
        addTaskToPrioritizedTasks(task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (isTimeIntervalBooked(subtask, getPrioritizedTasks())) {
            throw new InvalidReceivedTimeException("Данное время занято другой задачей.");
        }
        Integer newId = generateId();
        subtask.setId(newId);
        EpicTask epicOwner = allEpicTasks.get(subtask.getEpicId());
        addTaskToPrioritizedTasks(subtask);
        epicOwner.addSubtask(newId);
        allSubtasks.put(newId, subtask);
        updateEpicAttributes(epicOwner.getId());
    }

    @Override
    public void createEpicTask(EpicTask epictask) {
        Integer newId = generateId();
        epictask.setId(newId);
        allEpicTasks.put(newId, epictask);
        updateEpicAttributes(newId);
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
        Optional<Task> requestedTask = Optional.ofNullable(allTasks.get(id));
        requestedTask.ifPresent(history::add);
        return requestedTask.orElseThrow(() -> new NoSuchElementException("Задача (Task, id: " + id + ") не найдена."));
    }

    @Override
    public EpicTask getEpicTask(Integer id) {
        Optional<EpicTask> requestedTask = Optional.ofNullable(allEpicTasks.get(id));
        requestedTask.ifPresent(history::add);
        return requestedTask.orElseThrow(() -> new NoSuchElementException("Задача (EpicTask, id: " + id + ") не найдена."));
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Optional<Subtask> requestedTask = Optional.ofNullable(allSubtasks.get(id));
        requestedTask.ifPresent(history::add);
        return requestedTask.orElseThrow(() -> new NoSuchElementException("Задача (Subtask, id: " + id + ") не найдена."));
    }

    @Override
    public void deleteAllTasks() {
        allTasks.keySet().stream()
                .peek(history::remove)
                .map(allTasks::get)
                .forEach(prioritizedTasks::remove);

        allTasks.clear();
    }

    @Override
    public void deleteAllEpicTasks() {
        allSubtasks.keySet().stream()
                .peek(history::remove)
                .map(allSubtasks::get)
                .forEach(prioritizedTasks::remove);

        allEpicTasks.keySet().forEach(history::remove);
        allSubtasks.clear();
        allEpicTasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        allEpicTasks.values().stream()
                .peek(EpicTask::deleteSubtasks)
                .map(EpicTask::getId)
                .forEach(this::updateEpicAttributes);

        allSubtasks.keySet().stream()
                .peek(history::remove)
                .map(allSubtasks::get)
                .forEach(prioritizedTasks::remove);

        allSubtasks.clear();
    }

    @Override
    public void deleteTask(Integer id) {
        final Task tempTask = allTasks.get(id);
        allTasks.remove(id);
        history.remove(id);
        prioritizedTasks.remove(tempTask);
    }

    @Override
    public void deleteEpicTask(Integer id) {
        final EpicTask tempEpic = allEpicTasks.get(id);
        history.remove(id);

        tempEpic.getSubtasks().stream()
                .peek(history::remove)
                .map(allSubtasks::remove)
                .forEach(prioritizedTasks::remove);

        allEpicTasks.remove(id);
    }

    @Override
    public void deleteSubtask(Integer id) {
        final Subtask tempSub = allSubtasks.get(id);
        final EpicTask EpicOwner = allEpicTasks.get(tempSub.getEpicId());
        prioritizedTasks.remove(tempSub);
        history.remove(id);
        EpicOwner.removeLinkedSubtask(id);
        updateEpicAttributes(EpicOwner.getId());
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(Integer id) {
        return (ArrayList<Subtask>) allEpicTasks.get(id).getSubtasks().stream()
                .map(allSubtasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public void updateTask(Task task) {
        if (isTimeIntervalBooked(task, getPrioritizedTasks())) {
            throw new InvalidReceivedTimeException("Данное время занято.");
        }
        if (allTasks.containsKey(task.getId())) {
            prioritizedTasks.remove(allTasks.get(task.getId()));
            allTasks.put(task.getId(), task);
            addTaskToPrioritizedTasks(task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (isTimeIntervalBooked(subtask, getPrioritizedTasks())) {
            throw new InvalidReceivedTimeException("Данное время занято.");
        }
        if (allSubtasks.containsKey(subtask.getId())) {
            Integer epicId = subtask.getEpicId();
            allSubtasks.put(subtask.getId(), subtask);
            updateEpicTaskStatus(epicId);
            prioritizedTasks.remove(allSubtasks.get(subtask.getId()));
            addTaskToPrioritizedTasks(subtask);
            updateEpicAttributes(epicId);
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

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isPossibleToPrioritizeByTime(Task task) {
        return task != null && task.getStartTime() != null;
    }

    protected void addTaskToPrioritizedTasks(Task task) {
        if (isPossibleToPrioritizeByTime(task)) {
            prioritizedTasks.add(task);
        }
    }

    private void updateEpicAttributes(int id) {
        final EpicTask epic = allEpicTasks.get(id);
        final List<Integer> idList = epic.getSubtasks();
        LocalDateTime earliestStartTime;
        LocalDateTime latestEndTime;

            List<Task> timeFrontiers = prioritizedTasks.stream()
                .filter(Task -> idList.contains(Task.getId()))
                .toList();

        if (timeFrontiers.isEmpty()) {
            earliestStartTime = null;
            latestEndTime = null;
            epic.setDuration(null);
        } else {
            earliestStartTime = timeFrontiers.getFirst().getStartTime();
            latestEndTime = timeFrontiers.getLast().getEndTime();
            epic.setDuration(Duration.between(earliestStartTime, latestEndTime));
        }

        epic.setStartTime(earliestStartTime);
        epic.setEndTime(latestEndTime);
        updateEpicTaskStatus(id);
    }

    private boolean isTimeIntervalBooked(Task newTask, List<Task> existedTasks) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }
        return existedTasks.stream().anyMatch(existedTask -> checkTimeIntersection(newTask, existedTask));
    }

    private boolean checkTimeIntersection(Task newTask, Task existedTask) {
        return newTask.getStartTime().isBefore(existedTask.getEndTime()) &&
                newTask.getEndTime().isAfter(existedTask.getStartTime());
    }

}