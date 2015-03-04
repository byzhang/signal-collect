# Introduction #

# On Disk Storage #
Signal-Collect allows the user to store vertices on disk to increase the numer of vertices that fit into one graph and overcome the constraint due to limited main memory.

## MongoDB ##
### MongoDB Installation ###
A very straight forward installation tutorial can be found in the MongoDB documentation wiki:
http://www.mongodb.org/display/DOCS/Quickstart

The most important Steps are summarized here:

### OSX Installation ###
The easiest way to install MongoDB is via [MacPorts](http://www.macports.org) or [Homebrew](http://mxcl.github.com/homebrew) package manager.

For Macports use:
```
$ sudo port install mongodb
```

With the Homebrew package the MongoDB package can be installed with this commands:
```
$ brew update
$ brew install mongodb
```

After the installation a folder for the MongoDB needs to be created. For the default configuration settings use:
```
$ mkdir -p /data/db
```

When you have created your data folder you can start the MongoDB server by entering:
```
$ ./mongodb-xxxxxxx/bin/mongod
```
And enter the MongoDB shell from another terminal with:
```
$ ./mongodb-xxxxxxx/bin/mongo
```
Hint: Adding the "bin" folder path (e.g. "/usr/bin/scala/bin/") to your PATH variable might come in handy.

### Binaries for various OS ###
Binaries for OSX, Linux, Windows & Solaris can be found here: http://www.mongodb.org/downloads