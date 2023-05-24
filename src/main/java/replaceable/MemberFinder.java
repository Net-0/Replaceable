package replaceable;

import org.json.JSONObject;
import java.util.Map;

@FunctionalInterface
public interface MemberFinder {
    Object find(String[] path);

    /**
     * Try find the member on context passed on memberPath
     * @param context Root object to find the member based on path
     * @param memberPath Path to find the member in context
     * @param expression The full expression of path. It's just used to throw errors
     */
    static Object findAt(Object context, String[] memberPath, String expression) {
        Object member = context;
        boolean optionalMember = false;
        for (String _key: memberPath) {
            String key = _key.endsWith("?") ? _key.substring(0, _key.length()-1) : _key;
            if (member instanceof Map) member = ((Map<?, ?>) member).get(key);
            else if (member instanceof JSONObject) member = ((JSONObject) member).get(key);
            else if (optionalMember) return null;
            else {
                try {
                    member = member.getClass().getField(key).get(member);
                } catch (Exception ignored) {
                    try {
                        var getKey = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
                        member = member.getClass().getMethod(getKey).invoke(member);
                    } catch (Exception ignored2) {
                        throw new ReplaceException(String.format("Invalid path expression to find member: #%s#", expression));
                    }
                }
            }
            optionalMember = _key.endsWith("?");
        }
        return member;
    }

    /**
     * Try find the member on context passed on memberPath
     * @param context Root object to find the member based on path
     * @param memberPath Path to find the member in context
     */
    static Object findAt(Object context, String[] memberPath) { return MemberFinder.findAt(context, memberPath, String.join(".", memberPath)); }

}
