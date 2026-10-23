import argparse
import os
import re
import sys
import zipfile
from collections import defaultdict

TEXT_EXTENSIONS = {".json", ".json5", ".toml", ".snbt", ".js", ".txt", ".cfg", ".properties", ".yml", ".yaml", ".zs", ".xml", ".mcmeta"}
JAR_TEXT_EXTENSIONS = (".json", ".js", ".toml", ".cfg", ".snbt", ".txt")
SKIPPED_FOLDERS = {".git", "mods", "shaderpacks", "resourcepacks", "bin", "tacz"}
GENERATED_NOISE = ("inventory-particles/cache", "structurify/structurify_backup", "structurify_dump", "structure_cache")
REFERENCE = re.compile(r"bloodyhell:([a-z0-9_.]+)(?![a-z0-9_./])")
REFERENCE_BYTES = re.compile(rb"bloodyhell:([a-z0-9_.]+)(?![a-z0-9_./])")


def load_registered_ids(dump_path):
    registered = set()
    with open(dump_path, encoding="utf-8") as dump:
        for line in dump:
            registry, path = line.rstrip("\n").split("\t")
            registered.add(path)
    return registered


def scan_pack_files(pack_dir):
    found = defaultdict(set)
    for folder, subfolders, files in os.walk(pack_dir):
        subfolders[:] = [name for name in subfolders if name not in SKIPPED_FOLDERS]
        for name in files:
            if os.path.splitext(name)[1].lower() not in TEXT_EXTENSIONS:
                continue
            path = os.path.join(folder, name)
            relative = os.path.relpath(path, pack_dir).replace("\\", "/")
            if any(noise in relative for noise in GENERATED_NOISE) or os.path.getsize(path) > 30_000_000:
                continue
            with open(path, encoding="utf-8", errors="ignore") as handle:
                for match in REFERENCE.finditer(handle.read()):
                    found[match.group(1)].add(relative)
    return found


def scan_pack_mods(mods_dir):
    found = defaultdict(set)
    if not os.path.isdir(mods_dir):
        return found
    for jar_name in sorted(os.listdir(mods_dir)):
        if not jar_name.endswith(".jar") or jar_name.startswith("bloodyhell"):
            continue
        try:
            jar = zipfile.ZipFile(os.path.join(mods_dir, jar_name))
        except zipfile.BadZipFile:
            continue
        for entry in jar.namelist():
            if not entry.endswith(JAR_TEXT_EXTENSIONS) or jar.getinfo(entry).file_size > 5_000_000:
                continue
            data = jar.read(entry)
            if b"bloodyhell:" not in data:
                continue
            for match in REFERENCE_BYTES.finditer(data):
                found[match.group(1).decode()].add(jar_name + "!" + entry)
    return found


def report(title, found, registered):
    missing = {identifier: places for identifier, places in found.items() if identifier not in registered}
    print(f"{title}: {len(found)} ids referenced, {len(missing)} not registered")
    for identifier in sorted(missing):
        print(f"  bloodyhell:{identifier}")
        for place in sorted(missing[identifier])[:4]:
            print(f"      {place}")
    return len(missing)


def main():
    parser = argparse.ArgumentParser(description="Check the bloodyhell ids used by a modpack against the ids the mod registers.")
    parser.add_argument("--pack", required=True, help="folder of the modpack (config, kubejs, mods...)")
    parser.add_argument("--dump", default="run/bh-registry-ids.txt", help="file written by RegistryDumpGameTests")
    arguments = parser.parse_args()
    registered = load_registered_ids(arguments.dump)
    problems = report("pack files", scan_pack_files(arguments.pack), registered)
    problems += report("data inside other mods", scan_pack_mods(os.path.join(arguments.pack, "mods")), registered)
    sys.exit(1 if problems else 0)


if __name__ == "__main__":
    main()
