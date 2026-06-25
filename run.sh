#!/bin/bash
# HiDPI-Fix fuer Wayland/Linux: skaliert Swing-GUI korrekt
java \
  -Dsun.java2d.uiScale.enabled=true \
  -Dawt.useSystemAAFontSettings=on \
  -Dswing.aatext=true \
  -jar target/java-parser-1.0-SNAPSHOT.jar
