# MATLAB2TikZ
This is an Java assignment which converts MATLAB plots to TikZ pictures.

## Features offered now:
* Importing pictures.
* Generating TikZ documents using the imported picture.
* Viewing TikZ files(\*.tikz) by adding something to it and compile it using pdflatex/xelatex.
* Automatically generation and preview mode.
* Classifying pictures with multiple datasets.

## Dependecies:
* LaTeX compiler: xelatex or pdflatex in your $PATH.
* (Future) Tesseract-OCR: tesseract-ocr in your $PATH to enable word recognition.

## Update Notes:

### Version 0.2.0
* Add support for multiple dataset detection and classification.
* Change the algorithm to 'filter' the picture by color.
* Add a feature to show a red border in 'Data Config' menu to show input errors clearly.
* Add a feature to reset the data while importing a new picture.

### Version 0.1.5-alpha
* Add a **testing** feature to detect and  classify legends inside the picture. (**Some additional files will be generated.**)
* Fix a bug in Preview TikZ part.
* Add a feature to let the program accept pictures without X, Y Labels.
* Add some algorithms to 'filter' the picture by color.
* Fix a bug in Otsu algorithm.
* Adding a picture-resizing algorithm to prepare for OCR.

### Version 0.1.2
* Fix a dialog-data bug in the 'General Settings' menu.
* Change some dialog styles in the 'Settings' menu. (Add borders, add hgap, vgap).
* Remove some redundant codes.
* Change class visiblity levels.
* Add some guidelines and buttons in the main window.

### Version 0.1.0
* Basic structure of 2-d plots are constructed now.
* Automatic mode enabled.
* **NOTICE: You need to manually entering data because word recognition hasn't been developed now.**

### Version 0.0.9:
* Get prepared for the program.
* Change all-possible static methods to non-static

### Version 0.0.1:
* Initial commit.
