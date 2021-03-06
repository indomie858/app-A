# Appa

Appa is user-friendly application that will be used to help visually impaired students or individuals at California State University, Northridge. <br />
The idea was brought up when we realized that the university does not provide a wide variety of options for those who are visually impaired. <br />
We want to create an app that will provide them guidance and information without having the need of a second person. <br />

## Background
California State University, Northridge has accessibility programs such as JAWS[1], Screen Magnification[2], VoiceOver[3] and Window-Eyes[4] for the visually impaired. However, the programs that are provided do not help in guiding students around campus. We want the university to be more inclusive and give students with disabilities a sense of independence when they are on campus so that they would not need to rely on a second person to guide them around campus. With that said, our application will help students navigate around campus as safely as possible through the use of our mobile application and its associated hardware, as well as know which building they are closest to with accompanying building information. <br> <br>
The purpose of project Appa is to create a mobile application that would guide visually impaired students around the university campus and provide them with relevant information. This project will have an Arduino microcontroller with an ultrasonic sensor attached near the handle of the cane which will detect objects within close proximity, bluetooth beacons will be placed around campus and relay a message when the user is within the radius and have a mobile app that will retrieve and interpret the informations being sent by the bluetooth beacon and Arduino. The app will be simple and user friendly, and will strive to follow WCAG[5] accessibility standards. <br />
- [1] <b>Job Access With Speech (JAWS)</b>: Job Access With Speech is a computer screen reader program for Microsoft Windows that allows blind and visually impaired users to read the screen.
- [2] <b>Screen Magnification</b>: Zoom is full-screen magnifier that can magnify the items on the screen up to 40 times built into Apple Inc.'s Mac OS X, iOS and iPod operating systems.
- [3] <b>VoiceOVer</b>: A screen reader built into Apple Inc.'s Mac OS X, iOS and iPod operating systems.
Window-Eyes: Window-Eyes is a screen reader for Microsoft Windows.
- [4] <b>Web Content Accessibility Guidelines (WCAG)</b>:  a set of guidelines developed by the World Wide Web Consortium (W3C) with the goal of making web content more accessible to people with disabilities. Though these guidelines were developed for web content, these guidelines are also highly relevant to mobile content and will serve as a baseline for developing an accessible mobile application.

## Project Members
```
Anne Xaymountry, Gaven Grantz, Frank Joseph Serdenia, Daniel Palencia, Allan Huidobro, Ryunosuke Mori
```
## Tools
```
Android Studio | Bluetooth Beacons | Arduino | Ultrasonic sensors | Cane | Mapbox | Altbeacon Android Beacon Library 
```
## Languages
```
Java | C | Kotlin
```

## Devops Documentation (GitHub/GitKraken/Travis CI)
### Guidelines for git process
1.	Always pull/fetch latest changes whenever you begin a working session.
2.	Handle any conflicts in GitKraken or git CLI.
3.	Commit changes to feature branches (example: issue17-homescreen)
4.	Push only to the feature branches, not directly to dev branch
5.	Create pull request to merge changes from feature branches into dev branch.
6.	At the end of the day (working session), make sure you commit or stash your changes.
7.  Never push directly to “release” branch. This branch will be solely for releases.



