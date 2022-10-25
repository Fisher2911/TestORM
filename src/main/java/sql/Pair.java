package sql;

public record Pair<T, Z>(T first, Z second) {

    public static <T,Z> Pair<T, Z> of(T first, Z second) {
        return new Pair<>(first, second);
    }

}
