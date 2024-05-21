import model.*;
import service.Managers;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Забрать подарок", "ПВ Юбилейный, к оплате 2600 руб");
        manager.createTask(task1);
        Task task2 = new Task("Ретро по спринту", "11:00 Ссылка на зум-встречу");
        manager.createTask(task2);

        EpicTask epic3 = new EpicTask("Форматирование отчета", "Отчет по А/Б-тесту");
        manager.createEpicTask(epic3);
        Subtask sub4 = new Subtask("Отформатировать заголовки", "Roboto 18 bold", 3);
        manager.createSubtask(sub4);
        Subtask sub5 = new Subtask("Отформатировать текст", "Roboto 13 regular", 3);
        manager.createSubtask(sub5);

        EpicTask epic6 = new EpicTask("Визит к неврологу", "18:00 ул. Гудкова, 3а, каб 10");
        manager.createEpicTask(epic6);
        Subtask sub7 = new Subtask("Забрать результаты рентгена", "Кабинет №14", 6);
        manager.createSubtask(sub7);

        System.out.println("Получим все задачи, затем все эпики, затем все подзадачи");
        System.out.println("Задачи " + manager.getAllTasks());
        System.out.println("Эпики " + manager.getAllEpicTasks());
        System.out.println("Подзадачи " + manager.getAllSubtasks());

        System.out.println("\nПолучим подзадачи эпика 3");
        System.out.println("Подзадачи эпика " + epic3.getId() + ": " + epic3.getSubtasks());
        System.out.println("Статус эпика " + epic3.getStatus());

        System.out.println("\nОбновим задачу 1");
        task1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task1);

        System.out.println("\nОбновим подзадачи эпика 3");
        System.out.println("Тестовые варианты DONE + PROGRESS, DONE + DONE");
        sub4.setStatus(TaskStatus.DONE);
        manager.updateSubtask(sub4);
        sub5.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(sub5);
        System.out.println("Подзадачи эпика " + epic3.getId() + ": " + epic3.getSubtasks());
        System.out.println("Статус эпика " + epic3.getStatus());
        sub4.setStatus(TaskStatus.DONE);
        manager.updateSubtask(sub4);
        sub5.setStatus(TaskStatus.DONE);
        manager.updateSubtask(sub5);
        System.out.println("Подзадачи эпика " + epic3.getId() + ": " + epic3.getSubtasks());
        System.out.println("Статус эпика " + epic3.getStatus());

        System.out.println("\nДобавим в эпик 3 новую подзадачу и проверим изменения");
        Subtask sub8 = new Subtask("Открыть доступ к документу", "В настройках", 3);
        manager.createSubtask(sub8);
        System.out.println("Подзадачи эпика " + epic3.getId() + ": " + epic3.getSubtasks());
        System.out.println("Статус эпика " + epic3.getStatus());

        System.out.println("\nОбновим единственную подзадачу эпика 6 на DONE");
        sub7.setStatus(TaskStatus.DONE);
        manager.updateSubtask(sub7);
        System.out.println("Подзадачи эпика " + epic6.getId() + ": " + epic6.getSubtasks());
        System.out.println("Статус эпика " + epic6.getStatus());

        System.out.println("\nОбновим единственную подзадачу эпика 6 на IN_PROGRESS");
        sub7.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(sub7);
        System.out.println("Подзадачи эпика " + epic6.getId() + ": " + epic6.getSubtasks());
        System.out.println("Теперь проверим статус самого эпика");
        System.out.println("Статус эпика " + epic6.getStatus());

        task1.setStatus(TaskStatus.NEW);
        manager.getTask(2);
        manager.getTask(2);
        manager.getEpicTask(3);
        manager.getEpicTask(6);
        manager.getSubtask(4);
        task1.setStatus(TaskStatus.IN_PROGRESS);
        manager.getTask(1);

        System.out.println("\nИстория просмотров:");
        System.out.println(manager.getHistory());

        System.out.println("\nТеперь удалим единственную подзадачу эпика 6 и проверим статус");
        manager.deleteSubtask(7);
        System.out.println("Подзадачи эпика " + epic6.getId() + ": " + epic6.getSubtasks());
        for (Integer id : epic6.getSubtasks()) {
            System.out.println(manager.getSubtask(id));
        }
        System.out.println("Статус эпика " + epic6.getStatus());

        System.out.println("\nУдалим задачу 1 и эпик 6, затем получим задачи всех типов");
        manager.deleteTask(1);
        manager.deleteEpicTask(6);
        System.out.println("Задачи " + manager.getAllTasks());
        System.out.println("Эпики " + manager.getAllEpicTasks());
        System.out.println("Подзадачи " + manager.getAllSubtasks());

        System.out.println("\nУдалим все эпики и проверим удалились ли подзадачи");
        manager.deleteAllEpicTasks();
        System.out.println("Задачи " + manager.getAllTasks());
        System.out.println("Эпики " + manager.getAllEpicTasks());
        System.out.println("Подзадачи " + manager.getAllSubtasks());

        System.out.println("\nУдалим все стандартные задачи");
        manager.deleteAllTasks();
        System.out.println("Задачи " + manager.getAllTasks());
        System.out.println("Эпики " + manager.getAllEpicTasks());
        System.out.println("Подзадачи " + manager.getAllSubtasks());

    }
}