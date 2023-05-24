package replaceable;

import org.json.JSONObject;

public class SimpleReplaceable implements IReplaceable {
    final String expression;
    private final String[] memberPath;

    public static SimpleReplaceable from(JSONObject json) {
        return new SimpleReplaceable(json.getString("expression"));
    }

    public SimpleReplaceable (String expression) {
        this.expression = expression;
        this.memberPath = expression.split("\\.");
    }

    public String toString() { return String.format("#%s#", this.expression); }

    public String replace(MemberFinder finder) {
        var result = finder.find(memberPath);
        return result == null ? "" : result.toString();
    }

    public JSONObject toJSON() {
        var json = new JSONObject();
        json.put("kind", Kind.SIMPLE_REPLACEABLE.toString());
        json.put("expression", this.expression);
        return json;
    }
}
