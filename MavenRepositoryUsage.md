# How I got Maven Repo Sync to Work #

  * Install gpg
  * Followed tutorial @ https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide

Special commands that were required:
  * mvn release:clean
  * mvn release:prepare -Dusername=**USERNAME** -Dpassword=**PW**
  * mvn release:perform -Dusername=**USERNAME** -Dpassword=**PW** -DconnectionUrl=scm:svn:http://signal-collect.googlecode.com/svn/trunk/core/
  * mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=sonatype-nexus-staging -DpomFile=pom.xml -Dfile=./target/signal-collect-core-1.1.2-SNAPSHOT-javadoc.jar -Dclassifier=javadoc