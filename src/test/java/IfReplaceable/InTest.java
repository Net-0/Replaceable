package IfReplaceable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import replaceable.Replaceable;

import java.util.HashMap;
import java.util.List;

public class InTest {

    @Test
    public void in() throws Exception {
        var expected = "test";
        var expression = """
                #if test in [ 1, null, 'irineu', 'Test Value']#
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
    public void erroneousIn() throws Exception {
        var expected = "test";
        var expression = """
                #if test in [ 1, null, 'irineu']#
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
    public void inList() throws Exception {
        var expected = "test";
        var expression = """
                #if test in values#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("values", List.of(1, "irineu", "Test Value"));
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void erroneousInList() throws Exception {
        var expected = "test";
        var expression = """
                #if test in values#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("values", List.of(1, "irineu"));
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void inArray() throws Exception {
        var expected = "test";
        var expression = """
                #if test in values#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("values", new Object[] { 1, null, "irineu", "Test Value" });
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void erroneousInArray() throws Exception {
        var expected = "test";
        var expression = """
                #if test in values#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("values", new Object[] { 1, null, "irineu" });
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void notIn() throws Exception {
        var expected = "test";
        var expression = """
                #if test nin [ 1, null, 'irineu']#
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
    public void erroneousNotIn() throws Exception {
        var expected = "test";
        var expression = """
                #if test nin [ 1, null, 'irineu', 'Test Value']#
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
    public void notInList() throws Exception {
        var expected = "test";
        var expression = """
                #if test nin values#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("values", List.of(1, "irineu"));
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void erroneousNotInList() throws Exception {
        var expected = "test";
        var expression = """
                #if test nin values#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("values", List.of(1, "irineu", "Test Value"));
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void notInArray() throws Exception {
        var expected = "test";
        var expression = """
                #if test nin values#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("values", new Object[]{ 1, null, "irineu" });
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void erroneousNotInArray() throws Exception {
        var expected = "test";
        var expression = """
                #if test nin values#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("values", new Object[]{ 1, null, "irineu", "Test Value" });
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }


}
