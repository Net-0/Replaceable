package IfReplaceable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import replaceable.Replaceable;
import java.util.HashMap;

public class StructureTest {

    @Test
    public void wrongSyntax() throws Exception {
        var expected = """
                #if test == #
                error - test
                #else#
                error - test
                #endif#
                """;
        var expression = """
                #if test == #
                error - test
                #else#
                error - test
                #endif#
                """;

        var context = new HashMap<String, Object>();

        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void _if() throws Exception {
        var expected = "test";
        var expression = """
                #if test.value#
                test
                #else#
                error - test
                #endif#
                """;

        var test = new HashMap<String, Object>();
        test.put("value", true);

        var context = new HashMap<String, Object>();
        context.put("test", test);

        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void elseIf() throws Exception {
        var expected = "test";
        var expression = """
                #if !test.value#
                error - test
                #elseif test.value#
                test
                #else#
                error - test
                #endif#
                """;

        var test = new HashMap<String, Object>();
        test.put("value", true);

        var context = new HashMap<String, Object>();
        context.put("test", test);

        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void _else() throws Exception {
        var expected = "test";
        var expression = """
                #if !test.value#
                error - test
                #else#
                test
                #endif#
                """;

        var test = new HashMap<String, Object>();
        test.put("value", true);

        var context = new HashMap<String, Object>();
        context.put("test", test);

        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

}