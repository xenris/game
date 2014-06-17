#!/bin/sh

package=com.xenris.game
activity=MainMenu

adb devices | tail -n +2 | cut -sf 1 | xargs -iX adb -s X shell am start -n $package/$package.$activity
