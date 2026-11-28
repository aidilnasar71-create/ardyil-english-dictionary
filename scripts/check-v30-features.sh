#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-.}"
HTML="$ROOT/index.html"
QUEST="$ROOT/v30-quest.js"
CSS="$ROOT/v30-quest.css"
fail(){ echo "::error::ARDYIL 30.0 verification failed: $1" >&2; exit 1; }
require_text(){ grep -Fq -- "$2" "$1" || fail "Missing '$2' in $1"; }
[[ -f "$HTML" ]] || fail "index.html is missing"
[[ -f "$QUEST" ]] || fail "v30-quest.js is missing"
[[ -f "$CSS" ]] || fail "v30-quest.css is missing"
require_text "$HTML" 'ARDYIL · VERSION 30.0'
require_text "$HTML" 'id="v30-quest-hub"'
require_text "$HTML" 'id="v30-coins"'
require_text "$HTML" 'id="v30-unlock-next"'
require_text "$HTML" 'src="./v30-quest.js"'
require_text "$HTML" 'href="./v30-quest.css"'
require_text "$HTML" 'The Paper Bridge'
require_text "$HTML" 'Little Music Makers'
require_text "$HTML" 'The Cloud Library'
require_text "$HTML" 'A Map of Kindness'
require_text "$QUEST" "var KEY = 'ardyil_v30_quest_v1'"
require_text "$QUEST" 'function unlock()'
require_text "$QUEST" "addEventListener('click',unlock)"
require_text "$QUEST" 'LEVELS.length !== 30'
require_text "$QUEST" 'localStorage.setItem(KEY'
require_text "$CSS" '.v30-quest-shell'
require_text "$HTML" 'fingerstyle-guitar'
require_text "$HTML" 'bright-grand-piano'
python3 - "$HTML" "$QUEST" <<'PY'
from pathlib import Path
import re, sys
html = Path(sys.argv[1]).read_text(encoding='utf-8')
quest = Path(sys.argv[2]).read_text(encoding='utf-8')
levels = quest.split('var LEVELS = [', 1)[1].split('];', 1)[0]
assert len(re.findall(r'^\s*\[', levels, re.M)) == 30, 'expected exactly 30 learning levels'
start = html.index('var cinema=[')
end = html.index('];\nvar cinemaState', start)
cinema = html[start:end]
assert len(re.findall(r"\{title:'", cinema)) == 12, 'expected 12 original animated stories'
assert len(re.findall(r'duration:300', cinema)) >= 8, 'expected eight 5-minute animation entries'
assert 'id=\"ardyil30Mp4Player\"' in html, 'missing MP4 player'
print('ARDYIL 30.0 feature data OK: 30 levels; 12 vector animation stories; 8 animation entries; 4 five-minute offline MP4s.')
PY
python3 - "$HTML" "$QUEST" <<'PY'
from pathlib import Path
import re, subprocess, sys, tempfile
html = Path(sys.argv[1]).read_text(encoding='utf-8')
quest = Path(sys.argv[2]).read_text(encoding='utf-8')
blocks = re.findall(r'<script\b([^>]*)>([\s\S]*?)</script\s*>', html, re.I)
checked = 0
with tempfile.TemporaryDirectory() as tmp:
    for i, (attrs, body) in enumerate(blocks):
        if not body.strip() or re.search(r'type\s*=\s*["\']application/json', attrs, re.I):
            continue
        file = Path(tmp) / f'inline-{i}.js'
        file.write_text(body, encoding='utf-8')
        subprocess.run(['node', '--check', str(file)], check=True)
        checked += 1
    file = Path(tmp) / 'quest.js'
    file.write_text(quest, encoding='utf-8')
    subprocess.run(['node', '--check', str(file)], check=True)
print(f'JavaScript syntax OK: {checked} inline scripts + external quest engine.')
PY
require_text "$HTML" 'id="ardyil30Mp4Player"'
require_text "$HTML" 'the-paper-bridge.mp4'
require_text "$HTML" 'value="little-music-makers"'
require_text "$HTML" 'value="the-cloud-library"'
require_text "$HTML" 'value="a-map-of-kindness"'
require_text "$HTML" "'./media/'+this.value+'.mp4'"
require_text "$CSS" '.ardyil30-mp4-panel'
require_text "$ROOT/scripts/generate-cinema-mp4.py" 'W,H,FPS,DURATION,SR=640,360,12,300,22050'
