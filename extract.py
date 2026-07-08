import os
import re
import json

directory = r'd:\FLIPPED_CLASS\DONG4\FlippedClass_Project\src\main\resources\templates'
vn_pattern = re.compile(r'[àá??ãâ?????a?????èé???ê?????ìí??iòó??õô?????o?????ùú??uu??????ı???dÀÁ??ÃÂ?????A?????ÈÉ???Ê?????ÌÍ??IÒÓ??ÕÔ?????O?????ÙÚ??UU??????İ???Ğ]')

extracted = {}

for root, _, files in os.walk(directory):
    for file in files:
        if file.endswith('.html'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                lines = f.readlines()
            
            for i, line in enumerate(lines):
                if vn_pattern.search(line):
                    # Clean up leading/trailing whitespace for dictionary key
                    cleaned = line.strip()
                    if cleaned not in extracted:
                        extracted[cleaned] = cleaned

with open('vn_strings.json', 'w', encoding='utf-8') as f:
    json.dump(extracted, f, ensure_ascii=False, indent=4)
