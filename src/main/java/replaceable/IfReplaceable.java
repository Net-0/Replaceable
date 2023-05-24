package replaceable;

import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class IfReplaceable implements IReplaceable {

    /** This class represents a comparison between two values */
    public enum Comparator {

        NOT_EQUALS(
                "!=",
                (MemberFinder finder, ValueGetter leftValue, ValueGetter rightValue) -> {
                    Object left = leftValue.get(finder);
                    Object right = rightValue.get(finder);
                    if (left == null || right == null) return left != right;
                    if (left instanceof Number && right instanceof Number) return !new BigDecimal(left.toString()).equals(new BigDecimal(right.toString()));
                    return !left.equals(right);
                }
        ),

        EQUALS(
                "==",
                (MemberFinder finder, ValueGetter leftValue, ValueGetter rightValue) -> {
                    Object left = leftValue.get(finder);
                    Object right = rightValue.get(finder);
                    if (left == null || right == null) return left == right;
                    if (left instanceof Number && right instanceof Number) return new BigDecimal(left.toString()).equals(new BigDecimal(right.toString()));
                    return left.equals(right);
                }
        ),

        LESS(
                "<",
                (MemberFinder finder, ValueGetter leftValue, ValueGetter rightValue) -> {
                    Object left = leftValue.get(finder);
                    Object right = rightValue.get(finder);
                    if (!(left instanceof Number) || !(right instanceof Number)) return false;
                    return new BigDecimal(left.toString()).compareTo(new BigDecimal(right.toString())) < 0;
                }
        ),

        LESS_OR_EQUAL(
                "<=",
                (MemberFinder finder, ValueGetter leftValue, ValueGetter rightValue) -> {
                    Object left = leftValue.get(finder);
                    Object right = rightValue.get(finder);
                    if (!(left instanceof Number) || !(right instanceof Number)) return false;
                    return new BigDecimal(left.toString()).compareTo(new BigDecimal(right.toString())) <= 0;
                }
        ),

        HIGHER(
                ">",
                (MemberFinder finder, ValueGetter leftValue, ValueGetter rightValue) -> {
                    Object left = leftValue.get(finder);
                    Object right = rightValue.get(finder);
                    if (!(left instanceof Number) || !(right instanceof Number)) return false;
                    return new BigDecimal(left.toString()).compareTo(new BigDecimal(right.toString())) > 0;
                }
        ),

        HIGHER_OR_EQUAL(
                ">=",
                (MemberFinder finder, ValueGetter leftValue, ValueGetter rightValue) -> {
                    Object left = leftValue.get(finder);
                    Object right = rightValue.get(finder);
                    if (!(left instanceof Number) || !(right instanceof Number)) return false;
                    return new BigDecimal(left.toString()).compareTo(new BigDecimal(right.toString())) >= 0;
                }
        ),

        REGEX_TEST(
                "=~",
                (MemberFinder finder, ValueGetter leftValue, ValueGetter rightValue) -> {
                    Object left = leftValue.get(finder);
                    Object right = rightValue.get(finder);
                    if (!(left instanceof CharSequence) || !(right instanceof Pattern)) return false;
                    return ((Pattern) right).matcher((CharSequence) left).find();
                }
        ),

        HAS(
                "has",
                (MemberFinder finder, ValueGetter leftValue, ValueGetter rightValue) -> {
                    Object left = leftValue.get(finder);
                    Object right = rightValue.get(finder);
                    if (left instanceof Iterable<?>) {
                        boolean has = false;
                        for (var item: ((Iterable<?>) left)) {
                            if (item == null || right == null) has = item == right;
                            else if (!(right instanceof Number) || !(item instanceof Number)) has = item.equals(right);
                            else has = new BigDecimal(right.toString()).equals(new BigDecimal(item.toString()));
                            if (has) break;
                        }
                        return has;
                    }
                    if (left != null && left.getClass().isArray()) {
                        boolean has = false;
                        for (int i = 0; i < Array.getLength(left); i++) {
                            var item = Array.get(left, i);
                            if (item == null || right == null) has = item == right;
                            else if (!(right instanceof Number) || !(item instanceof Number)) has = item.equals(right);
                            else has = new BigDecimal(right.toString()).equals(new BigDecimal(item.toString()));
                            if (has) break;
                        }
                        return has;
                    }
                    return left instanceof String && right instanceof CharSequence && ((String) left).contains((CharSequence) right);
                }
        ),

        IN(
                "in",
                (MemberFinder finder, ValueGetter leftValue, ValueGetter rightValue) -> HAS.lambda.compare(finder, rightValue, leftValue)
        ),

        NOT_IN(
                "nin",
                (MemberFinder finder, ValueGetter leftValue, ValueGetter rightValue) -> !HAS.lambda.compare(finder, rightValue, leftValue)
        ),

        SIZE(
                "size",
                (MemberFinder finder, ValueGetter leftValue, ValueGetter rightValue) -> {
                    Object left = leftValue.get(finder);
                    Object right = rightValue.get(finder);

                    boolean isByte = right instanceof Byte;
                    boolean isShort = right instanceof Short;
                    boolean isInt = right instanceof Integer;
                    boolean isLong = right instanceof Long;
                    if (!isByte && !isShort && !isInt && !isLong) return false;
                    int rightInt = ((Number) right).intValue();
                    if (left instanceof Collection<?>) return ((Collection<?>) left).size() == rightInt;
                    if (left instanceof String) return ((String) left).length() == rightInt;
                    if (left instanceof JSONArray) return ((JSONArray) left).length() == rightInt;
                    if (left != null && left.getClass().isArray()) return Array.getLength(left) == rightInt;
                    return false;
                }
        );


        @FunctionalInterface
        public interface Lambda {
            boolean compare (MemberFinder finder, ValueGetter leftValue, ValueGetter rightValue);
        }

        private final String expression;
        private final Lambda lambda;

        Comparator(String expression, Lambda lambda) {
            this.expression = expression;
            this.lambda = lambda;
        }

        public boolean compare (MemberFinder finder, ValueGetter leftValue, ValueGetter rightValue) {
            return this.lambda.compare(finder, leftValue, rightValue);
        }

        public String toString () { return this.expression; }

        public static Comparator from(String expression) {
            for (var comp: Comparator.values()) if (comp.expression.equals(expression)) return comp;
            return null;
        }
    }

    /** This enum represents operation between {@link Validator} */
    public enum Operator {
        OR("||"),
        AND("&&");

        private final String expression;

        Operator(String expression) { this.expression = expression; }

        public static Operator from(String expression) {
            for (var op: Operator.values()) if (op.expression.equals(expression)) return op;
            return null;
        }
    }

    /** This class represents a getter to static values or values defined by a member path expression */
    public interface ValueGetter {

        enum Kind {
            NUMBER,
            MEMBER,
            STRING,
            REGEX,
            NULL,
            LIST;
        }

        static ValueGetter from(JSONObject json) {
            var kind = Kind.valueOf(json.getString("kind"));
            return switch (kind) {
                case NUMBER -> ValueGetter.ofNumber(json.getString("numberStr"));
                case MEMBER -> ValueGetter.ofMember(json.getString("memberPathExpression"));
                case STRING -> ValueGetter.ofString(json.getString("stringExpression"));
                case REGEX -> ValueGetter.ofRegEx(json.getString("expression"), json.getString("flags"));
                case NULL -> ValueGetter.nullable;
                case LIST -> {
                    var jsonList = json.getJSONArray("list");
                    List<ValueGetter> list = new ArrayList<>(jsonList.length());
                    for (var item: jsonList) list.add(ValueGetter.from((JSONObject) item));
                    yield ValueGetter.ofList(list);
                }
            };
        }

        Object get(MemberFinder finder);
        String toString();
        JSONObject toJSON();

        /**
         * Create a {@link ValueGetter} that return a Long number
         * @param numberStr the value of Long number that the getter will return
         */
        static ValueGetter ofNumber(String numberStr) {
            var number = Long.parseLong(numberStr);
            return new ValueGetter() {
                public Object get(MemberFinder finder) { return number; }
                public String toString() { return numberStr; }
                public JSONObject toJSON() {
                    var json = new JSONObject();
                    json.put("kind", Kind.NUMBER.toString());
                    json.put("numberStr", numberStr);
                    return json;
                }
            };
        }

        /**
         * Create a {@link ValueGetter} that return a member in context based on path
         * @param memberPathExpression the path expression of where is the member in context
         */
        static ValueGetter ofMember(String memberPathExpression) {
            String[] memberPath = memberPathExpression.split("\\.");
            return new ValueGetter() {
                public Object get(MemberFinder finder) { return finder.find(memberPath); }
                public String toString() { return memberPathExpression; }
                public JSONObject toJSON() {
                    var json = new JSONObject();
                    json.put("kind", Kind.MEMBER.toString());
                    json.put("memberPathExpression", memberPathExpression);
                    return json;
                }
            };
        }

        /**
         * Create a {@link ValueGetter} that return a String
         *
         * @param stringExpression the expression of value of string that will be returned
         */
        static ValueGetter ofString(String stringExpression) {
            var string = stringExpression.substring(1, stringExpression.length() - 1).replaceAll("\\\\'", "'");
            return new ValueGetter() {
                public Object get(MemberFinder finder) { return string; }
                public String toString() { return stringExpression; }
                public JSONObject toJSON() {
                    var json = new JSONObject();
                    json.put("kind", Kind.STRING.toString());
                    json.put("stringExpression", stringExpression);
                    return json;
                }
            };
        }

        /**
         * Create a {@link ValueGetter} that return a RegEx/{@link Pattern}
         *
         * @param expression the body of the regular expression
         * @param flags      the flags of RegEx
         */
        static ValueGetter ofRegEx(String expression, String flags) {
            int flagsInt = 0;
            for (char c : flags.toCharArray()) {
                flagsInt |= switch (c) {
                    case 'i' -> Pattern.CASE_INSENSITIVE;
                    case 'm' -> Pattern.MULTILINE;
                    case 'd' -> Pattern.UNIX_LINES;
                    case 's' -> Pattern.DOTALL;
                    case 'u' -> Pattern.UNICODE_CASE;
                    default -> 0;
                };
            }
            var regEx = Pattern.compile(expression, flagsInt);
            return new ValueGetter() {
                public Object get(MemberFinder finder) { return regEx; }
                public String toString() { return String.format("/%s/%s", expression, flags); }
                public JSONObject toJSON() {
                    var json = new JSONObject();
                    json.put("kind", Kind.REGEX.toString());
                    json.put("expression", expression);
                    json.put("flags", flags);
                    return json;
                }
            };
        }

        /**
         * The default {@link ValueGetter} that return a null
         */
        ValueGetter nullable = new ValueGetter() {
            public Object get(MemberFinder finder) { return null; }
            public String toString() { return "null"; }
            public JSONObject toJSON() { var json = new JSONObject(); json.put("kind", Kind.NULL.toString()); return json; }
        };

        /**
         * Create a {@link ValueGetter} that return a List of items
         * @param list a list of getters to be mapped to a simple list
         * */
        static ValueGetter ofList(List<ValueGetter> list) {
            return new ValueGetter() {
                public Object get(MemberFinder finder) {
                    return list.stream().map(e -> e.get(finder)).toList();
                }

                public String toString() {
                    return String.format("[ %s ]", String.join(", ", list.stream().map(ValueGetter::toString).toList()) );
                }

                public JSONObject toJSON() {
                    var json = new JSONObject();
                    json.put("kind", Kind.LIST.toString());
                    json.put("list", list.stream().map(ValueGetter::toJSON).toList());
                    return json;
                }
            };
        }
    }

    /** This interface represents a validation of something */
    public interface Validator {
        boolean validate(MemberFinder finder);
        JSONObject toJSON();

        /** A validator that always return true, typically used to 'else' conditional replaceables */
        Validator alwaysTrue = new Validator() {
            public boolean validate(MemberFinder finder) { return true; }
            public JSONObject toJSON() { var json = new JSONObject(); json.put("kind", Kind.ALWAYS_TRUE.toString()); return json; }
        };

        enum Kind { SINGLE, PAIR, ALWAYS_TRUE; }

        static Validator from(JSONObject json) {
            var kind = Kind.valueOf(json.getString("kind"));
            return switch (kind) {
                case SINGLE -> Condition.from(json);
                case PAIR -> ConditionPair.from(json);
                case ALWAYS_TRUE -> Validator.alwaysTrue;
            };
        }
    }

    /** This class represents a validation of a Single Value of a Comparison between values */
    public static class Condition implements Validator {
        private final boolean singleValue;
        private final boolean negate;
        private final ValueGetter leftValue;
        private final Comparator comparator;
        private final ValueGetter rightValue;

        public Condition(ValueGetter leftValue, Comparator comparator, ValueGetter rightValue) {
            this.singleValue = false;
            this.negate = false;
            this.leftValue = leftValue;
            this.comparator = comparator;
            this.rightValue = rightValue;
        }

        public Condition(ValueGetter leftValue, boolean negate) {
            this.singleValue = true;
            this.leftValue = leftValue;
            this.negate = negate;
            this.comparator = null;
            this.rightValue = null;
        }

        public boolean validate(MemberFinder finder) {
            if (this.singleValue) {
                Object val = this.leftValue.get(finder);
                val = val instanceof Boolean ? val : val != null;
                return this.negate != (Boolean) val;
            }
            return this.comparator.compare(finder, leftValue, rightValue);
        }

        public String toString() {
            if (this.singleValue) return (this.negate ? "!" : "") + this.leftValue;
            else return String.format("%s %s %s", this.leftValue, this.comparator, this.rightValue);
        }

        public JSONObject toJSON() {
            var json = new JSONObject();
            json.put("kind", Kind.SINGLE.toString());
            json.put("singleValue", this.singleValue);
            json.put("negate", this.negate);
            json.put("leftValue", this.leftValue.toJSON());
            json.put("comparator", this.comparator != null ? this.comparator.name() : null);
            json.put("rightValue", this.rightValue != null ? this.rightValue.toJSON() : null);
            return json;
        }

        public static Condition from(JSONObject json) {
            if (json.getBoolean("singleValue")) {
                ValueGetter leftValue = ValueGetter.from(json.getJSONObject("leftValue"));
                boolean negate = json.getBoolean("negate");
                return new Condition(leftValue, negate);
            } else {
                ValueGetter leftValue = ValueGetter.from(json.getJSONObject("leftValue"));
                Comparator comparator = Comparator.valueOf(json.getString("comparator"));
                ValueGetter rightValue = ValueGetter.from(json.getJSONObject("rightValue"));
                return new Condition(leftValue, comparator, rightValue);
            }
        }
    }

    public static class ConditionPair implements Validator {
        private final Validator leftCondition;
        private final Operator operation;
        private final Validator rightCondition;

        public ConditionPair(Validator leftCondition, Operator operation, Validator rightCondition) {
            this.leftCondition = leftCondition;
            this.operation = operation;
            this.rightCondition = rightCondition;
        }

        public boolean validate(MemberFinder finder) {
            if (this.operation == Operator.AND) return leftCondition.validate(finder) && rightCondition.validate(finder);
            else return leftCondition.validate(finder) || rightCondition.validate(finder);
        }

        public String toString() {
            return String.format("%s %s %s", this.leftCondition, this.operation, this.rightCondition);
        }

        public JSONObject toJSON() {
            var json = new JSONObject();
            json.put("kind", Kind.PAIR.toString());
            json.put("leftCondition", this.leftCondition.toJSON());
            json.put("operation", this.operation.toString());
            json.put("rightCondition", this.rightCondition.toJSON());
            return json;
        }

        public static ConditionPair from(JSONObject json) {
            var leftCondition = Validator.from(json.getJSONObject("leftCondition"));
            var operation = json.has("operation") ? Operator.valueOf(json.getString("operation")) : null;
            var rightCondition = Validator.from(json.getJSONObject("rightCondition"));
            return new ConditionPair(leftCondition, operation, rightCondition);
        }
    }

    public static class ConditionalReplaceable {
        final Replaceable body;
        final Validator validator;

        public ConditionalReplaceable(Replaceable body, Validator validator) { this.body = body; this.validator = validator; }

        public JSONObject toJSON() {
            var json = new JSONObject();
            json.put("body", body.toJSON());
            json.put("validator", validator.toJSON());
            return json;
        }

        public static ConditionalReplaceable from(JSONObject json) {
            var body = Replaceable.from(json.getJSONObject("body"));
            var validator = Validator.from(json.getJSONObject("validator"));
            return new ConditionalReplaceable(body, validator);
        }
    }

    private final List<ConditionalReplaceable> conditionalsReplaceables;

    public IfReplaceable(List<ConditionalReplaceable> conditionalsReplaceables) {
        this.conditionalsReplaceables = conditionalsReplaceables;
    }

    public String replace(MemberFinder finder) {
        for (var replaceableBody: this.conditionalsReplaceables)
            if (replaceableBody.validator.validate(finder))
                return replaceableBody.body.replace(finder);
        return "";
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (var a: this.conditionalsReplaceables) {
            if (i == 0) sb.append("#if ").append(a.validator).append("#\n");
            else if (i == this.conditionalsReplaceables.size() - 1) sb.append("#else#\n");
            else sb.append("#elseif ").append(a.validator).append("#\n");
            sb.append(a.body);
            i++;
        }
        return sb.append("#endif#\n").toString();
    }

    public JSONObject toJSON() {
        var json = new JSONObject();
        json.put("kind", Kind.IF_REPLACEABLE.toString());
        json.put("conditionalsReplaceables", this.conditionalsReplaceables.stream().map(ConditionalReplaceable::toJSON).toList());
        return json;
    }

    public static IfReplaceable from(JSONObject json) {
        JSONArray jsonList = json.getJSONArray("conditionalsReplaceables");
        List<ConditionalReplaceable> conditionalsReplaceables = new ArrayList<>(jsonList.length());
        for (var item: jsonList) conditionalsReplaceables.add( ConditionalReplaceable.from((JSONObject) item) );

        return new IfReplaceable(conditionalsReplaceables);
    }
}
