package game.engine.monica.core.base.element;

public final class ElementList {

    public ElementList(AbstractElement e, AbstractElement... e2) {
        if (e == null)
            throw new NullPointerException("Element is null.");
        elements = new AbstractElement[e2.length + 1];
        if (e2.length != 0)
            for (int i = 0; i != e2.length; ++i)
                if (e2[i] == null)
                    throw new NullPointerException("Element is null.");
                else
                    elements[i + 1] = e2[i];
        elements[0] = e;
    }

    public AbstractElement[] getElements() {
        return elements;
    }

    public int size() {
        return elements.length;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ElementList))
            return false;
        return equals((ElementList) obj);
    }

    public boolean equals(ElementList list) {
        if (list == null)
            return false;
        if (size() != list.size())
            return false;
        for (int i = 0; i != size(); ++i)
            if (elements[i] != list.elements[i])
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        int eh = elements[0].hashCode();
        for (int i = 1; i != size(); ++i) {
            eh *= i;
            eh += elements[i].hashCode();
        }
        return 74 + eh;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(6 * size()).append("[ ");
        for (AbstractElement e : elements)
            sb.append(e.getName()).append(", ");
        sb.replace(sb.length() - 2, sb.length(), " ]");
        return sb.toString();
    }
    private final AbstractElement[] elements;
}
