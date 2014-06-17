#!/bin/sh

name=Game

ant clean
android update project -t android-19 -n $name -p .
