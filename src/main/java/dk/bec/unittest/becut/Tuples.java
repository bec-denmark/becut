package dk.bec.unittest.becut;

import java.util.Objects;

public class Tuples {
	public static class Tuple2<T, U> {
		private final T t;
		private final U u;
		Tuple2(T t, U u) {
			Objects.requireNonNull(t, "First element of the tuple cannot be null.");
			Objects.requireNonNull(u, "Second element of the tuple cannot be null.");
			this.t = t;
			this.u = u;
		}
		
		public T _1() {
			return t;
		}

		public U _2() {
			return u;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(t, u);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (!(obj instanceof Tuple2)) return false;
			return Objects.equals(t, ((Tuple2)obj)._1()) && Objects.equals(u, ((Tuple2)obj)._2());
		}
		
		@Override
		public String toString() {
			return String.format("(%s, %s)", t, u);
		}
	}
	
	public static <T, U> Tuple2<T, U> of(T t, U u) {
		return new Tuple2<T, U>(t, u);
	}
}
