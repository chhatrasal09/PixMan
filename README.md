# PixMan

PixMan is a powerful image editing app for Android which helps in applying a few image operations on a selected image. The app has following features:
#### Features
The user can perform the following action on an image : 
- Flip image vertically.
- Flip image horizontally.
- Add __GreedyGame__ text with green text color and black background to right bottom of the image.
- Reduce the opacity of image to 50% of the current opacity.
- User can undo the last 3 tranformation action performed on the image.
- Save the file to the __Pictures__ folder of th device.

#### Decisions and Assumptions
The decisions made in the development of the app.
- The __GreedyGame__ logo will be addded to the left bottom of the image after performing the save action.
- The theme selected doen't contains toolbar.
- The files saved is in __.png__ fromat to retain the opacity of the image.
- The images will be saved only when user performs the save operation. There will be no intermediate saves for the images.
- The __UNDO__ button will be disabled initially and will be enabled if user performs the transformation action. And again it will be disabled after performing 3 undo steps.
- The transformations will be performed only when user click the __PREVIEW CHANGES__ button. All the transformation will not be executed unless user clicks the __PREVIEW CHANGES__ button. So to view the transformation user hast to manually press the __PREVIEW CHANGES__ button to perform the transformation.
