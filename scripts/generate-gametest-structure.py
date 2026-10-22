import gzip
import struct
import sys
from pathlib import Path

SIZE = (9, 6, 9)
OUTPUT = Path(sys.argv[1] if len(sys.argv) > 1 else "src/devtools/resources/data/bloodyhell/structures/empty.nbt")


def name(value):
    raw = value.encode("utf-8")
    return struct.pack(">H", len(raw)) + raw


def tag_int(key, value):
    return b"\x03" + name(key) + struct.pack(">i", value)


def tag_string(key, value):
    raw = value.encode("utf-8")
    return b"\x08" + name(key) + struct.pack(">H", len(raw)) + raw


def tag_int_list(key, values):
    return b"\x09" + name(key) + b"\x03" + struct.pack(">i", len(values)) + b"".join(struct.pack(">i", v) for v in values)


def compound(parts):
    return b"".join(parts) + b"\x00"


def tag_compound_list(key, items):
    return b"\x09" + name(key) + b"\x0a" + struct.pack(">i", len(items)) + b"".join(compound(item) for item in items)


palette = [[tag_string("Name", "minecraft:smooth_stone")]]
blocks = [[tag_int_list("pos", [x, 0, z]), tag_int("state", 0)] for x in range(SIZE[0]) for z in range(SIZE[2])]
root = compound([
    tag_int_list("size", list(SIZE)),
    tag_compound_list("entities", []),
    tag_compound_list("blocks", blocks),
    tag_compound_list("palette", palette),
    tag_int("DataVersion", 3465),
])
OUTPUT.parent.mkdir(parents=True, exist_ok=True)
OUTPUT.write_bytes(gzip.compress(b"\x0a" + name("") + root))
