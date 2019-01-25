Main features
-------------
   * Language: Java
   * Network: Netty
   * Database: File, Redis

Build & Runtime Requirements
------------------
* Window
* InteliJ IDEA 2017.1.2
* Java 8

How to run
--------------------
Method1: Using CMD on Window:
1) Edit file config.bat, edit the path of JDK according location on your computer
(set JDK_DIR="C:\Program Files\Java\jdk1.8.0_131")
2) Open cmd, direct to folder LeaderBoardServer
3) Use cmd for running file make.bat
4) Use cmd for running file run.bat in folder LeaderBoardServer/run

OR

Method2: Using IDE
1) Edit file config.bat, edit the path of JDK according location on your computer
(set JDK_DIR="C:\Program Files\Java\jdk1.8.0_131")
2) Open project with InteliJ IDEA
3) Click build project
4) Click Run or Debug project

Functionality
-------------
Service Leaderboard
- Allow user post score
```
Example:
127.0.0.1:19140/postscore?userid=100001&score=120
127.0.0.1:19140/postscore?userid=100002&score=80
127.0.0.1:19140/postscore?userid=100004&score=150
Check data with UserID 100001 was changed (in folder _release_/_database/user_game_data)
```

- Allow user change name
```
Example: 127.0.0.1:19140/updateuser?userid=100001&name=vietrise
Check data with UserID 100001 was changed (in folder _release_/_database/user_game_data)
```

- Get list user at top score
```
Example: 127.0.0.1:19140/topuser
```

***Note***:
- Use POSTMAN application to send http request
[Postman](https://www.getpostman.com/downloads/)
- Use some browser to send http request (Chrome, Firefox, ...)

Structure Project
--------------------
* _release_: this folder contain file jar after compiling the application
* _release_/_database: this folder was generated for saving data of user
* data: Contain file config for the application
* libs: Contain libs which is essential for the application
* out: this folder was generated automatically by IDE
* run: contain file .bat run for window os, so don't need IDE to build, compile, run the application
* src: contain source code of the application
* tools: some tools is needed when running on local environment
