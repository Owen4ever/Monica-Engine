
package engine.monica.core.memory;

public interface IEvent {

    CEventType getEventType();

    public static class CEventType {

        public CEventType(String type) {
            if (type == null || type.isEmpty())
                throw new NullPointerException("The event type name is null.");
            typeName = type;
        }

        public String getTypeName() {
            return typeName;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof CEventType))
                return false;
            CEventType t = (CEventType) obj;
            return typeName.equals(t.typeName);
        }

        @Override
        public int hashCode() {
            return 31 + typeName.hashCode();
        }

        @Override
        public String toString() {
            return typeName;
        }
        private String typeName;
    }
}
