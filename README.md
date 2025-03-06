# imagealign
Manual image pre-alignment tool for use with image stacking software.

I created this tiny tool for myself since I do untracked astrophotography and I wanted to at least
roughly align my images before stacking them, so that the stacking software can successfully align them.

## Usage
[Download the latest release](https://github.com/Seggan/imagealign/releases) and run the jar file.

imagealign requires at least Java 11 to run. You can download the latest version of Java from [here](https://adoptium.net/).

1. Select the images to align
2. For every image, click on the rough center of the object you want to align
3. Once all images have been selected, the program will automatically align them and save them in a sibling folder called `aligned-n`

