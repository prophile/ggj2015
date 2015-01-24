#!/bin/sh
function dl {
    SRC=$1
    TGT=$2
    if [ -f "$TGT" ]; then
        curl --location -z "$TGT" "$SRC" -o "$TGT"
    else
        curl --location "$SRC" -o "$TGT"
    fi
}
set -e
mkdir -p jres
dl 'http://bit.ly/packrgdx' jres/packr.jar
dl 'https://bitbucket.org/alexkasko/openjdk-unofficial-builds/downloads/openjdk-1.7.0-u60-unofficial-windows-i586-image.zip' jres/win32.zip
dl 'https://bitbucket.org/alexkasko/openjdk-unofficial-builds/downloads/openjdk-1.7.0-u60-unofficial-macosx-x86_64-image.zip' jres/mac64.zip

