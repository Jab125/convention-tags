package dev.jab125.convention.tags.util;

import dev.jab125.convention.tags.Ecosystem;
import dev.jab125.convention.tags.Tag;

import java.util.*;

public class TagMerger {
	public static List<Tag> merge(Collection<Tag> newList, Collection<Tag> oldList) {
		List<Tag.TagBuilder> combinedTagBuilders = new ArrayList<>();
		List<String> newTags = newList.stream().map(a -> a.registryKey() + "|" + a.name()).toList();
		List<String> oldTags = oldList.stream().map(a -> a.registryKey() + "|" + a.name()).toList();
		Set<String> mergedTags = new HashSet<>();
		mergedTags.addAll(newTags);
		mergedTags.addAll(oldTags);
		for (String mergedTag : mergedTags) {
			Optional<Tag> old = oldList.stream().filter(tag -> tag.getRegistryAndName().equals(mergedTag)).findFirst();
			Optional<Tag> nyu = newList.stream().filter(tag -> tag.getRegistryAndName().equals(mergedTag)).findFirst();
			Tag.TagBuilder tagBuilder = nyu.isPresent() ? new Tag.TagBuilder() : null;
			for (Ecosystem ecosystem : Ecosystem.ECOSYSTEMS_NO_COMMON) {
				Tag.Field oldField = old.map(a -> a.fields().get(ecosystem)).orElse(null);
				Tag.Field newField = nyu.map(a -> a.fields().get(ecosystem)).orElse(null);

				if (!Objects.equals(oldField, newField)) {
					System.out.printf(ecosystem.serializedName() + " %s -> %s%n", oldField == null ? null : (oldField.method() + " " + mergedTag), newField == null ? null : (newField.method() + " " + mergedTag));
				}
				if (nyu.isPresent()) {
					tagBuilder.field(ecosystem, newField.clazz(), newField.method());
				}
			}

			if (nyu.isPresent()) {
				nyu.get().entries().forEach((a,b) -> {
					for (Ecosystem ecosystem : b) {
						tagBuilder.entry(ecosystem, a.id(), a.required());
					}
				});
				tagBuilder.name(mergedTag.split("\\|")[0], mergedTag.split("\\|")[1]);
				if (old.isPresent()) {
					Tag tag = old.get();
					if (tag.fields().get(Ecosystem.COMMON) != null) tagBuilder.field(Ecosystem.COMMON, tag.fields().get(Ecosystem.COMMON).clazz(), tag.fields().get(Ecosystem.COMMON).method());
					if (tag.comments() != null) {
						for (String comment : tag.comments()) {
							tagBuilder.comment(comment);
						}
					}
				}
				combinedTagBuilders.add(tagBuilder);
			}
		}
		List<Tag> tags = new ArrayList<>();
		for (Tag.TagBuilder combinedTagBuilder : combinedTagBuilders) {
			tags.add(combinedTagBuilder.build());
		}
		return tags;
	}
}
