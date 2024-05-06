package dev.jab125.convention.tags;

import java.util.*;

public record Tag(String registryKey, String name, String[] comments, Method fabric, Method neoForge, Method convention) implements Comparable<Tag> {
	public String serialise() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("TAG\t" + registryKey + "\t" + name);
		for (String comment : comments) {
			strs.add("\tCOMMENT\t" + comment);
		}
		if (notBlank(fabric)) strs.add("\tFABRIC\t" + fabric);
		if (notBlank(neoForge)) strs.add("\tNEOFORGE\t" + neoForge);
		if (notBlank(convention)) strs.add("\tCOMMON\t" + convention);
		return String.join("\n", strs);
	}

	public String serialiseMini() {
		if (!notBlank(convention)) return null;
		ArrayList<String> strs = new ArrayList<>();
		strs.add("TAG\t" + registryKey + "\t" + name);
		for (String comment : comments) {
			strs.add("\tCOMMENT\t" + comment);
		}
		strs.add("\tCOMMON\t" + convention);
		return String.join("\n", strs);
	}

	public String getRegistryAndName() {
		return registryKey + "|" + name;
	}

	public record Method(String clazz, String method) {
		@Override
		public String toString() {
			return clazz + "\t" + method;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Method method1 = (Method) o;

			if (!Objects.equals(clazz, method1.clazz)) return false;
			return Objects.equals(method, method1.method);
		}

		@Override
		public int hashCode() {
			int result = clazz != null ? clazz.hashCode() : 0;
			result = 31 * result + (method != null ? method.hashCode() : 0);
			return result;
		}
	}

	@Override
	public int compareTo(Tag o) {
		String name = this.registryKey + "/" + this.name;
		String otherName = o.registryKey + "/" + o.name;
		return name.compareTo(otherName);
	}

	public static class TagBuilder {
		private String name;
		private String registryKey;
		private ArrayList<String> comments = new ArrayList<>();
		private Method fabric;
		private Method neoforge;
		private Method convention;

		public TagBuilder name(String registryKey, String name) {
			this.registryKey = registryKey;
			this.name = name;
			return this;
		}
		public TagBuilder fabric(String clazz, String method) {
			this.fabric = new Method(clazz, method);
			return this;
		}
		public TagBuilder neoforge(String clazz, String method) {
			this.neoforge = new Method(clazz, method);
			return this;
		}
		public TagBuilder convention(String clazz, String method) {
			this.convention = new Method(clazz, method);
			return this;
		}
		public TagBuilder comment(String comment) {
			this.comments.add(comment);
			return this;
		}

		public Tag build() {
			return new Tag(registryKey, name, comments.toArray(new String[0]), fabric, neoforge, convention);
		}

		public String getName() {
			return name;
		}

		public String getRegistryKey() {
			return registryKey;
		}

		public ArrayList<String> getComments() {
			return comments;
		}

		public Method getFabric() {
			return fabric;
		}

		public Method getNeoForge() {
			return neoforge;
		}

		public Method getConvention() {
			return convention;
		}
	}

	public static List<Tag> deserialize(String str) {
		Deserializer tagBuilder = new Deserializer(str);
		return tagBuilder.deserialize();
	}

	public static String serialize(Collection<Tag> tags) {
		ArrayList<Tag> r = new ArrayList<>(tags);
		Collections.sort(r);
		List<String> strings = new ArrayList<>();
		for (Tag tag : r) {
			strings.add(tag.serialise());
		}
		return String.join("\n", strings);
	}

	public static String serializeMini(Collection<Tag> tags) {
		ArrayList<Tag> r = new ArrayList<>(tags);
		Collections.sort(r);
		List<String> strings = new ArrayList<>();
		for (Tag tag : r) {
			String s = tag.serialiseMini();
			if (s != null) strings.add(s);
		}
		return String.join("\n", strings);
	}

	@Override
	public String toString() {
		return "Tag{" +
			   "registryKey='" + registryKey + '\'' +
			   ", name='" + name + '\'' +
			   ", comments=" + Arrays.toString(comments) +
			   ", fabric=" + fabric +
			   ", neoForge=" + neoForge +
			   ", convention=" + convention +
			   '}';
	}

	private static class Deserializer {

		private final String str;
		private TagBuilder tagBuilder;

		private Deserializer(String str) {
			this.str = str;
		}

		private List<Tag> deserialize() {
			ArrayList<Tag> tags = new ArrayList<>();
			str.lines().forEach(s -> {
				if (s.startsWith("TAG\t")) {
					if (tagBuilder != null) {
						tags.add(tagBuilder.build());
						tagBuilder = null;
					}
					tagBuilder = new TagBuilder();
					String substring = s.substring(4);
					int i = substring.indexOf("\t");
					if (i == -1) throw new RuntimeException();
					tagBuilder.name(substring.substring(0, i), substring.substring(i + 1));
				} else if (s.startsWith("\t")) {
					if (tagBuilder == null) throw new RuntimeException();
					s = s.substring(1);
					if (s.startsWith("COMMENT\t")) {
						s = s.substring(8);
						tagBuilder.comment(s);
					} else if (s.startsWith("FABRIC\t")) {
						s = s.substring(7);
						int i = s.indexOf("\t");
						if (i == -1) throw new RuntimeException();
						tagBuilder.fabric(s.substring(0, i), s.substring(i + 1));
					} else if (s.startsWith("NEOFORGE\t")) {
						s = s.substring(9);
						int i = s.indexOf("\t");
						if (i == -1) throw new RuntimeException();
						tagBuilder.neoforge(s.substring(0, i), s.substring(i + 1));
					} else if (s.startsWith("COMMON\t")) {
						s = s.substring(7);
						int i = s.indexOf("\t");
						if (i == -1) throw new RuntimeException();
						tagBuilder.convention(s.substring(0, i), s.substring(i + 1));
					}
				}
			});
			if (tagBuilder != null) tags.add(tagBuilder.build());
			Collections.sort(tags);
			return tags;
		}
	}

	private static boolean notBlank(String str) {
		return str != null && !str.isEmpty() && !str.isBlank();
	}

	private static boolean notBlank(Method method) {
		return method != null && notBlank(method.clazz) && notBlank(method.method);
	}
}
