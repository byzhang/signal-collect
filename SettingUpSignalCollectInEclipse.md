# Introduction #

## Requirements ##

If you previously followed our RecommendedTools  tutorial then this tutorial is for you. Otherwise you should do it first.

## Considerations ##

For simplicity, we are going to demonstrate how to perform all the steps inside eclipse. You can also do it from the command line for operations related to **svn** and **maven**.

# Steps #

## Step 1 ##

On the **SVN Repository Exploring** perspective add the signal-collect repo:

Either ` https://signal-collect.googlecode.com/svn/trunk/ ` if you intend to contribute or ` http://signal-collect.googlecode.com/svn/trunk/ ` if you just want to download the source code.

## Step 2 ##

Now you see the tree with all the sub-projects inside signal-collect. Right click on the desired sub-project and select checkout.

The **Checkout as** wizard appears. Select to _Checkout as a project in the workspace_ leaving the default name in place. Once the project is checked out, switch to the **Scala Perspective** and you will see the project in place. Right click on it and **delete** it without selecting **Delete project contents on disk**.

Note.: this is step can ben replaced simply by checking out the project using the svn command line.

## Step 3 ##

Once the project has been deleted from the IDE, still in the same perspective, select right-click on the **Package explorer** empty area and choose _Import_ -> _Maven_ -> _Existing maven projects_

The root directory for the project should be your workspace where you actually checked out the project (if you used eclipse to check it out).

Browse to the directory, select the directory where the project is (not the project directory itself) and you should see that the wizard found the **pom.xml** configuration. With that just finish the wizard.

With that, you're ready to go.