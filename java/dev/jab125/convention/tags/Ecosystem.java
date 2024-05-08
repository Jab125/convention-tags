package dev.jab125.convention.tags;

public record Ecosystem(String name, String serializedName) {
	public static final Ecosystem FABRIC = new Ecosystem("Fabric", "FABRIC");
	public static final Ecosystem NEOFORGE = new Ecosystem("NeoForge", "NEOFORGE");
	public static final Ecosystem COMMON = new Ecosystem("Common", "COMMON");
	public static final Ecosystem[] ECOSYSTEMS = new Ecosystem[]{FABRIC, NEOFORGE, COMMON};

	public static final Ecosystem[] ECOSYSTEMS_NO_COMMON = new Ecosystem[]{FABRIC, NEOFORGE};
}
