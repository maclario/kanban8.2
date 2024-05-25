package service;

import exceptions.InvalidReceivedTimeException;
import model.EpicTask;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import util.TimeCalculator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected Integer taskId = 0;
    protected HashMap<Integer, Task> allTasks = new HashMap<>();
    protected HashMap<Integer, EpicTask> allEpicTasks = new HashMap<>();
    protected HashMap<Integer, Subtask> allSubtasks = new HashMap<>();
    protected HistoryManager history = Managers.getDefaultHistory();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected final static int TIME_SLOT_MINUTES = 15;
    protected Map<LocalDateTime, Boolean> timeSlots; // Занатые слоты: true; Свободные слоты: false.

    public InMemoryTaskManager() {
        timeSlots = new HashMap<>();
        LocalDateTime startTime = LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0);
        LocalDateTime endTime = startTime.plusMonths(12);
        while (startTime.isBefore(endTime)) {
            timeSlots.put(startTime, false);
            startTime = startTime.plusMinutes(TIME_SLOT_MINUTES);
        }
    }

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

    @Override // Готово
    public void createTask(Task task) {
        if (isTimeIntervalBooked(task.getStartTime(), task.getEndTime())) {
            throw new InvalidReceivedTimeException("Данное время занято другой задачей.");
        }
        Integer newId = generateId();
        task.setId(newId);
        allTasks.put(newId, task);
        addTaskToPrioritizedTasks(task);
    }

    @Override // Готово
    public void createSubtask(Subtask subtask) {
        if (isTimeIntervalBooked(subtask.getStartTime(), subtask.getEndTime())) {
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

    @Override // Готово
    public void createEpicTask(EpicTask epictask) {
        Integer newId = generateId();
        epictask.setId(newId);
        allEpicTasks.put(newId, epictask);
        updateEpicAttributes(newId);
    }

    @Override // Готово
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(allTasks.values());
    }

    @Override // Готово
    public ArrayList<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(allEpicTasks.values());
    }

    @Override // Готово
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(allSubtasks.values());
    }

    @Override // Готово
    public Task getTask(Integer id) {
        Optional<Task> requestedTask = Optional.ofNullable(allTasks.get(id));
        requestedTask.ifPresent(history::add);
        return requestedTask.orElseThrow(() -> new NoSuchElementException("Задача (Task, id: " + id + ") не найдена."));
    }

    @Override // Готово
    public EpicTask getEpicTask(Integer id) {
        Optional<EpicTask> requestedTask = Optional.ofNullable(allEpicTasks.get(id));
        requestedTask.ifPresent(history::add);
        return requestedTask.orElseThrow(() -> new NoSuchElementException("Задача (EpicTask, id: " + id + ") не найдена."));
    }

    @Override // Готово
    public Subtask getSubtask(Integer id) {
        Optional<Subtask> requestedTask = Optional.ofNullable(allSubtasks.get(id));
        requestedTask.ifPresent(history::add);
        return requestedTask.orElseThrow(() -> new NoSuchElementException("Задача (Subtask, id: " + id + ") не найдена."));
    }

    @Override // Готово
    public void deleteAllTasks() {
        allTasks.keySet().stream()
                .peek(history::remove)
                .map(allTasks::get)
                .peek(task -> freeTimeInTimeSlots(task.getStartTime(), task.getEndTime()))
                .forEach(prioritizedTasks::remove);

        allTasks.clear();
    }

    @Override // Готово
    public void deleteAllEpicTasks() {
        allSubtasks.keySet().stream()
                .peek(history::remove)
                .map(allSubtasks::get)
                .peek(subtask -> freeTimeInTimeSlots(subtask.getStartTime(), subtask.getEndTime()))
                .forEach(prioritizedTasks::remove);

        allEpicTasks.keySet().forEach(history::remove);
        allSubtasks.clear();
        allEpicTasks.clear();
    }

    @Override // Готово
    public void deleteAllSubtasks() {
        allEpicTasks.values().stream()
                .peek(EpicTask::deleteSubtasks)
                .map(EpicTask::getId)
                .forEach(this::updateEpicAttributes);

        allSubtasks.keySet().stream()
                .peek(history::remove)
                .map(allSubtasks::get)
                .peek(subtask -> freeTimeInTimeSlots(subtask.getStartTime(), subtask.getEndTime()))
                .forEach(prioritizedTasks::remove);

        allSubtasks.clear();
    }

    @Override  // Готово
    public void deleteTask(Integer id) {
        final Task tempTask = allTasks.get(id);
        allTasks.remove(id);
        history.remove(id);
        prioritizedTasks.remove(tempTask);
        freeTimeInTimeSlots(tempTask.getStartTime(), tempTask.getEndTime());
    }

    @Override // Готово
    public void deleteEpicTask(Integer id) {
        final EpicTask tempEpic = allEpicTasks.get(id);
        history.remove(id);

        tempEpic.getSubtasks().stream()
                .peek(history::remove)
                .map(allSubtasks::remove)
                .peek(subtask -> freeTimeInTimeSlots(subtask.getStartTime(), subtask.getEndTime()))
                .forEach(prioritizedTasks::remove);

        allEpicTasks.remove(id);
    }

    @Override // Готово
    public void deleteSubtask(Integer id) {
        final Subtask tempSub = allSubtasks.get(id);
        final EpicTask EpicOwner = allEpicTasks.get(tempSub.getEpicId());
        prioritizedTasks.remove(tempSub);
        freeTimeInTimeSlots(tempSub.getStartTime(), tempSub.getEndTime());
        history.remove(id);
        EpicOwner.removeLinkedSubtask(id);
        updateEpicAttributes(EpicOwner.getId());
    }

    @Override // Готово
    public ArrayList<Subtask> getSubtasksOfEpic(Integer id) {
        return (ArrayList<Subtask>) allEpicTasks.get(id).getSubtasks().stream()
                .map(allSubtasks::get)
                .collect(Collectors.toList());
    }

    @Override // Готово
    public void updateTask(Task task) {
        if (isTimeIntervalBooked(task.getStartTime(), task.getEndTime())) {
            throw new InvalidReceivedTimeException("Данное время занято.");
        }
        if (allTasks.containsKey(task.getId())) {
            allTasks.put(task.getId(), task);
            addTaskToPrioritizedTasks(task);
        }
    }

    @Override // Готово
    public void updateSubtask(Subtask subtask) {
        if (isTimeIntervalBooked(subtask.getStartTime(), subtask.getEndTime())) {
            throw new InvalidReceivedTimeException("Данное время занято.");
        }
        if (allSubtasks.containsKey(subtask.getId())) {
            Integer epicId = subtask.getEpicId();
            allSubtasks.put(subtask.getId(), subtask);
            updateEpicTaskStatus(epicId);
            addTaskToPrioritizedTasks(subtask);
            updateEpicAttributes(epicId);
        }
    }

    @Override // Готово
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

    @Override // Готово
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    // Готово
    private boolean isPossibleToPrioritizeByTime(Task task) {
        return task != null && task.getStartTime() != null;
    }

    // Готово
    private void addTaskToPrioritizedTasks(Task task) {
        if (isPossibleToPrioritizeByTime(task)) {
            prioritizedTasks.add(task);
            bookTimeInTimeSlots(task.getStartTime(), task.getEndTime());
        }
    }

    // Готово
    private void updateEpicAttributes(int id){
        final EpicTask epic = allEpicTasks.get(id);
        final List<Integer> idList = epic.getSubtasks();
        final HashMap<Integer, Subtask> AllSubs = allSubtasks;

        final Duration duration = TimeCalculator.findDurationSum(idList, AllSubs);
        final LocalDateTime startTime = TimeCalculator.findEarliestStartTime(idList, AllSubs);
        final LocalDateTime endTime = TimeCalculator.findLatestEndTime(idList, AllSubs);

        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
        updateEpicTaskStatus(id);
    }

    private void bookTimeInTimeSlots(LocalDateTime start, LocalDateTime end) {
        LocalDateTime currDateTime = start;
        while (currDateTime.isBefore(end)) {
            timeSlots.put(currDateTime, true);
            currDateTime = currDateTime.plusMinutes(TIME_SLOT_MINUTES);
        }
    }

    private void freeTimeInTimeSlots(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return;
        }
        LocalDateTime currDateTime = start;
        while (currDateTime.isBefore(end)) {
            timeSlots.put(currDateTime, false);
            currDateTime = currDateTime.plusMinutes(TIME_SLOT_MINUTES);
        }
    }

    private boolean isTimeIntervalBooked(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return false;
        }
        LocalDateTime currDateTime = start;
        while (currDateTime.isBefore(end)) {
            if (!timeSlots.containsKey(currDateTime) || timeSlots.get(currDateTime)) {
                return true;
            }
            currDateTime = currDateTime.plusMinutes(TIME_SLOT_MINUTES);
        }
        return false;
    }

}