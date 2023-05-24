
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import replaceable.Replaceable;
import java.util.HashMap;
import java.util.List;

public class ForReplaceableTest {

    @Test
    public void wrongSyntax() throws Exception {
        var expected = """
                #for item off lista#
                - Olá abc;
                #endfor#
                """;
        var expression = """
                #for item off lista#
                - Olá abc;
                #endfor#
                """;

        var context = new HashMap<String, Object>();
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void replaceList() throws Exception {
        var expected = """
                - Olá Test-Name 1;
                - Olá Test-Name 2;
                - Olá Test-Name 3;
                """;
        var expression = """
                #for item of lista#
                - Olá #item.name#;
                #endfor#
                """;
        var item1 = new HashMap<String, Object>();
        item1.put("name", "Test-Name 1");

        var item2 = new HashMap<String, Object>();
        item2.put("name", "Test-Name 2");

        var item3 = new HashMap<String, Object>();
        item3.put("name", "Test-Name 3");

        var context = new HashMap<String, Object>();
        context.put("lista", List.of(item1, item2, item3));
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void replaceArray() throws Exception {
        var expected = """
                - Olá Test-Name 1;
                - Olá Test-Name 2;
                - Olá Test-Name 3;
                """;
        var expression = """
                #for item of lista#
                - Olá #item.name#;
                #endfor#
                """;
        var item1 = new HashMap<String, Object>();
        item1.put("name", "Test-Name 1");

        var item2 = new HashMap<String, Object>();
        item2.put("name", "Test-Name 2");

        var item3 = new HashMap<String, Object>();
        item3.put("name", "Test-Name 3");

        var context = new HashMap<String, Object>();
        context.put("lista", new Object[]{ item1, item2, item3 });
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

    @Test
    public void replaceJSONArray() throws Exception {
        var expected = """
                - Olá Test-Name 1;
                - Olá Test-Name 2;
                - Olá Test-Name 3;
                """;
        var expression = """
                #for item of lista#
                - Olá #item.name#;
                #endfor#
                """;

        var context = new JSONObject("""
                    {
                        "lista": [
                            { "name": "Test-Name 1" },
                            { "name": "Test-Name 2" },
                            { "name": "Test-Name 3" }
                        ]
                    }
                    """);
        var replaceable = Replaceable.from(expression);
        Assertions.assertEquals(expected.trim(), replaceable.replace(context).trim());
    }

}
