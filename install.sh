#!/bin/sh

name=Game

adb devices | tail -n +2 | cut -sf 1 | xargs -iX adb -s X install -r bin/$name-debug.apk
