const fs = require('fs');
const path = require('path');

const dir = 'src/main/resources/templates';
const vnPattern = /[àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ]/;

const extracted = {};

function walk(directory) {
    const files = fs.readdirSync(directory);
    for (const file of files) {
        const filepath = path.join(directory, file);
        if (fs.statSync(filepath).isDirectory()) {
            walk(filepath);
        } else if (filepath.endsWith('.html')) {
            const content = fs.readFileSync(filepath, 'utf8');
            const lines = content.split('\n');
            for (let i = 0; i < lines.length; i++) {
                if (vnPattern.test(lines[i])) {
                    const cleaned = lines[i].trim();
                    if (!extracted[cleaned] && cleaned.length > 0) {
                        extracted[cleaned] = cleaned;
                    }
                }
            }
        }
    }
}

walk(dir);
fs.writeFileSync('vn_strings.json', JSON.stringify(extracted, null, 2), 'utf8');
console.log('Extracted ' + Object.keys(extracted).length + ' unique lines.');