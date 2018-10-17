package infovk.prototype_2.helper;

public final class Tuple<A, B> {
    private final A mFirst;

    private final B mSecond;

    public Tuple(A first, B second) {
        mFirst = first;
        mSecond = second;
    }

    public A getFirst() {
        return mFirst;
    }

    public B getSecond() {
        return mSecond;
    }
}
