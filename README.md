Functionality
-------------
Service Leaderboard
- Allow user post socre
- Allow user change name
- Get list user at top score

Main features
-------------
   * Language: Java
   * Network: Netty
   * Database: File, Redis

Build & Runtime Requirements
------------------
* Mac OS X 10.7+, InteliJ IDEA 2017.1.2
* Java 8

How to run
--------------------
Using IDE
1) Open project with InteliJ IDEA
2) Click build project
3) Click Run or Debug project

Using CMD on Window:
1) Open file config.bat in folder run, edit the path of JDK according on your computer
(set JDK_DIR="C:\Program Files\Java\jdk1.8.0_131")
2) Run redis: click on file run_redis.bat
3) Open cmd, direct to folder LeaderBoardServer/run
4) Run game: run_game.bat

Structure Project
--------------------
_database: this folder was generate for saving data of user
_release_: this folder contain file jar after compiling the application
data: Contain file config for the application
libs: Contain libs which neccesary for the application
out: this folder was generate automatically by IDE
run: contain file .bat run for window os, so don't need IDE to build, compile, run the application
src: contain source code of the application
tools: some tools is needed when running on local environment
