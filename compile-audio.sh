#!/bin/bash
TYPE=$1
SRC='source-audio'
AU='core/assets/audio'
if [ -z "$TYPE" ]; then
    TYPE=wav
fi
set -e
rm -rf "$AU"
mkdir -p "$AU"
set +e
case $TYPE in
    wav)
        sox --version || (echo "SoX not installed." ; exit 1)
        function compile {
            sox "$SRC/$1" -b16 -esigned-integer -r44100 -c2 "$AU/$2.wav"
        }
        ;;
    aifc)
        sox --version || (echo "SoX not installed." ; exit 1)
        which afconvert || (echo "afconvert not installed." ; exit 1)
        function compile {
            SCRATCH='/tmp/auconvert.salvage.wav'
            sox "$SRC/$1" -b24 -esigned-integer -r44100 -c2 "$SCRATCH"
            afconvert -f AIFC -d ima4 "$SCRATCH" "$AU/$2.aifc"
        }
        ;;
    aac)
        ffmpeg --version || (echo "ffmpeg not installed." ; exit 1)
        function compile {
            ffmpeg -i "$SRC/$1" "$AU/$2.aac"
        }
        ;;
    ogg)
        function compile {
            cp "$SRC/$1" "$AU/$2.ogg"
        }
        ;;
    *)
        echo "Unknown format $TYPE"
        exit 2
        ;;
esac
set -e
compile "ambience loops/wind loop ogg.ogg" night-wind
compile "sfx/new alarm.ogg" game-over
compile "sfx/robot noises/robot in distress 2.ogg" quicksand
compile "sfx/lift off softer.ogg" victory
compile "music tracks/main track 70 seconds.ogg" music
compile "sfx/engine/new engine/full rev.ogg" engine
compile "sfx/hits/eargty hit 2.ogg" hit-mine
compile "sfx/hits/water loop.ogg" hit-well
compile "sfx/hits/metal hit 1.ogg" hit-salvage
compile "sfx/menu sounds/new new robo attention.ogg" intf-send
compile "sfx/menu sounds/click 3.ogg" intf-sel
compile "sfx/roboto upgrade/electric saw spraying hammer.ogg" intf-purchase
compile "sfx/roboto upgrade/robo upgrade tada.ogg" intf-upgrade
