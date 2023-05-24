package IfReplaceable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import replaceable.Replaceable;
import java.util.HashMap;

public class OperatorsTest {

    @Test
    public void equalAndOperator() throws Exception {
        var expected = "test";
        var expression = """
                #if test == 'Test Value' && test2 == null#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("test2", null);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void equalOrOperator() throws Exception {
        var expected = "test";
        var expression = """
                #if test == '' || test2 == null#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("test2", null);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }



    @Test
    public void notEqualAndOperator() throws Exception {
        var expected = "test";
        var expression = """
                #if test != '' && test2 != 1#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("test2", null);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void notEqualOrOperator() throws Exception {
        var expected = "test";
        var expression = """
                #if test != 'Test Value' || test2 != 128#
                test
                #else#
                error - test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("test2", null);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }



    @Test
    public void equalAndOperatorErroneous() throws Exception {
        var expected = "test";
        var expression = """
                #if test == '' && test2 == null#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("test2", null);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void equalOrOperatorErroneous() throws Exception {
        var expected = "test";
        var expression = """
                #if test == '' || test2 == 'null'#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("test2", null);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }



    @Test
    public void notEqualAndOperatorErroneous() throws Exception {
        var expected = "test";
        var expression = """
                #if test != 'Test Value' && test2 != 1#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("test2", null);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void notEqualOrOperatorErroneous() throws Exception {
        var expected = "test";
        var expression = """
                #if test != 'Test Value' || test2 != null#
                error - test
                #else#
                test
                #endif#
                """;
        var context = new HashMap<String, Object>();
        context.put("test", "Test Value");
        context.put("test2", null);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

}
