Functionality
-------------
Service Leaderboard
- Allow user post score
Example: 127.0.0.1:19140/postscore?userid=100001&score=120
- Allow user change name
Example: 127.0.0.1:19140/updateuser?userid=100001&name=vietrise
- Get list user at top score
Example:127.0.0.1:19140/postscore?topuser

***Note***:
You can use POSTMAN application to send http request
Postman: https://www.getpostman.com/downloads/

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
2) Open cmd, direct to folder LeaderBoardServer
3) Use cmd for running file make.bat
(set JDK_DIR="C:\Program Files\Java\jdk1.8.0_131")
4) Run redis: click on file run_redis.bat
5) Run game: Use cmd and cd to folder LeaderBoardServer/run,run file run_game.bat

Structure Project
--------------------
* _database: this folder was generate for saving data of user
* _release_: this folder contain file jar after compiling the application
* data: Contain file config for the application
* libs: Contain libs which neccesary for the application
* out: this folder was generate automatically by IDE
* run: contain file .bat run for window os, so don't need IDE to build, compile, run the application
* src: contain source code of the application
* tools: some tools is needed when running on local environment
