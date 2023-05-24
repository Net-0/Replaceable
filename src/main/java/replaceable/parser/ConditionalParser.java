package replaceable.parser;

import replaceable.IfReplaceable.*;
import replaceable.parser.RegExs.GROUPS;
import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** This class has the responsibility to parse {@link ConditionalReplaceable} */
class ConditionalParser {

    /**
     * Mutation of {@link RegExs.CONDITION} to be able to capture an operation after a condition
     */
    private static final Pattern CONDITION = Pattern.compile(String.format("(?:%s)\\s*(?<operation>%s)?", RegExs.CONDITION, RegExs.OPERATORS));

    /**
     * Create a {@link ValueGetter} that return a List of items
     * @param listStr the list expression to be parsed in a list of {@link ValueGetter} to create this getter
     */
    private static ValueGetter listParser(String listStr) {
        var matcher = RegExs.SINGLE_VALUE.matcher(listStr);
        var list = new ArrayList<ValueGetter>();
        while (matcher.find()) {
            var itemNumber = matcher.group(GROUPS.SINGLE_VALUE.NUMBER);
            var itemMember = matcher.group(GROUPS.SINGLE_VALUE.MEMBER_PATH);
            var itemString = matcher.group(GROUPS.SINGLE_VALUE.STRING);
            if (matcher.group(GROUPS.SINGLE_VALUE.NULL) != null)
                list.add(ValueGetter.nullable);
            else if (itemNumber != null)
                list.add(ValueGetter.ofNumber(itemNumber));
            else if (itemMember != null)
                list.add(ValueGetter.ofMember(itemMember));
            else if (itemString != null)
                list.add(ValueGetter.ofString(itemString));
            else if (matcher.group(GROUPS.SINGLE_VALUE.REGEX) != null)
                list.add(ValueGetter.ofRegEx(matcher.group(GROUPS.SINGLE_VALUE.REGEX_EXPRESSION), matcher.group(GROUPS.SINGLE_VALUE.REGEX_FLAGS)));
            else throw new RuntimeException("Invalid match: " + matcher.group());
        }

        return ValueGetter.ofList(list);
    }

    /**
     * Return a {@link Condition} based on the match
     */
    private static Condition conditionOf(MatchResult match) {
        var singleMember = match.group(GROUPS.CONDITION.SINGLE_MEMBER);

        if (singleMember != null) {
            var negateSingleMember = match.group(GROUPS.CONDITION.NEGATE_SINGLE_MEMBER);
            return new Condition(ValueGetter.ofMember(singleMember), negateSingleMember != null);
        } else {
            var leftMember = match.group(GROUPS.CONDITION.LEFT_MEMBER);
            var comparator = Comparator.from(match.group(GROUPS.CONDITION.COMPARATOR));

            var rightArray = match.group(GROUPS.CONDITION.ARRAY);
            var rightNumber = match.group(GROUPS.CONDITION.NUMBER);
            var rightMember = match.group(GROUPS.CONDITION.RIGHT_MEMBER);
            var rightString = match.group(GROUPS.CONDITION.STRING);
            var rightNull = match.group(GROUPS.CONDITION.NULL);
            var rightRegEx = match.group(GROUPS.CONDITION.REGEX);

            ValueGetter rightGetter = null;
            if (rightArray != null)
                rightGetter = listParser(rightArray);
            else if (rightNumber != null)
                rightGetter = ValueGetter.ofNumber(rightNumber);
            else if (rightMember != null)
                rightGetter = ValueGetter.ofMember(rightMember);
            else if (rightString != null)
                rightGetter = ValueGetter.ofString(rightString);
            else if (rightNull != null)
                rightGetter = ValueGetter.nullable;
            else if (rightRegEx != null)
                rightGetter = ValueGetter.ofRegEx(match.group(GROUPS.CONDITION.REGEX_EXPRESSION), match.group(GROUPS.CONDITION.REGEX_FLAGS));
            else
                throw new RuntimeException("Something Wrong in Parser. Invalid match: " + match.group());

            return new Condition(ValueGetter.ofMember(leftMember), comparator, rightGetter);
        }
    }

    /**
     * Create a Conditional Replaceable based on Shard of open conditional and body fragment
     */
    static ConditionalReplaceable replaceableOf(String conditionStr, Fragment bodyFragment) {
        Matcher matcher = CONDITION.matcher(conditionStr);
        Operator operation = null;
        Validator condition = null;

        while (matcher.find()) {
            String nextOperation = matcher.group("operation");
            Condition nextCondition = conditionOf(matcher);
            condition = condition == null ? nextCondition : new ConditionPair(condition, operation, nextCondition);
            operation = nextOperation != null ? Operator.from(nextOperation) : null;
        }

        return new ConditionalReplaceable(Fragment.replaceableFrom(bodyFragment), condition);
    }

    /**
     * Create a Conditional Replaceable that the validator always return true
     */
    static ConditionalReplaceable elseOf(Fragment bodyFragment) {
        return new ConditionalReplaceable(Fragment.replaceableFrom(bodyFragment), Validator.alwaysTrue);
    }

}
