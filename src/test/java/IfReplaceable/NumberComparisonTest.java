package IfReplaceable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import replaceable.Replaceable;
import java.util.HashMap;

public class NumberComparisonTest {

    @Test
    public void higher() throws Exception {
        var expected = "test";
        var expression = """
                #if num > 100#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("num", 100);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void lower() throws Exception {
        var expected = "test";
        var expression = """
                #if num < 100#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("num", 100);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void lowerOrEqual() throws Exception {
        var expected = "test";
        var expression = """
                #if num <= 100#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("num", 100);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void higherOrEqual() throws Exception {
        var expected = "test";
        var expression = """
                #if num >= 100#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("num", 100);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

}
