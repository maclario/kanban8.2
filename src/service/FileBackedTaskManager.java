package service;

import exceptions.LoadingFromFileException;
import exceptions.SavingToFileException;
import mappers.CSVFormatter;
import model.EpicTask;
import model.Subtask;
import model.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File backupFile;

    public FileBackedTaskManager(File backupFile) {
        this.backupFile = backupFile;
    }

    private void save() {
        try (BufferedWriter buffwriter = new BufferedWriter(new FileWriter(backupFile))) {
            buffwriter.write(CSVFormatter.getHeader());
            buffwriter.newLine();

            for (final Task task : getAllTasks()) {
                buffwriter.write(CSVFormatter.taskToString(task));
                buffwriter.newLine();
            }

            for (final Task task : getAllEpicTasks()) {
                buffwriter.write(CSVFormatter.taskToString(task));
                buffwriter.newLine();
            }

            for (final Task task : getAllSubtasks()) {
                buffwriter.write(CSVFormatter.taskToString(task));
                buffwriter.newLine();
            }

        } catch (IOException e) {
            throw new SavingToFileException("Ошибка записи/сохранения задач в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        try {
            final String fullFile = Files.readString(file.toPath());
            final String[] lines = fullFile.split(System.lineSeparator());
            int lastId = -1;
            int lineNum = 1;

            for (int i = lineNum; lineNum < lines.length; lineNum++) {
                String currLine = lines[lineNum];
                if (currLine.isBlank()) {
                    break;
                }
                Task task = CSVFormatter.stringToTask(currLine);
                taskManager.defineTypeAndAddToRelevantStorage(task);
                if (lastId < task.getId()) {
                    lastId = task.getId();
                }
            }

            for (Map.Entry<Integer, Subtask> entry : taskManager.allSubtasks.entrySet()) {
                final Subtask subtask = entry.getValue();
                final EpicTask epic = taskManager.allEpicTasks.get(subtask.getEpicId());
                epic.addSubtask(subtask.getId());
            }

            taskManager.taskId = lastId;

        } catch (IOException e) {
            throw new LoadingFromFileException("Ошибка чтения/загрузки из файла.", e);
        }
        return taskManager;
    }

    private void defineTypeAndAddToRelevantStorage(Task task) {
        switch (task.getType()) {
            case EPIC -> allEpicTasks.put(task.getId(), (EpicTask) task);
            case SUBTASK -> allSubtasks.put(task.getId(), (Subtask) task);
            case TASK -> allTasks.put(task.getId(), task);
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpicTask(EpicTask epictask) {
        super.createEpicTask(epictask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpicTasks() {
        super.deleteAllEpicTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteTask(Integer id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpicTask(Integer id) {
        super.deleteEpicTask(id);
        save();
    }

    @Override
    public void deleteSubtask(Integer id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpicTask(EpicTask epic) {
        super.updateEpicTask(epic);
        save();
    }

}
