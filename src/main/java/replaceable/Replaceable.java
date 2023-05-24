package replaceable;

import replaceable.parser.ReplaceableParser;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Replaceable implements IReplaceable {

    /** Create a new {@link Replaceable} from an expression of replace */
    public static Replaceable from(String expression) throws IOException {
        return ReplaceableParser.parse(expression);
    }

    /**
     * Create a new {@link Replaceable} from a JSON structure definition.
     * Obs.: This method is around 10 times faster than creating from an expression of replace in string
     */
    public static Replaceable from(JSONObject json) {
        var toReplaceJSON = json.getJSONArray("toReplace");
        var toReplace = new ArrayList<IReplaceable>(toReplaceJSON.length());
        for (var $item: toReplaceJSON) {
            var item = (JSONObject) $item;
            var itemKind = Kind.valueOf(item.getString("kind"));
            var replaceable = switch (itemKind) {
                case REPLACEABLE -> Replaceable.from(item);
                case IF_REPLACEABLE -> IfReplaceable.from(item);
                case FOR_REPLACEABLE -> ForReplaceable.from(item);
                case SIMPLE_REPLACEABLE -> SimpleReplaceable.from(item);
            };
            toReplace.add(replaceable);
        }

        var template = json.getString("template");
        return new Replaceable(template, toReplace);
    }

    private final String template;
    private final List<IReplaceable> toReplace;

    public Replaceable(String template, List<IReplaceable> toReplace) {
        this.template = template;
        this.toReplace = toReplace;
    }

    /** Replace the content using 'finder' to get values from member path */
    public String replace(MemberFinder finder) {
        var toPlace = toReplace.stream().map(r -> r.replace(finder)).toList();
        return String.format(template, toPlace.toArray());
    }

    /** Replace using a {@link Map} as the context to find members */
    public String replace(Map<String, Object> context) { return replace((memberPath) -> MemberFinder.findAt(context, memberPath)); }

    /** Replace using a {@link JSONObject} as the context to find members */
    public String replace(JSONObject context) { return replace((memberPath) -> MemberFinder.findAt(context, memberPath)); }

    /** Replace using a {@link Object} as the context to find members */
    public String replace(Object context) { return replace((memberPath) -> MemberFinder.findAt(context, memberPath)); }

    /** Replace with an empty context to find members */
    public String replace() { return replace((memberPath) -> MemberFinder.findAt(new HashMap<>(), memberPath)); }

    public String toString() {
        var str = this.toReplace.stream().map(Object::toString).toList();
        return String.format(template, str.toArray());
    }

    /** Returns a JSON Structure definition of this {@link Replaceable} */
    public JSONObject toJSON() {
        var json = new JSONObject();
        json.put("kind", Kind.REPLACEABLE.toString());
        json.put("template", this.template);
        json.put("toReplace", this.toReplace.stream().map(IReplaceable::toJSON).toList());
        return json;
    }

}
