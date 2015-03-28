package uk.co.alynn.games.suchrobot;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class ArraySet<E> extends AbstractSet<E> {
    private final ArrayList<E> elements;
    private final Comparator<E> compar;

    private boolean readReady = true;
    private static final int DEFAULT_CAPACITY = 32;

    private final static Comparator<Object> DEFAULT_COMPARATOR = new Comparator<Object>() {

        @Override
        public int compare(Object o1, Object o2) {
            int id1 = System.identityHashCode(o1);
            int id2 = System.identityHashCode(o2);
            if (id1 < id2) {
                return -1;
            } else if (id1 > id2) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    public ArraySet(int capacity, Comparator<E> compar) {
        this.compar = compar;
        this.elements = new ArrayList<E>(capacity);
    }

    @SuppressWarnings("unchecked")
    public ArraySet(int capacity) {
        this(capacity, (Comparator<E>) DEFAULT_COMPARATOR);
    }

    @SuppressWarnings("unchecked")
    public ArraySet() {
        this(DEFAULT_CAPACITY, (Comparator<E>) DEFAULT_COMPARATOR);
    }

    private void makeReadReady() {
        if (!readReady) {
            elements.sort(compar);
            E previous = null;
            Iterator<E> iter = elements.iterator();
            while (iter.hasNext()) {
                E element = iter.next();
                if (element.equals(previous)) {
                    iter.remove();
                }
                previous = element;
            }
            readReady = true;
        }
    }

    @Override
    public Iterator<E> iterator() {
        makeReadReady();
        return elements.iterator();
    }

    @Override
    public int size() {
        makeReadReady();
        return elements.size();
    }

    @Override
    public boolean add(E element) {
        readReady = false;
        return elements.add(element);
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        makeReadReady();
        @SuppressWarnings("unchecked")
        int index = Collections.binarySearch(elements, (E)o, compar);
        return index >= 0;
    }

    @Override
    public void clear() {
        elements.clear();
        readReady = true;
    }
}
