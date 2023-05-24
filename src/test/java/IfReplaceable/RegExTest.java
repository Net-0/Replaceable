package IfReplaceable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import replaceable.Replaceable;
import java.util.HashMap;

public class RegExTest {

    @Test
    public void regExMatchFlags() throws Exception {
        var expected = "test";
        var expression = """
                #if test =~ /test\\s+value/i#
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
    public void regExMatch() throws Exception {
        var expected = "test";
        var expression = """
                #if test =~ /Test\\s+Value/#
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
    public void regExMatchErroneous() throws Exception {
        var expected = "test";
        var expression = """
                #if test =~ /test\\s+value/#
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
    public void regExMatchFlagsErroneous() throws Exception {
        var expected = "test";
        var expression = """
                #if test =~ /testt\\s+value/i#
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

}
