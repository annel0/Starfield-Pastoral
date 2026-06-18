const fs = require('fs');
const data = JSON.parse(fs.readFileSync('源文件/Content/Data/Shops.json', 'utf8'));
console.log('Main keys:', Object.keys(data).slice(0, 5));
const firstKey = Object.keys(data)[0];
console.log('First shop keys:', Object.keys(data[firstKey]));
if (data[firstKey].Items) {
    console.log('Found "Items" instead of "Entries"');
}
