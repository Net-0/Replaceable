package replaceable.parser;

import replaceable.*;
import replaceable.parser.RegExs.GROUPS;
import java.util.ArrayList;
import java.util.List;

/** A Fragment represent a scope of shards, as a meta-data to {@link Replaceable}, {@link ForReplaceable} or {@link IfReplaceable}.
 * With a base-template and sub-replaceables */
class Fragment {
    private final List<IReplaceable> toReplace = new ArrayList<>();
    private final StringBuilder template = new StringBuilder();

    /** Add a shard into the fragment */
    void addShard(Shard shard) {
        if (shard.kind == Shard.Kind.SIMPLE_REPLACE) this.addReplaceable(Fragment.simpleReplaceFrom(shard));
        else this.template.append(shard.toText().text);
    }

    /** Add a replaceable in the fragment */
    void addReplaceable(IReplaceable replaceable) {
        toReplace.add(replaceable);
        template.append("%s");
    }

    static Replaceable replaceableFrom(Fragment fragment) {
        return new Replaceable(fragment.template.toString(), fragment.toReplace);
    }

    /** Create a {@link SimpleReplaceable} from a Shard Replace of Simple Replace */
    static SimpleReplaceable simpleReplaceFrom(Shard shard) {
        return new SimpleReplaceable(shard.match.group(GROUPS.SIMPLE_REPLACE.MEMBER_PATH));
    }

    /** Create a {@link ForReplaceable} from a Shard Replace of Open For of For Replace and a {@link Fragment} that represents the for-body */
    static ForReplaceable forReplaceableFrom(Shard openForShard, Fragment bodyFragment) {
        return new ForReplaceable(
                openForShard.match.group(GROUPS.OPEN_FOR.VALUE),
                openForShard.match.group(GROUPS.OPEN_FOR.KEY),
                openForShard.match.group(GROUPS.OPEN_FOR.HANDLER_EXPRESSION),
                Fragment.replaceableFrom(bodyFragment)
        );
    }
}
