#!/usr/bin/env bash
set -euo pipefail
PROJECT_DIR="${1:-}"
[[ -n "$PROJECT_DIR" && -d "$PROJECT_DIR" ]] || { echo "Usage: $0 <extracted-android-project-dir>" >&2; exit 2; }

python3 - "$PROJECT_DIR" <<'PY'
from pathlib import Path
import re, sys

root = Path(sys.argv[1])
gradle_files = list(root.rglob("*.gradle")) + list(root.rglob("*.gradle.kts")) + list(root.rglob("*.toml"))
if not gradle_files:
    raise SystemExit("No Gradle configuration files found.")

agp_changed = False
compile_changed = False
target_changed = False
version_code_changed = False
version_name_changed = False
for path in gradle_files:
    try:
        text = path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        continue
    original = text
    # Android 16 requires AGP 8.9.1 or newer; the project starts on AGP 8.7.3.
    text, n = re.subn(r"(?<![0-9])8\.7\.3(?![0-9])", "8.9.1", text)
    agp_changed = agp_changed or n > 0
    # Move compile and target SDK to API 36 while retaining minSdk for Android 13 and older.
    text, n = re.subn(r"(?m)(\bcompileSdk(?:Version)?\s*(?:=\s*)?)35\b", r"\g<1>36", text)
    compile_changed = compile_changed or n > 0
    text, n = re.subn(r"(?m)(\btargetSdk(?:Version)?\s*(?:=\s*)?)35\b", r"\g<1>36", text)
    target_changed = target_changed or n > 0
    text, n = re.subn(r"(?m)(\bbuildToolsVersion\s*(?:=\s*)?['\"])35\.0\.0(['\"])", r"\g<1>36.0.0\2", text)
    text, n = re.subn(r"(?m)(\bversionCode\s*(?:=\s*)?)\d+", r"\g<1>2000", text)
    version_code_changed = version_code_changed or n > 0
    text, n = re.subn(r"(?m)(\bversionName\s*(?:=\s*)?['\"])[^'\"]+(['\"])", r"\g<1>20.0\2", text)
    version_name_changed = version_name_changed or n > 0
    if text != original:
        path.write_text(text, encoding="utf-8")

missing=[]
if not compile_changed: missing.append("compileSdk 35 -> 36")
if not target_changed: missing.append("targetSdk 35 -> 36")
if not version_code_changed: missing.append("versionCode -> 3000")
if not version_name_changed: missing.append("versionName -> 30.0")
if not agp_changed:
    # Some project templates may already be using a newer AGP. Verify rather than silently downgrading.
    combined = "\n".join(p.read_text(encoding="utf-8", errors="ignore") for p in gradle_files)
    if not re.search(r"8\.(?:9\.[1-9]|[1-9][0-9]\.)", combined):
        missing.append("Android Gradle Plugin 8.9.1+")
if missing:
    raise SystemExit("Play Store preparation could not safely patch: " + ", ".join(missing))
print("ARDYIL 20.0 Android build configured: AGP >= 8.9.1, compileSdk 36, targetSdk 36, versionCode 3000, versionName 30.0.")
PY
