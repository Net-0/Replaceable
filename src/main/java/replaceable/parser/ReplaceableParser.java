package replaceable.parser;

import replaceable.ForReplaceable;
import replaceable.IfReplaceable;
import replaceable.Replaceable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** A parser of Replaceables */
public class ReplaceableParser {

    /** Parse a template string to a ReplaceableString */
    public static Replaceable parse(String template) throws IOException {
        var shards = Shard.shardsFrom(template);
        return replaceableFrom(new Shard.Stream(shards));
    }

    private static Replaceable replaceableFrom(Shard.Stream stream) throws IOException {
        Fragment fragment = new Fragment();

        while (stream.hasNext()) {
            Shard shard = stream.next();
            if (shard.kind == Shard.Kind.END_FOR) fragment.addShard(shard);
            else if (shard.kind == Shard.Kind.OPEN_FOR) fragment.addReplaceable(forReplaceableFrom(shard, stream));
            else if (shard.kind == Shard.Kind.OPEN_IF) fragment.addReplaceable(ifReplaceableFrom(shard, stream));
            else fragment.addShard(shard);
        }

        return Fragment.replaceableFrom(fragment);
    }

    private static ForReplaceable forReplaceableFrom(Shard openForShard, Shard.Stream stream) throws IOException {
        Fragment fragment = new Fragment();
        boolean foundEndFor = false;

        while (stream.hasNext()) {
            Shard shard = stream.next();
            if (shard.kind == Shard.Kind.END_FOR) { foundEndFor = true; break; }
            else if (shard.kind == Shard.Kind.OPEN_FOR) fragment.addReplaceable(forReplaceableFrom(shard, stream));
            else if (shard.kind == Shard.Kind.OPEN_IF) fragment.addReplaceable(ifReplaceableFrom(shard, stream));
            else fragment.addShard(shard);
        }
        if (!foundEndFor) throw new IOException("Hasn't a #endfor# to this for: " + openForShard.match.group());

        return Fragment.forReplaceableFrom(openForShard, fragment);
    }

    private static IfReplaceable ifReplaceableFrom(Shard openIfShard, Shard.Stream stream) throws IOException {
        var fragment = new Fragment();
        var conditionMatch = openIfShard.match;
        var conditionKind = Shard.Kind.OPEN_IF;
        List<IfReplaceable.ConditionalReplaceable> conditionalsReplaceable = new ArrayList<>();
        boolean foundEndIf = false;

        while (stream.hasNext()) {
            Shard shard = stream.next();
            if (shard.kind == Shard.Kind.OPEN_FOR) fragment.addReplaceable(forReplaceableFrom(shard, stream));
            else if (shard.kind == Shard.Kind.OPEN_IF) fragment.addReplaceable(ifReplaceableFrom(shard, stream));
            else if (shard.kind == Shard.Kind.ELSE_IF && conditionKind != Shard.Kind.ELSE) {
                var conditionGroup = conditionKind == Shard.Kind.OPEN_IF ? RegExs.GROUPS.OPEN_IF.CONDITION : RegExs.GROUPS.ELSE_IF.CONDITION;
                conditionalsReplaceable.add(ConditionalParser.replaceableOf(conditionMatch.group(conditionGroup), fragment));
                fragment = new Fragment();
                conditionMatch = shard.match;
                conditionKind = Shard.Kind.ELSE_IF;
            }
            else if (shard.kind == Shard.Kind.ELSE) {
                var conditionGroup = conditionKind == Shard.Kind.OPEN_IF ? RegExs.GROUPS.OPEN_IF.CONDITION : RegExs.GROUPS.ELSE_IF.CONDITION;
                conditionalsReplaceable.add(ConditionalParser.replaceableOf(conditionMatch.group(conditionGroup), fragment));
                fragment = new Fragment();
                conditionMatch = shard.match;
                conditionKind = Shard.Kind.ELSE;
            }
            else if (shard.kind == Shard.Kind.END_IF) {
                if (conditionKind == Shard.Kind.ELSE) conditionalsReplaceable.add(ConditionalParser.elseOf(fragment));
                else {
                    var conditionGroup = conditionKind == Shard.Kind.OPEN_IF ? RegExs.GROUPS.OPEN_IF.CONDITION : RegExs.GROUPS.ELSE_IF.CONDITION;
                    conditionalsReplaceable.add(ConditionalParser.replaceableOf(conditionMatch.group(conditionGroup), fragment));
                }
                foundEndIf = true;
                break;
            }
            else fragment.addShard(shard);
        }
        if (!foundEndIf) throw new IOException("Hasn't a #endif# to this if: " + openIfShard.match.group());

        return new IfReplaceable(conditionalsReplaceable);
    }

}


