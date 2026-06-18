const fs = require('fs');
const path = require('path');

const tables = [
    'src/main/resources/assets/stardewcraft/blockstates/birch_table.json',
    'src/main/resources/assets/stardewcraft/blockstates/oak_table.json',
    'src/main/resources/assets/stardewcraft/blockstates/spruce_table.json'
];

let success = true;

tables.forEach(file => {
    if (!fs.existsSync(file)) {
        console.error(`Missing file: ${file}`);
        success = false;
        return;
    }
    const content = fs.readFileSync(file, 'utf8');

    // 1. Old references should be 0
    const old1 = (content.match(/tablecloth_floral_inner_corner/g) || []).length;
    const old2 = (content.match(/tablecloth_blank_inner_corner/g) || []).length;
    if (old1 > 0 || old2 > 0) {
        console.error(`${file}: Found old references (floral: ${old1}, blank: ${old2})`);
        success = false;
    }

    // 2. 24 edge_1_e_inner_corner/edge_2_ne_inner_corner specialized references
    const new1 = (content.match(/edge_1_e_inner_corner/g) || []).length;
    const new2 = (content.match(/edge_2_ne_inner_corner/g) || []).length;
    const totalNew = new1 + new2;
    if (totalNew !== 24) {
        console.error(`${file}: Expected 24 new inner corner references, found ${totalNew} (e1: ${new1}, e2: ${new2})`);
        success = false;
    }

    // 3. Model existence
    const models = content.match(/"model":\s*"([^"]+)"/g);
    if (models) {
        models.forEach(m => {
            const modelPath = m.match(/"model":\s*"([^"]+)"/)[1];
            if (modelPath.includes('tablecloth')) {
                const [namespace, name] = modelPath.split(':');
                const relPath = `src/main/resources/assets/${namespace || 'minecraft'}/models/${name}.json`;
                if (!fs.existsSync(relPath)) {
                    console.error(`${file}: Model not found: ${relPath}`);
                    success = false;
                } else {
                    // Check textures in this model
                    const modelContent = fs.readFileSync(relPath, 'utf8');
                    const textures = modelContent.match(/"[a-zA-Z0-9_]+":\s*"([^"]+)"/g);
                    if (textures) {
                        textures.forEach(t => {
                            const texMatch = t.match(/"[a-zA-Z0-9_]+":\s*"([^"]+)"/);
                            if (texMatch) {
                                const texPath = texMatch[1];
                                if (texPath.includes('tablecloth')) {
                                    const [tNamespace, tName] = texPath.split(':');
                                    const tRelPath = `src/main/resources/assets/${tNamespace || 'minecraft'}/textures/${tName}.png`;
                                    if (!fs.existsSync(tRelPath)) {
                                        console.error(`${relPath}: Texture not found: ${tRelPath}`);
                                        success = false;
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
});

if (success) {
    console.log("Validation passed!");
    process.exit(0);
} else {
    process.exit(1);
}
