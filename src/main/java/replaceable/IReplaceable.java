package replaceable;

import org.json.JSONObject;

public interface IReplaceable {
    String replace(MemberFinder finder);
    String toString();
    JSONObject toJSON();

    enum Kind { FOR_REPLACEABLE, IF_REPLACEABLE, REPLACEABLE, SIMPLE_REPLACEABLE; }
}