package replaceable.parser;

import java.util.regex.Pattern;

/**
 * Store the RegEx to capture the 'replace shards' and texts with a relation with this
 */
class RegExs {
    /** A RegEx in String that have all spaces captures, except line break */
    private static final String SPACES = "[\\r\\t\\f ]";

    /** A regEx in String that captures the path of a member */
    private static final String MEMBER_PATH = "[\\w-.?]+";

    /** RegEx that captures a Shard Replace of Simple Replace */
    public static final Pattern SIMPLE_REPLACE = Pattern.compile(String.format("#(%s)#", MEMBER_PATH));

    private static final String S_OPEN_FOR = String.format("(?:^%s*)?#\\s*for\\s+([\\w-]+)(\\s*,\\s*([\\w-]+))?\\s+of\\s+(%s)\\s*#(?:%s*$\\n?)?", SPACES, MEMBER_PATH, SPACES);
    /** RegEx that Captures a Shard Replace of Open For of For Replace */
    public static final Pattern OPEN_FOR = Pattern.compile(S_OPEN_FOR, Pattern.MULTILINE);

    private static final String S_END_FOR = String.format("(?:^%s*)?#\\s*endfor\\s*#(?:%s*$\\n?)?", SPACES, SPACES);
    /** RegEx that captures a Shard Replace of End For of For Replace */
    public static final Pattern END_FOR = Pattern.compile(S_END_FOR, Pattern.MULTILINE);

    private static final String COMPARATORS = "(!=)|(==)|(<)|(<=)|(>)|(>=)|(=~)|(in)|(nin)|(size)|(has)";
    /** RegEx that captures an Operation of a Condition Expression in a Shard Replace of Open If or Else If */
    public static final String OPERATORS = "(\\|\\|)|(&&)";
    private static final String STRING = "'(\\\\'|[^'])*'";
    private static final String REGEX = "\\/((\\\\/|[^/])+)\\/([imdsu]*)";
    private static final String S_SINGLE_VALUE = String.format("(null)|(\\d+)|(%s)|(%s)|(%s)", MEMBER_PATH, STRING, REGEX);
    /** RegEx that captures a Single Value as a Condition Expression in a Shard Replace of Open If or Else If */
    public static final Pattern SINGLE_VALUE = Pattern.compile(S_SINGLE_VALUE);
    private static final String VALUE = String.format("(\\[\\s*((%s)\\s*,\\s*)*(%s)\\s*\\])|(%s)", S_SINGLE_VALUE, S_SINGLE_VALUE, S_SINGLE_VALUE);
    /** RegEx that captures a Single Condition Expression in a Shard Replace of Open If or Else if */
    public static final String CONDITION = String.format("((%s)\\s+(%s)\\s+(%s))|(!)?(%s)", MEMBER_PATH, COMPARATORS, VALUE, MEMBER_PATH);
    private static final String CONDITION_CHAIN = String.format("((?:%s)\\s*(%s)\\s*)*(%s)", CONDITION, OPERATORS, CONDITION);

    private static final String S_OPEN_IF = String.format("(?:^%s*)?#\\s*if\\s+(%s)\\s*#(?:%s*$\\n?)?", SPACES, CONDITION_CHAIN, SPACES);
    /** RegEx that captures a Shard Replace of Open If */
    public static final Pattern OPEN_IF = Pattern.compile(S_OPEN_IF, Pattern.MULTILINE);

    private static final String S_ELSE_IF = String.format("(?:^%s*)?#\\s*elseif\\s+(%s)\\s*#(?:%s*$\\n?)?", SPACES, CONDITION_CHAIN, SPACES);
    /** RegEx that captures a Shard Replace of Else If */
    public static final Pattern ELSE_IF = Pattern.compile(S_ELSE_IF, Pattern.MULTILINE);

    private static final String S_ELSE = String.format("(?:^%s*)?#\\s*else\\s*#(?:%s*$\\n?)?", SPACES, SPACES);
    /** RegEx that captures a Shard Replace of Else */
    public static final Pattern ELSE = Pattern.compile(S_ELSE, Pattern.MULTILINE);

    private static final String S_END_IF = String.format("(?:^%s*)?#\\s*endif\\s*#(?:%s*$\\n?)?", SPACES, SPACES);
    /** RegEx that captures a Shard Replace of End If */
    public static final Pattern END_IF = Pattern.compile(S_END_IF, Pattern.MULTILINE);

    /** Stores the Group Indexes of RegExs */
    public static class GROUPS {

        public static class SINGLE_VALUE {
            public static int NULL = 1;
            public static int NUMBER = 2;
            public static int MEMBER_PATH = 3;
            public static int STRING = 4;
            public static int REGEX = 6;
            public static int REGEX_EXPRESSION = 7;
            public static int REGEX_FLAGS = 9;
        }

        public static class SIMPLE_REPLACE {
            public static int MEMBER_PATH = 1;
        }

        public static class OPEN_FOR {
            public static int VALUE = 1;
            public static int KEY = 3;
            public static int HANDLER_EXPRESSION = 4;
        }

        public static class CONDITION {
            public static int SINGLE_MEMBER = 49;
            public static int NEGATE_SINGLE_MEMBER = 48;
            public static int LEFT_MEMBER = 2;
            public static int COMPARATOR = 3;

            public static int ARRAY = 16;
            public static int NUMBER = 40;
            public static int RIGHT_MEMBER = 41;
            public static int STRING = 42;
            public static int NULL = 39;
            public static int REGEX = 44;
            public static int REGEX_EXPRESSION = 45;
            public static int REGEX_FLAGS = 47;
        }

        public static class OPEN_IF {
            public static int CONDITION = 1;
        }

        public static class ELSE_IF {
            public static int CONDITION = 1;
        }

    }

}
