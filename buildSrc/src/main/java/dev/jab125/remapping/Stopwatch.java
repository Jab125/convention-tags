package dev.jab125.remapping;

import java.io.Closeable;

public class Stopwatch implements Closeable {
	private final long startTime = System.currentTimeMillis();
	private final String name;

	public Stopwatch(String name) {
		this.name = name;
	}
	@Override
	public void close() {
		System.out.printf("%s: Took %s ms%n", name, System.currentTimeMillis() - startTime);
	}
}
