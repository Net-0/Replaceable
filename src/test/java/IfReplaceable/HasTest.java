package IfReplaceable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import replaceable.Replaceable;
import java.util.HashMap;
import java.util.List;

public class HasTest {

    @Test
    public void stringHasSubString() throws Exception {
        var expected = "test";
        var expression = """
                #if test has 'Value'#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void stringHasErroneousSubString() throws Exception {
        var expected = "test";
        var expression = """
                #if test has 'valor'#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void listHas() throws Exception {
        var expected = "test";
        var expression = """
                #if nums has 8#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("nums", List.of(2, 4, 8));
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void listHasErroneous() throws Exception {
        var expected = "test";
        var expression = """
                #if nums has 81#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("nums", List.of(2, 4, 8));
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void arrayHas() throws Exception {
        var expected = "test";
        var expression = """
                #if nums has 8#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("nums", new int[] { 2, 4, 8 });
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void arrayHasErroneous() throws Exception {
        var expected = "test";
        var expression = """
                #if nums has 81#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("nums", new int[] { 2, 4, 8 });
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

}
