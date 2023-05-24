package IfReplaceable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import replaceable.Replaceable;
import java.util.HashMap;
import java.util.List;

public class SizeTest {

    @Test
    public void stringSize() throws Exception {
        var expected = "test";
        var expression = """
                #if str size 3#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("str", "ABC");
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void listSize() throws Exception {
        var expected = "test";
        var expression = """
                #if nums size 3#
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
    public void arraySize() throws Exception {
        var expected = "test";
        var expression = """
                #if nums size 3#
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
    public void stringErroneousSize() throws Exception {
        var expected = "test";
        var expression = """
                #if str size 10#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("str", "ABC");
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void listErroneousSize() throws Exception {
        var expected = "test";
        var expression = """
                #if nums size 10#
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
    public void arrayErroneousSize() throws Exception {
        var expected = "test";
        var expression = """
                #if nums size 10#
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
