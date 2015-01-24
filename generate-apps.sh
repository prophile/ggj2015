#!/bin/bash
set -e
mkdir -p distributions
java -jar jres/packr.jar \
     -platform windows \
     -jdk 'jres/win32.zip' \
     -executable robogame \
     -appjar desktop/build/libs/desktop-1.0.jar \
     -mainclass uk/co/alynn/games/suchrobot/desktop/DesktopLauncher \
     -minimizejre hard \
     -outdir distributions/windows
java -jar jres/packr.jar \
     -platform mac \
     -jdk 'jres/mac64.zip' \
     -executable RoboGame \
     -appjar desktop/build/libs/desktop-1.0.jar \
     -mainclass uk/co/alynn/games/suchrobot/desktop/DesktopLauncher \
     -minimizejre hard \
     -outdir distributions/mac

