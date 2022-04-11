package nl.han.ica.datastructures;

import java.util.LinkedList;

public class HANLinkedlist<Type> implements IHANLinkedList<Type> {

    LinkedList<Type> linkedList = new LinkedList<>();


    @Override
    public void addFirst(Type value) {
        linkedList.addFirst(value);
    }

    @Override
    public void clear() {
        linkedList.clear();
    }

    @Override
    public void insert(int index, Type value) {
        linkedList.add(index, value);
    }

    @Override
    public void delete(int pos) {
        linkedList.remove(pos);
    }

    @Override
    public Type get(int pos) {
        return linkedList.get(pos);
    }

    @Override
    public void removeFirst() {
        linkedList.removeFirst();
    }

    @Override
    public Type getFirst() {
        return linkedList.getFirst();
    }

    @Override
    public int getSize() {
        return linkedList.size();
    }
}

