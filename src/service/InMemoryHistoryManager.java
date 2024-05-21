package service;

import model.Task;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    private void linkLast(Task task) {
        Node newNode = new Node(tail, task, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            int id = task.getId();
            Task savedTask = new Task(task.getTitle(), task.getDescription());
            savedTask.setId(id);
            removeNode(historyMap.get(id));
            linkLast(savedTask);
            historyMap.put(id, tail);
        }
    }

    private void removeNode(Node node) {
        if (node != null) {
            final Node prevNode = node.prev;
            final Node nextNode = node.next;
            if (node == head && node == tail) {
                head = null;
                tail = null;
            } else if (node == head) {
                head = nextNode;
            } else if (node == tail) {
                tail = prevNode;
                prevNode.next = null;
            } else {
                prevNode.next = node.next;
                nextNode.prev = node.prev;
            }
        }
    }

    @Override
    public void remove(int id) {
        removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        Node currNode = head;
        while (currNode != null) {
            historyList.add(currNode.data);
            currNode = currNode.next;
        }
        return historyList;
    }

    private static class Node {
        Task data;
        Node prev;
        Node next;

        Node(Node prev, Task data, Node next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }

}
