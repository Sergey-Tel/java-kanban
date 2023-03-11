package dev.service.history;

import dev.domain.TaskBase;
import dev.service.history.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<TaskBase> history;

    public InMemoryHistoryManager() {
        history = new CustomLinkedList<>();
    }


    @Override
    public void add(TaskBase task) {
        history.add(task);
    }


    @Override
    public void remove(int id) {
        history.remove(id);
    }

    @Override
    public TaskBase getFirst() {
        return history.getFirst();
    }

    @Override
    public TaskBase getLast() {
        return history.getLast();
    }


    @Override
    public List<TaskBase> getHistory() {
        return history.getTasks();
    }

    @Override
    public List<Integer> getHistoryId() {
        return history.getTasksId();
    }

    @Override
    public void clear() {
        history.clear();
    }

    static class CustomLinkedList<E extends TaskBase> {
        private final Map<Integer, Node<E>> nodesMap = new HashMap<>();
        private Node<E> first;
        private Node<E> last;

        public E getFirst() {
            return first.item;
        }

        public E getLast() {
            return last.item;
        }

        public void add(E task) {
            remove(task.getTaskId());
            linkLast(task);
            nodesMap.put(task.getTaskId(), last);
        }


        private void linkLast(E element) {
            final Node<E> last1 = last;
            final Node<E> newNode = new Node<>(last1, element, null);
            last = newNode;
            if (last1 == null)
                first = newNode;
            else
                last1.next = newNode;
        }

        public void remove(int id) {
            final Node<E> removedNode = nodesMap.remove(id);
            if (removedNode != null) {
                removeNode(removedNode);
            }
        }

        private void removeNode(Node<E> node) {
            final Node<E> next = node.next;
            final Node<E> prev = node.prev;

            if (prev == null) {
                first = next;
            } else {
                prev.next = next;
                node.prev = null;
            }

            if (next == null) {
                last = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
            node.item = null;
        }

        public void clear() {
            nodesMap.clear();
            for (Node<E> first1 = first; first1 != null; ) {
                Node<E> next = first1.next;
                first1.item = null;
                first1.next = null;
                first1.prev = null;
                first1 = next;
            }
            first = last = null;
        }


        public List<E> getTasks() {
            List<E> result = new ArrayList<>();
            for (Node<E> first1 = first; first1 != null; first1 = first1.next) {
                result.add(first1.item);
            }
            return result;
        }

        public List<Integer> getTasksId() {
            List<Integer> result = new ArrayList<>();
            for (Node<E> first1 = first; first1 != null; first1 = first1.next) {
                result.add(first1.item.getTaskId());
            }
            return result;
        }

        private static class Node<E extends TaskBase> {
            E item;
            Node<E> next;
            Node<E> prev;

            Node(Node<E> prev, E element, Node<E> next) {
                this.item = element;
                this.next = next;
                this.prev = prev;
            }
        }
    }
}
