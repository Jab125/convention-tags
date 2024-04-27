// Very cursed JavaScript
HTMLElement.prototype.getElementById = function(id) {
    b = this.getElementsByTagName("*");
    for (i in Array.from(b)) {
        if (b[i].id == id) return b[i];
    }
}
const tagElements = Array.from(document.getElementsByClassName("tag"));
for (i in tagElements) {
	const element = tagElements[i];
	const autofillButton = element.getElementById("autofill-class");
	autofillButton.onclick = () => {
	    const fabric = element.getElementById("Fabric-class").innerText;
	    const neoforge = element.getElementById("NeoForge-class").innerText;
	    if ((!fabric || fabric == "ConventionalBlockTags") && (!neoforge || neoforge == "Blocks")) {
	    	element.getElementById("architectury-class").value = "dev.architectury.tags.BlockTags";
	    } else if ((!fabric || fabric == "ConventionalEnchantmentTags") && (!neoforge || neoforge == "Enchantments")) {
            element.getElementById("architectury-class").value = "dev.architectury.tags.EnchantmentTags";
        } else if ((!fabric || fabric == "ConventionalEntityTypeTags") && (!neoforge || neoforge == "EntityTypes")) {
            element.getElementById("architectury-class").value = "dev.architectury.tags.EntityTypeTags";
        } else if ((!fabric || fabric == "ConventionalFluidTags") && (!neoforge || neoforge == "Fluids")) {
            element.getElementById("architectury-class").value = "dev.architectury.tags.FluidTags";
        } else if ((!fabric || fabric == "ConventionalItemTags") && (!neoforge || neoforge == "Items")) {
            element.getElementById("architectury-class").value = "dev.architectury.tags.ItemTags";
        } else if ((!fabric || fabric == "ConventionalBiomeTags") && (!neoforge || neoforge == "Biomes")) {
            element.getElementById("architectury-class").value = "dev.architectury.tags.BiomeTags";
        } else if ((!fabric || fabric == "ConventionalStructureTags") && (!neoforge || neoforge == "Structures")) {
            element.getElementById("architectury-class").value = "dev.architectury.tags.StructureTags";
        }
	}

	const autofillFabric = element.getElementById("autofill-fabric");
	autofillFabric.onclick = () => {
	    element.getElementById("architectury-field").value = element.getElementById("Fabric-field").innerText;
	}

	const autofillNeoforge = element.getElementById("autofill-neoforge");
    autofillNeoforge.onclick = () => {
    	element.getElementById("architectury-field").value = element.getElementById("NeoForge-field").innerText;
    }
}

document.getElementById("save").onclick = () => {
	const json = {};
	for (i in tagElements) {
		const element = tagElements[i];
		const type = element.getElementById("tag-type").innerText;
		if (!json[type]) json[type] = {};
		json[type][element.getElementById("tag-id").innerText] = {"class": element.getElementById("architectury-class").value, "field": element.getElementById("architectury-field").value, "javadoc": element.getElementById("javadoc").value }
	}
    fetch("/save", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(json)
    }).then(res => {
        alert("Saved!");
    });
}