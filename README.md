filebrowser-android
===================

Feature:

1. browse files from the specified directory.
2. click the file to open. 
3. easily integrated into your APK applications.

Usage:

1. copy res and src to your Android project directory.
2. how to start it? 

Intent intent = new Intent(this, FileBrowserActivity.class);
intent.putExtra("start_mode", "browser");
intent.putExtra("root_dir", "/sdcard/");

startActivity(intent);

Future:

1. add 'start_mode' equals "select_dir" and "select_file".