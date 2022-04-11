package nl.han.ica.datastructures;

public class HANStack<Type> implements IHANStack<Type> {

    IHANLinkedList<Type> linkedList = new HANLinkedlist<>();

    @Override
    public void push(Type value) {
        linkedList.addFirst(value);
    }

    @Override
    public Type pop() {
        Type first = linkedList.getFirst();
        linkedList.delete(0);
        return first;
    }

    @Override
    public Type peek() {
        return linkedList.getFirst();
    }
}

