To work with the Signal/Collect framework using the provided jar file you **don't** necessarily need to follow the instructions listed in the [Recommended Tools](RecommendedTools.md) section. It is sufficient to simply add the jar file to build path of your java project. The following instructions are for the eclipse IDE but work for other settings in a similar way.

### Step 1: Download Signal/Collect Jar File ###

Download the file "core-1.0.1-SNAPSHOT-jar-with-dependencies.jar" from [here](here.md) **!!UPDATE ME!!**

### Step 2: Import Signal/Collect Jar File ###

In Eclipse create a new Java Project and add the downloaded jar file to its build path.

To add the jar file to the project follow the following steps
  1. right-click on the project in the package explorer tab and select _Build Path_ > _Add external Archives..._
  1. select the downloaded jar in the dialogue window.
  1. In the package explorer you should now see the jar in your project under _Referenced Libraries_

### Step 3: First  Algorithm ###
Look at the algorithms [here](http://code.google.com/p/signal-collect/source/browse/trunk/#trunk%2Fjavaapi%2Fsrc%2Ftest%2Fjava%2Fcom%2Fsignalcollect%2Fjavaapi%2Fexamples) and try to run them and play with them in your project. An explanation of the programming model and the example algorithm is contained [here](JavaExampleAlgorithm.md)