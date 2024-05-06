package dev.jab125.convention.tags;

import java.io.Closeable;
import java.io.IOException;

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
