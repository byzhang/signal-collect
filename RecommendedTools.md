# Recommended Tools to Develop S/C #
This is a short guide that describes how to get the tools that we recommend for working with the internals of the Signal/Collect code.


## Eclipse ##
  * Install the **Eclipse IDE for Java Developers 3.7** (_INDIGO_) from http://www.eclipse.org/downloads
Optional steps, potentially different depending on your platform and how much memory your PC has:
  * Find the eclipse.ini file within the eclipse folder/package.
  * Within that file, adjust the -Xms vaue to 800m and the -Xmx value to 2048m

## Scala ##
Install the Scala plugin for Eclipse:
  * Within Eclipse go to _Help_, _Install New Software..._
  * Add as a new location: http://download.scala-ide.org/releases-29/stable/site
  * Install all the available items from that source.

## Maven-Scala Integration ##
Install the Maven-Scala integration plugin:
  * Within Eclipse go to _Help_, _Install New Software..._
  * Add as a new location: http://alchim31.free.fr/m2e-scala/update-site
  * Install the items "Maven Integration for Eclipse" and "Maven Integration for Scala IDE" from that source.

## SVN ##
Install _Subclipse_ from the _Eclipse Marketplace_: You can find it in the Eclipse _Help_ menu, it's the last entry. On OS X we recommend using the SVNKit client, which you can install in the "Install New Software ..." menu and activate in _Eclipse_, _Preferences..._, _Team_, _SVN_, _Client_