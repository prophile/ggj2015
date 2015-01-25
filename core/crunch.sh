#!/bin/bash
IMAGE="$1"
set -e
set -v
cp "$IMAGE" /tmp/crushtmp.png
rm "$IMAGE"
pngcrush /tmp/crushtmp.png "$IMAGE"
rm /tmp/crushtmp.png

