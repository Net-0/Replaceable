package IfReplaceable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import replaceable.Replaceable;
import java.util.HashMap;

public class EqualTest {

    @Test
    public void equalString() throws Exception {
        var expected = "test";
        var expression = """
                #if test == 'Test Value'#
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
    public void notEqualString() throws Exception {
        var expected = "test";
        var expression = """
                #if test != 'Test Value'#
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
    public void equalStringErroneous() throws Exception {
        var expected = "test";
        var expression = """
                #if test == 'Test Valueeeee'#
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
    public void notEqualStringErroneous() throws Exception {
        var expected = "test";
        var expression = """
                #if test != 'Test Valu'#
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
    public void equalNumber() throws Exception {
        var expected = "test";
        var expression = """
                #if num == 100#
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
    public void notEqualNumber() throws Exception {
        var expected = "test";
        var expression = """
                #if num != 100#
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
    public void equalNumberErroneous() throws Exception {
        var expected = "test";
        var expression = """
                #if num == 101#
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
    public void notEqualNumberErroneous() throws Exception {
        var expected = "test";
        var expression = """
                #if num != 101#
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
    public void equalNull() throws Exception {
        var expected = "test";
        var expression = """
                #if val == null#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("val", null);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void notEqualNull() throws Exception {
        var expected = "test";
        var expression = """
                #if val != null#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("val", null);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void equalNullErroneous() throws Exception {
        var expected = "test";
        var expression = """
                #if val == 'null'#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("val", null);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void notEqualNullErroneous() throws Exception {
        var expected = "test";
        var expression = """
                #if val != 'null'#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("val", null);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

}
