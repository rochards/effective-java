package br.rochards.item7;

import java.util.Arrays;
import java.util.EmptyStackException;

public class Stack {
    private Object[] elements;
    private int nextFreePosition = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[nextFreePosition++] = e;
    }

    public Object pop() {
        if (nextFreePosition == 0)
            throw new EmptyStackException();
        return elements[--nextFreePosition];
    }

    private void ensureCapacity() {
        if (elements.length == nextFreePosition)
            elements = Arrays.copyOf(elements, 2 * nextFreePosition + 1);
    }
}
