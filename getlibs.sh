#!/bin/bash

mkdir lib
cd lib
wget "http://www.slick2d.org/downloads/slick.jar"
wget "http://www.newdawnsoftware.com/jenkins/view/LWJGL/job/LWJGL/lastSuccessfulBuild/artifact/dist/lwjgl-2.9.0.zip"
unzip lwjgl-2.9.0.zip
rm lwjgl-2.9.0.zip
