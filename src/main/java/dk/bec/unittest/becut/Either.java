package dk.bec.unittest.becut;

import java.util.Objects;
import java.util.function.Consumer;

public class Either<T, S> {
	private T t;
	private S s;
	
	private Either(T t, S s) {
		this.t = t;
		this.s = s;
	}
	
	public static <T, S> Either<T, S> left(T t) {
		Objects.requireNonNull(t, "left argument cannot be null");
		return new Either<>(t, null);
	}

	public static <T, S> Either<T, S> right(S s) {
		Objects.requireNonNull(s, "right argument cannot be null");
		return new Either<>(null, s);
	}
	
	public void apply(Consumer<T> left, Consumer<S> right) {
		if(t != null) {
			left.accept(t);
		} else {
			right.accept(s);
		}
	}
}
