package dev.jab125.remapping;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {

	private Supplier<T> supplier;
	private T value;

	public Lazy(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	@Override
	public T get() {
		if (supplier != null) {
			value = supplier.get();
			supplier = null;
		}
		return value;
	}
}
