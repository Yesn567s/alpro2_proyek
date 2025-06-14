@echo off
echo Compiling and running the application...

cd "c:\Users\benny\OneDrive\Documents\GitHub\alpro2_proyek\Alpro_proyek"

:: Compile Java files
javac -d bin src/*.java

:: Run the application
cd bin
java App

echo Done!
