package ua.tmmaple.pr25;

import ua.tmmaple.pr25.util.PR25RuntimeException;

/**
 * Керує списками оновлення та відмалювання.
 * Максимум може містити по 32 системи для кожного списку, тож системи мають внутрішньо оновлювати власні об'єкти, якщо їх більше.
 * @author uwuhasmile
 */
public final class Flow {
    public static final int FLOW_RESULT_CONTINUE = 0;
    public static final int FLOW_RESULT_CONTINUE_CUT = 1;
    public static final int FLOW_RESULT_REPEAT = 2;
    public static final int FLOW_RESULT_RESTART = 3;
    public static final int FLOW_RESULT_BREAK = 4;
    public static final int FLOW_RESULT_EXIT = 5;
    public static final int FLOW_RESULT_FATAL = 6;

    public static Flow global;

    private final FlowNode<?>[] toUpdate;
    private int toUpdateSize;
    private final FlowNode<?>[] toDraw;
    private int toDrawSize;

    public Flow() {
        toUpdate = new FlowNode[32];
        toUpdateSize = 0;
        toDraw = new FlowNode[32];
        toDrawSize = 0;
    }

    /**
     * Додає вузол до списку оновлення.
     * @param priority приоритет, впливає на позицію вузла в списку
     * @author uwuhasmile
     */
    public int addToUpdate(FlowNode<?> node, int priority) {
        if (toUpdateSize == toUpdate.length) throw new PR25RuntimeException("Flow update list is full");
        node.priority = priority;

        int i = 0;
        FlowNode<?> current = null;
        while (i < toUpdateSize && (current = toUpdate[i]).priority <= priority) { ++i; }

        if (current != null && current.priority > priority) ++i;
        if (i == toUpdateSize)
            toUpdate[toUpdateSize++] = node;
        else {
            for (int j = toUpdateSize++; j > i; --j)
                toUpdate[j] = toUpdate[j - 1];
            toUpdate[i] = node;
        }

        return node.added();
    }

    /**
     * Додає вузол до списку відмалювання.
     * @param priority приоритет, впливає на позицію вузла в списку
     * @author uwuhasmile
     */
    public int addToDraw(FlowNode<?> node, int priority) {
        if (toDrawSize == toDraw.length) throw new PR25RuntimeException("Flow draw list is full");
        node.priority = priority;

        int i = 0;
        FlowNode<?> current = null;
        while (i < toDrawSize && (current = toDraw[i]).priority <= priority) { ++i; }

        if (current != null && current.priority > priority) ++i;
        if (i == toDrawSize)
            toDraw[toDrawSize++] = node;
        else {
            for (int j = toDrawSize++; j > i; --j)
                toDraw[j] = toDraw[j - 1];
            toDraw[i] = node;
        }

        return node.added();
    }

    /**
     * Видаляє вузол зі списків.
     * @author uwuhasmile
     */
    public int cut(FlowNode<?> node) {
        int type = 0;
        int i = 0;
        while (i < toUpdateSize && toUpdate[i] != node) { ++i; }
        if (i < toUpdateSize) type = 1;
        else {
            i = 0;
            while (i < toDrawSize && toDraw[i] != node) { ++i; }
            if (i < toDrawSize) type = 2;
        }

        if (type == 1) {
            --toUpdateSize;
            while (i < toUpdateSize) { toUpdate[i] = toUpdate[++i]; }
        } else if (type == 2) {
            --toDrawSize;
            while (i < toDrawSize) { toDraw[i] = toDraw[++i]; }
        } else throw new PR25RuntimeException("Node doesn't exist in update or draw list");

        return node.removed();
    }

    /**
     * Оброблює всі вузли в списку оновлення
     * @author uwuhasmile
     */
    public int executeUpdate() {
        execution:
        for (int i = 0; i < toUpdateSize; ++i) {
            int result = FLOW_RESULT_REPEAT;
            while (result == FLOW_RESULT_REPEAT) {
                FlowNode<?> current = toUpdate[i];
                result = current.call();
                switch (result) {
                    case FLOW_RESULT_CONTINUE_CUT: {
                        cut(current);
                        --i;
                    } break;
                    case FLOW_RESULT_REPEAT: continue;
                    case FLOW_RESULT_RESTART: {
                        i = -1;
                        continue execution;
                    }
                    case FLOW_RESULT_BREAK: return 2;
                    case FLOW_RESULT_EXIT: return 1;
                    case FLOW_RESULT_FATAL: return -1;
                    default: break;
                }
            }
        }
        return 0;
    }

    /**
     * Оброблює всі вузли в списку відмалювання.
     * @author uwuhasmile
     */
    public int executeDraw() {
        execution:
        for (int i = 0; i < toDrawSize; ++i) {
            int result = FLOW_RESULT_REPEAT;
            while (result == FLOW_RESULT_REPEAT) {
                FlowNode<?> current = toDraw[i];
                result = current.call();
                switch (result) {
                    case FLOW_RESULT_CONTINUE_CUT: {
                        cut(current);
                        --i;
                    } break;
                    case FLOW_RESULT_REPEAT: continue;
                    case FLOW_RESULT_RESTART: {
                        i = -1;
                        continue execution;
                    }
                    case FLOW_RESULT_BREAK: return 2;
                    case FLOW_RESULT_EXIT: return 1;
                    case FLOW_RESULT_FATAL: return -1;
                    default: break;
                }
            }
        }
        return 0;
    }

    /**
     * Очищає список оновлення.
     * @author uwuhasmile
     */
    public void shutdown() {
        for (int i = 0; i < toUpdateSize; ++i)
            toUpdate[i].removed();
        toUpdateSize = 0;
        for (int i = 0; i < toDrawSize; ++i)
            toDraw[i].removed();
        toDrawSize = 0;
    }

    public interface FlowListener<T> {
        int call(T item);
    }

    /**
     * Вузол списку оновлень.
     * @param <T> Тип об'єкту вузла
     * @author uwuhasmile
     */
    public static final class FlowNode<T> {
        private int priority;
        public T ref;
        public FlowListener<T> listener;
        public FlowListener<T> addedListener;
        public FlowListener<T> removedListener;

        public FlowNode() {
            this(null, null, null, null);
        }

        public FlowNode(FlowListener<T> listener) {
            this(null, listener, null, null);
        }

        public FlowNode(FlowListener<T> listener, FlowListener<T> addedListener) {
            this(null, listener, addedListener, null);
        }

        public FlowNode(FlowListener<T> listener, FlowListener<T> addedListener, FlowListener<T> removedListener) {
            this(null, listener, addedListener, removedListener);
        }

        public FlowNode(T ref) {
            this(ref, null, null, null);
        }

        public FlowNode(T ref, FlowListener<T> listener) {
            this(ref, listener, null, null);
        }

        public FlowNode(T ref, FlowListener<T> listener, FlowListener<T> addedListener) {
            this(ref, listener, addedListener, null);
        }

        public FlowNode(T ref, FlowListener<T> listener, FlowListener<T> addedListener, FlowListener<T> removedListener) {
            this.ref = ref;
            this.listener = listener;
            this.addedListener = addedListener;
            this.removedListener = removedListener;
        }

        /**
         * Оновлює вузол.
         * @author uwuhasmile
         */
        private int call() {
            if (listener != null) return listener.call(ref);
            return 0;
        }

        /**
         * Викликає метод при додаванні вузла до списку
         * @author uwuhasmile
         */
        private int added() {
            int value = 0;
            if (addedListener != null) {
                value = addedListener.call(ref);
                addedListener = null;
            }
            return value;
        }

        /**
         * Викликає метод при видаленні вузла зі списку
         * @author uwuhasmile
         */
        private int removed() {
            int value = 0;
            if (removedListener != null) {
                value = removedListener.call(ref);
                removedListener = null;
            }
            return value;
        }
    }
}
