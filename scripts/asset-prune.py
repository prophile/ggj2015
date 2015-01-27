import os
import os.path
import sys
import re
from subprocess import check_call

ASSET_DIR = 'core/assets/'
# special-cases: assets that are known used but not loaded through the asset manager
EXTRA_ASSETS = ['text.vert',
                'text.frag',
                'nodes.txt',
                'constants.txt']

DIAG_LINE = re.compile(r'^(.*),\s*(\w+),\s*.*$')

all_assets = set()
used_assets = set()

for line in sys.stdin:
    match = DIAG_LINE.match(line.strip())
    if match is not None:
        ty = match.group(2)
        name = match.group(1)
        used_assets.add(os.path.join(ASSET_DIR, name))

for case in EXTRA_ASSETS:
    used_assets.add(os.path.join(ASSET_DIR, case))

for a, b, c in os.walk(ASSET_DIR):
    for entry in c:
        path = os.path.join(a, entry)
        all_assets.add(path)

missing = used_assets - all_assets
if missing:
    print('FATAL: MISSING ASSETS')
    for asset in missing:
        print(asset)
    exit(1)

unused = all_assets - used_assets
for asset in unused:
    print('Unused: {}'.format(asset))
    check_call(('git', 'rm', asset))

