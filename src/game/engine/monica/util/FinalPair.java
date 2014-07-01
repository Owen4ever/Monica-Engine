package game.engine.monica.util;

public final class FinalPair<F, L> {

    public FinalPair(F f, L l) {
        this.first = f;
        this.last = l;
        hashCode = 31 + first.hashCode() + last.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass())
            return false;
        return equals((FinalPair) obj);
    }

    public boolean equals(FinalPair p) {
        if (p == null)
            return false;
        return (first == null ? p.first == null : first.equals(p.first))
                && (last == null ? p.last == null : last.equals(p.last));
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
    public final F first;
    public final L last;
    private final int hashCode;
}
