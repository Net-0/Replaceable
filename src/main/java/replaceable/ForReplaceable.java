package replaceable;

import org.json.JSONObject;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ForReplaceable implements IReplaceable {
    final String value;
    final String key;
    final String handlerExpression;
    private final String[] handlerPath;
    final Replaceable body;

    /**
     * Wrap the MemberFinder.findAt() but to find member using 'higherPath' from the 'context'
     * @param context Root object to find the member based on path
     * @param higherPath Path to find the member in context, where the first item is the representation of context
     */
    private static Object subFindAt(Object context, String[] higherPath) {
        final String expression = String.join(".", higherPath);
        String[] subPath = Arrays.copyOfRange(higherPath, 1, higherPath.length);
        return MemberFinder.findAt(context, subPath, expression);
    }

    public static ForReplaceable from(JSONObject json) {
        var value = json.getString("value");
        var key = json.has("key") ? json.getString("key") : null;
        var handlerExpression = json.getString("handlerExpression");
        var body = Replaceable.from(json.getJSONObject("body"));

        return new ForReplaceable(value, key, handlerExpression, body);
    }

    public ForReplaceable (String value, String key, String handlerExpression, Replaceable body) {
        if (value.equals(key)) throw new ReplaceException("A for-loop replace cant have the same name for 'value' and 'key': " + value);
        this.value = value;
        this.key = key;
        this.handlerExpression = handlerExpression;
        this.handlerPath = handlerExpression.split("\\.");
        this.body = body;

        this.handlerFinder = (path) -> {
            if (path.length == 0) return null;
            final String firstKey = path[0];

            if (firstKey.equals(this.value)) {
                if (path.length == 1) return this.currentValue;
                return ForReplaceable.subFindAt(this.currentValue, path);
            } else if (firstKey.equals(this.key)) {
                if (path.length == 1) return this.currentKey;
                return ForReplaceable.subFindAt(this.currentKey, path);
            } else return currentFinder.find(path);
        };
    }

    /** Return the for replace expression of this */
    public String toString() {
        String members = key != null ? String.format("%s, %s", value, key) : value;
        return String.format("#for %s of %s#\n%s#endfor#\n", members, handlerExpression, body);
    }

    private Object currentValue = null;
    private Object currentKey = null;
    private MemberFinder currentFinder = null;
    private final MemberFinder handlerFinder;

    synchronized private String replaceIterable(MemberFinder finder, Iterable<?> handler) {
        this.currentFinder = finder;
        List<String> placedBodies = new ArrayList<>();
        int index = 0;
        for (var item: handler) {
            this.currentKey = index++;
            this.currentValue = item;
            placedBodies.add(body.replace(this.handlerFinder));
        }
        return String.join("", placedBodies);
    }

    synchronized private String replaceMap(MemberFinder finder, Map<?, ?> handler) {
        this.currentFinder = finder;
        List<String> placedBodies = new ArrayList<>(handler.size());
        for (Map.Entry<?, ?> entry: handler.entrySet()) {
            this.currentKey = entry.getKey();
            this.currentValue = entry.getValue();
            placedBodies.add(body.replace(this.handlerFinder));
        }
        return String.join("", placedBodies);
    }

    synchronized private String replaceJSONObject(MemberFinder finder, JSONObject handler) {
        this.currentFinder = finder;
        List<String> placedBodies = new ArrayList<>(handler.length());
        for (var key: handler.keySet()) {
            this.currentKey = key;
            this.currentValue = handler.get(key);
            placedBodies.add(body.replace(this.handlerFinder));
        }
        return String.join("", placedBodies);
    }

    synchronized private String replaceArray(MemberFinder finder, Object handler) {
        this.currentFinder = finder;
        List<String> placedBodies = new ArrayList<>();
        for (int index = 0; index < Array.getLength(handler); index++) {
            this.currentKey = index;
            this.currentValue = Array.get(handler, index);
            placedBodies.add(body.replace(this.handlerFinder));
        }
        return String.join("", placedBodies);
    }

    /** Execute a loop replacing the body of the for and return the result */
    synchronized public String replace(MemberFinder finder) {
        Object handler = finder.find(handlerPath);

        if (handler instanceof Iterable<?>) return replaceIterable(finder, (Iterable<?>) handler);
        if (handler instanceof Map) return replaceMap(finder, (Map<?, ?>) handler);
        if (handler instanceof JSONObject) return replaceJSONObject(finder, (JSONObject) handler);
        if (handler != null && handler.getClass().isArray()) return replaceArray(finder, handler);

        throw new ReplaceException(String.format("Invalid Type of member in #%s#. Expected a List, Map, JSONArray or JSONObject", handlerExpression));
    }

    public JSONObject toJSON() {
        var json = new JSONObject();
        json.put("kind", Kind.FOR_REPLACEABLE.toString());
        json.put("value", this.value);
        json.put("key", this.key);
        json.put("handlerExpression", this.handlerExpression);
        json.put("body", this.body.toJSON());
        return json;
    }

}
