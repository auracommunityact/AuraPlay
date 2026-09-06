#!/bin/bash
cd app/src/main/res

# Remove old files
rm -f drawable/ic_launcher_foreground.xml
rm -f drawable/ic_launcher_background.xml
rm -f mipmap-*/ic_launcher.webp
rm -f mipmap-*/ic_launcher_round.webp

# Setup background color
cat << 'XML' > values/colors_icon.xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#FFFFFF</color>
</resources>
XML

# Setup adaptive icons
cat << 'XML' > mipmap-anydpi-v26/ic_launcher.xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
XML
cp mipmap-anydpi-v26/ic_launcher.xml mipmap-anydpi-v26/ic_launcher_round.xml

# Place foreground
mkdir -p drawable-nodpi
cp /tmp/logo.png drawable-nodpi/ic_launcher_foreground.png

# Generate legacy and round icons
for size in 48 72 96 144 192; do
  dir="mipmap-mdpi"; [ $size = 72 ] && dir="mipmap-hdpi"; [ $size = 96 ] && dir="mipmap-xhdpi"; [ $size = 144 ] && dir="mipmap-xxhdpi"; [ $size = 192 ] && dir="mipmap-xxxhdpi"
  
  convert /tmp/logo.png -resize ${size}x${size} ${dir}/ic_launcher.png
  
  convert -size ${size}x${size} xc:none -fill white -draw "circle $(expr $size / 2),$(expr $size / 2) $(expr $size / 2),0" mask.png
  convert ${dir}/ic_launcher.png mask.png -alpha Set -compose Dst_In -composite ${dir}/ic_launcher_round.png
done
rm -f mask.png
