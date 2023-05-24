import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import replaceable.Replaceable;
import java.util.HashMap;

public class SimpleReplaceableTest {

    @Test
    public void simpleReplace() throws Exception {
        var expected = "Value 123";
        var expression = "#test#";
        var context = new HashMap<String, Object>();
        context.put("test", "Value 123");
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void memberPathReplace() throws Exception {
        var expected = "Value 123";
        var expression = "#test.value#";

        var test = new HashMap<String, Object>();
        test.put("value", "Value 123");

        var context = new HashMap<String, Object>();
        context.put("test", test);

        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

}
