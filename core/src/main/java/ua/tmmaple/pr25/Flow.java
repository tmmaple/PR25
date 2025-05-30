package ua.tmmaple.pr25;

import ua.tmmaple.pr25.util.PR25RuntimeException;

public final class Flow {
    public static final int FLOW_RESULT_CONTINUE = 0;
    public static final int FLOW_RESULT_CONTINUE_CUT = 1;
    public static final int FLOW_RESULT_REPEAT = 2;
    public static final int FLOW_RESULT_RESTART = 3;
    public static final int FLOW_RESULT_BREAK = 4;
    public static final int FLOW_RESULT_EXIT = 5;
    public static final int FLOW_RESULT_FATAL = 6;

    private static Flow instance;

    private final FlowNode<?>[] toUpdate;
    private int toUpdateSize;
    private final FlowNode<?>[] toDraw;
    private int toDrawSize;

    Flow() {
        toUpdate = new FlowNode[32];
        toDraw = new FlowNode[32];
    }

    public static int addToUpdate(FlowNode<?> node, int priority) {
        if (instance == null) throw new PR25RuntimeException("Flow is not initialized");

        if (instance.toUpdateSize == instance.toUpdate.length) throw new PR25RuntimeException("Flow update list is full");
        node.priority = priority;

        int i = 0;
        FlowNode<?> current = null;
        while (i < instance.toUpdateSize && (current = instance.toUpdate[i]).priority <= priority) { ++i; }

        if (current != null && current.priority > priority) ++i;
        if (i == instance.toUpdateSize)
            instance.toUpdate[instance.toUpdateSize++] = node;
        else {
            for (int j = instance.toUpdateSize++; j > i; --j)
                instance.toUpdate[j] = instance.toUpdate[j - 1];
            instance.toUpdate[i] = node;
        }

        return node.added();
    }

    public static int addToDraw(FlowNode<?> node, int priority) {
        if (instance == null) throw new PR25RuntimeException("Flow is not initialized");

        if (instance.toDrawSize == instance.toDraw.length) throw new PR25RuntimeException("Flow draw list is full");
        node.priority = priority;

        int i = 0;
        FlowNode<?> current = null;
        while (i < instance.toDrawSize && (current = instance.toDraw[i]).priority <= priority) { ++i; }

        if (current != null && current.priority > priority) ++i;
        if (i == instance.toDrawSize)
            instance.toDraw[instance.toDrawSize++] = node;
        else {
            for (int j = instance.toDrawSize++; j > i; --j)
                instance.toDraw[j] = instance.toDraw[j - 1];
            instance.toDraw[i] = node;
        }

        return node.added();
    }

    public static int cut(FlowNode<?> node) {
        if (instance == null) throw new PR25RuntimeException("Flow is not initialized");

        int type = 0;
        int i = 0;
        while (i < instance.toUpdateSize && instance.toUpdate[i] != node) { ++i; }
        if (i < instance.toUpdateSize) type = 1;
        else {
            i = 0;
            while (i < instance.toDrawSize && instance.toDraw[i] != node) { ++i; }
            if (i < instance.toDrawSize) type = 2;
        }

        if (type == 1) {
            --instance.toUpdateSize;
            while (i < instance.toUpdateSize) { instance.toUpdate[i] = instance.toUpdate[++i]; }
        } else if (type == 2) {
            --instance.toDrawSize;
            while (i < instance.toDrawSize) { instance.toDraw[i] = instance.toDraw[++i]; }
        } else throw new PR25RuntimeException("Node doesn't exist in update or draw list");

        return node.removed();
    }

    int executeUpdate() {
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

    int executeDraw() {
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

    static void initialize(Flow instance) {
        if (Flow.instance != null) throw new PR25RuntimeException("Flow was already initialized to instance");

        Flow.instance = instance;
        instance.toUpdateSize = 0;
        instance.toDrawSize = 0;
    }

    static void shutdown() {
        if (Flow.instance == null) throw new PR25RuntimeException("Flow was not initialized, can't shut down");

        Flow.instance.releaseUpdate(0);
        Flow.instance.releaseDraw(0);
        Flow.instance = null;
    }

    private void releaseUpdate(int from) {
        if (from >= instance.toUpdateSize) return;

        for (int i = toUpdateSize - 1; i >= from; --i, --toUpdateSize)
            toUpdate[i].removed();
    }

    private void releaseDraw(int from) {
        if (from >= instance.toDrawSize) return;

        for (int i = toDrawSize - 1; i >= from; --i, --toDrawSize)
            toDraw[i].removed();
    }

    public interface FlowListener<T> {
        int call(T item);
    }

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

        private int call() {
            if (listener != null) return listener.call(ref);
            return 0;
        }

        private int added() {
            int value = 0;
            if (addedListener != null) {
                value = addedListener.call(ref);
                addedListener = null;
            }
            return value;
        }

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
