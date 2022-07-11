# JSON-to-DAT
Hi! I made this simple project quite a while ago. I figured I'd go ahead and upload it for anyone who finds it useful, I know I have.

The sole class file converts JSON text to an NBT compound tag as a .DAT file usable by Minecraft.
I've been using this to store entities saved as .JSON files by mods into complex NBT compound tags within .DAT files, specifically saving clones into
the natural spawns "spawns.dat" file in the CNPC mod. It's made automating spawns a lot easier!

To import and develop the project, clone the repo to your PC, open in any Java IDE like IntelliJ, and add the included JNBT library. To run the project,
run the .java file with two arguments, one being the path to your JSON file, and the second being the path to the newly created .DAT file.
