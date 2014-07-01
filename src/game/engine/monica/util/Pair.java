package game.engine.monica.util;

public final class Pair<F, L> {

    public Pair(F f, L l) {
        this.first = f;
        this.last = l;
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
        return 31 + first.hashCode() >> 1 + last.hashCode() << 1;
    }
    public F first;
    public L last;
}
