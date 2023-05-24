package replaceable.parser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

/**
 * A shard is a piece of the template. Can be just a piece of text or can be a piece of replace,
 * as a simple replace or the start of a for-loop in replace
 */
class Shard {

    static class Stream {
        private final List<Shard> shards;
        private int index = 0;

        public Stream(List<Shard> shards) {
            this.shards = shards;
        }

        public boolean hasNext() {
            return shards.size() > index;
        }

        public Shard next() {
            return hasNext() ? shards.get(index++) : null;
        }
    }

    enum Kind {
        SIMPLE_REPLACE,
        OPEN_FOR,
        END_FOR,
        TEXT,
        OPEN_IF,
        ELSE_IF,
        ELSE,
        END_IF
    }

    final Kind kind;
    final MatchResult match;
    final String text;

    public Shard(Kind kind, MatchResult match) {
        this.kind = kind;
        this.match = match;
        this.text = null;
    }

    public Shard(String text) {
        this.kind = Kind.TEXT;
        this.match = null;
        this.text = text;
    }

    /** This method returns all 'replace shards' of template. So this method returns all shards except the 'text shards' */
    private static List<Shard> replaceShardsFrom(String template) {
        var shards = new ArrayList<Shard>();
        Matcher matcher;

        matcher = RegExs.SIMPLE_REPLACE.matcher(template);
        while (matcher.find()) {
            var match = matcher.toMatchResult();
            if (match.group().matches(RegExs.END_FOR.pattern())) continue;
            if (match.group().matches(RegExs.ELSE.pattern())) continue;
            if (match.group().matches(RegExs.END_IF.pattern())) continue;
            shards.add(new Shard(Kind.SIMPLE_REPLACE, match));
        }

        matcher = RegExs.OPEN_FOR.matcher(template);
        while (matcher.find()) shards.add(new Shard(Kind.OPEN_FOR, matcher.toMatchResult()));

        matcher = RegExs.END_FOR.matcher(template);
        while (matcher.find()) shards.add(new Shard(Kind.END_FOR, matcher.toMatchResult()));

        matcher = RegExs.OPEN_IF.matcher(template);
        while (matcher.find()) shards.add(new Shard(Kind.OPEN_IF, matcher.toMatchResult()));

        matcher = RegExs.ELSE_IF.matcher(template);
        while (matcher.find()) shards.add(new Shard(Kind.ELSE_IF, matcher.toMatchResult()));

        matcher = RegExs.ELSE.matcher(template);
        while (matcher.find()) shards.add(new Shard(Kind.ELSE, matcher.toMatchResult()));

        matcher = RegExs.END_IF.matcher(template);
        while (matcher.find()) shards.add(new Shard(Kind.END_IF, matcher.toMatchResult()));

        shards.sort(Comparator.comparingInt(a -> a.match.start()));
        return shards;
    }

    /** This method use the 'replace shards' of template to calculate the 'text shards' and return all shards of template */
    private static List<Shard> allShardsFrom(String template, List<Shard> replaceShards) {
        int lastEndIndex = 0;
        var newShards = new ArrayList<Shard>();
        for (var shard: replaceShards) {
            if (shard.match.start() != lastEndIndex) {
                var text = template.substring(lastEndIndex, shard.match.start());
                newShards.add(new Shard(text));
            }
            lastEndIndex = shard.match.end();
            newShards.add(shard);
        }
        var lastShard = replaceShards.size() != 0 ? replaceShards.get(replaceShards.size()-1) : null;
        if (lastShard != null && lastShard.match.end() != template.length() - 1 && lastShard.match.end() < template.length() - 1) {
            var text = template.substring(lastShard.match.end(), template.length() - 1);
            newShards.add(new Shard(text));
        }
        return newShards;
    }

    /** This method will return all shards of template */
    public static List<Shard> shardsFrom(String template) {
        return allShardsFrom(template, replaceShardsFrom(template));
    }

    /** Returns a Text Shard based on this Shard */
    public Shard toText() {
        if (this.kind == Kind.TEXT) return this;
        return new Shard(this.match.group());
    }

}
