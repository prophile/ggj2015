#!/bin/bash
set -e
mkdir -p distributions/final
mkdir -p distributions/windows
mkdir -p distributions/mac
java -jar jres/packr.jar \
     -platform windows \
     -jdk 'jres/win32.zip' \
     -executable robogame \
     -appjar desktop/build/libs/desktop-1.0.jar \
     -mainclass uk/co/alynn/games/suchrobot/desktop/DesktopLauncher \
     -minimizejre hard \
     -outdir distributions/windows/robogame
java -jar jres/packr.jar \
     -platform mac \
     -jdk 'jres/mac64.zip' \
     -executable RoboGame \
     -appjar desktop/build/libs/desktop-1.0.jar \
     -mainclass uk/co/alynn/games/suchrobot/desktop/DesktopLauncher \
     -minimizejre hard \
     -outdir distributions/mac/RoboGame.app
pushd distributions/windows
zip -r ../final/robogame-win32.zip robogame
popd
pushd distributions/mac
zip -r ../final/robogame-mac.zip RoboGame.app
popd

