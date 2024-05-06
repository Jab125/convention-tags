package dev.jab125.convention.tags.util;

import dev.jab125.convention.tags.Tag;

import java.util.*;

public class TagMerger {
	public static Collection<Tag> merge(Collection<Tag> newList, Collection<Tag> oldList) {
		List<Tag.TagBuilder> combinedTagBuilders = new ArrayList<>();
		List<String> newTags = newList.stream().map(a -> a.registryKey() + "|" + a.name()).toList();
		List<String> oldTags = oldList.stream().map(a -> a.registryKey() + "|" + a.name()).toList();
		Set<String> mergedTags = new HashSet<>();
		mergedTags.addAll(newTags);
		mergedTags.addAll(oldTags);
		for (String mergedTag : mergedTags) {
			Optional<Tag> old = oldList.stream().filter(tag -> tag.getRegistryAndName().equals(mergedTag)).findFirst();
			Optional<Tag> nyu = newList.stream().filter(tag -> tag.getRegistryAndName().equals(mergedTag)).findFirst();
			Tag.Method oldFabric = old.map(Tag::fabric).orElse(null);
			Tag.Method oldNeoForge = old.map(Tag::neoForge).orElse(null);

			Tag.Method newFabric = nyu.map(Tag::fabric).orElse(null);
			Tag.Method newNeoForge = nyu.map(Tag::neoForge).orElse(null);
			if (!Objects.equals(oldFabric, newFabric)) {
				System.out.printf("Fabric %s -> %s%n", oldFabric == null ? null : (oldFabric.method() + " " + mergedTag), newFabric == null ? null : (newFabric.method() + " " + mergedTag));
			}

			if (!Objects.equals(oldNeoForge, newNeoForge)) {
				System.out.printf("NeoForge %s -> %s%n", oldNeoForge == null ? null : (oldNeoForge.method() + " " + mergedTag), newNeoForge == null ? null : (newNeoForge.method() + " " + mergedTag));
			}
			if (nyu.isPresent()) {
				Tag.TagBuilder tagBuilder = new Tag.TagBuilder();
				tagBuilder.name(mergedTag.split("\\|")[0], mergedTag.split("\\|")[1]);
				if (newFabric != null) tagBuilder.fabric(newFabric.clazz(), newFabric.method());
				if (newNeoForge != null) tagBuilder.neoforge(newNeoForge.clazz(), newNeoForge.method());
				if (old.isPresent()) {
					Tag tag = old.get();
					if (tag.convention() != null) tagBuilder.convention(tag.convention().clazz(), tag.convention().method());
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
