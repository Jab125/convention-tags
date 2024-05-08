package dev.jab125.convention.tags;

import java.util.*;

public record Tag(String registryKey, String name, String[] comments, Map<Ecosystem, Field> fields, Map<TagEntry, Ecosystem[]> entries) implements Comparable<Tag> {
	public String serialise() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("TAG\t" + registryKey + "\t" + name);
		for (String comment : comments) {
			strs.add("\tCOMMENT\t" + comment);
		}
		for (Ecosystem ecosystem : Ecosystem.ECOSYSTEMS) {
			if (notBlank(ecosystem)) strs.add("\t" + ecosystem.serializedName() + "\t" + fields.get(ecosystem));
		}
		ArrayList<TagEntry> tagEntries = new ArrayList<>(entries.keySet());
		Collections.sort(tagEntries);
		for (TagEntry entry : tagEntries) {
			Ecosystem[] ecosystems = entries.get(entry);
			strs.add("\tENTRY\t" + entry.id + "\t" + entry.required() + "\t" + String.join("\t", Arrays.stream(ecosystems).map(Ecosystem::serializedName).toList()));
		}
		return String.join("\n", strs);
	}

	public String serialiseMini() {
		if (!notBlank(Ecosystem.COMMON)) return null;
		ArrayList<String> strs = new ArrayList<>();
		strs.add("TAG\t" + registryKey + "\t" + name);
		for (String comment : comments) {
			strs.add("\tCOMMENT\t" + comment);
		}
		strs.add("\tCOMMON\t" + fields.get(Ecosystem.COMMON));
		return String.join("\n", strs);
	}

	public String getRegistryAndName() {
		return registryKey + "|" + name;
	}

	public record Field(String clazz, String method) {
		@Override
		public String toString() {
			return clazz + "\t" + method;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Field field1 = (Field) o;

			if (!Objects.equals(clazz, field1.clazz)) return false;
			return Objects.equals(method, field1.method);
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
		private Map<Ecosystem, Field> fieldMap = new HashMap<>();
		private Map<TagEntry, List<Ecosystem>> entries = new HashMap<>();

		public TagBuilder name(String registryKey, String name) {
			this.registryKey = registryKey;
			this.name = name;
			return this;
		}
		public TagBuilder field(Ecosystem ecosystem, String clazz, String method) {
			this.fieldMap.put(ecosystem, new Field(clazz, method));
			return this;
		}

		public TagBuilder comment(String comment) {
			this.comments.add(comment);
			return this;
		}

		public TagBuilder entry(Ecosystem ecosystem, String name, boolean required) {
			entries.computeIfAbsent(new TagEntry(name, required), a -> new ArrayList<>()).add(ecosystem);
			return this;
		}

		public Tag build() {
			Map<TagEntry, Ecosystem[]> newMap = new HashMap<>();
			for (Map.Entry<TagEntry, List<Ecosystem>> tagEntryListEntry : entries.entrySet()) {
				newMap.put(tagEntryListEntry.getKey(), tagEntryListEntry.getValue().toArray(new Ecosystem[0]));
			}
			return new Tag(registryKey, name, comments.toArray(new String[0]), fieldMap, newMap);
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

		public Map<Ecosystem, Field> getFieldMap() {
			return fieldMap;
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
			   ", fieldMap=" + fields +
			   ", entries=" + entries +
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
					} else if (s.startsWith("ENTRY\t")) {
						s = s.substring(6);
						int i = s.indexOf("\t");
						String tagName = s.substring(0, i);
						s = s.substring(i + 1);
						i = s.indexOf("\t");
						boolean optional = Boolean.parseBoolean(s.substring(0, i));
						s = s.substring(i + 1);
						while(true) {
							i = s.indexOf("\t");
							int d = i;
							if (i == -1) d = s.length();
							String ecosystem = s.substring(0, d);
							if (i != -1) s = s.substring(d + 1);
							tagBuilder.entry(Arrays.stream(Ecosystem.ECOSYSTEMS).filter(a -> a.serializedName().equals(ecosystem)).findFirst().orElseThrow(), tagName, optional);
							if (i == -1) return;
						}
					} else {
						int i = s.indexOf("\t");
						String serializedName = s.substring(0, i);
						Optional<Ecosystem> ecosystem = Arrays.stream(Ecosystem.ECOSYSTEMS).filter(a -> a.serializedName().equals(serializedName)).findFirst();
						if (ecosystem.isEmpty()) return;
						s = s.substring(serializedName.length() + 1);
						i = s.indexOf("\t");
						if (i == -1) throw new RuntimeException();
						tagBuilder.field(ecosystem.get(), s.substring(0, i), s.substring(i + 1));
					}
				}
			});
			if (tagBuilder != null) tags.add(tagBuilder.build());
			Collections.sort(tags);
			return tags;
		}
	}

	private boolean notBlank(Ecosystem ecosystem) {
		if (!fields().containsKey(ecosystem)) return false;
		return notBlank(fields().get(ecosystem));
	}

	private static boolean notBlank(String str) {
		return str != null && !str.isEmpty() && !str.isBlank();
	}

	private static boolean notBlank(Field field) {
		return field != null && notBlank(field.clazz) && notBlank(field.method);
	}

	public record TagEntry(String id, boolean required) implements Comparable<TagEntry> {
		public boolean isTag() {
			return id.startsWith("#");
		}

		@Override
		public int compareTo(TagEntry o) {
			String id = this.id + "/" + this.id;
			String otherId = o.id + "/" + o.id;
			return id.compareTo(otherId);
		}
	}
}
