# BetterNeptun for Obuda University Neumann Information Technology students

#### An application which is meant to replace the official Neptun application. 
(I will make it work for any neptun user univerity student in the long run)

## The Goal

#### My goal with this app was pretty simple. I got tired of always having to go through the process of either opening the neptun in a browser or in the official app (which more often doesn't work then it does work) and try to search for relevant informations releated to classes or exams.

#### This app was made exclusively for university students and to make their lives just a little bit easier.

## Features in a nutshell

#### I implemented some very useful features for the mentioned goal above, but i'll get back to those later. Let's just sum them up in a nutshell.
- UI element for your next classes and ongoing classes with useful informations
- UI element for your currently unread messages
- Page for reading your messages in better format in a modern material design.
- Page for all your calendar events.
- Page for all af yout current courses with relevant informations from them.
- Page for all your exams, with visual feedback on their status.
- And other pages which might or might not be helpful, but who knows.

## The UI (Dark and Light mode are both available)

### Home page

#### The home page contains all of the clickable menu navigations that you can access, and some basic information about your data, and your next class or ongoing classes. The white tinted space contains your name, and neptun code.

<img src="https://user-images.githubusercontent.com/96139474/190135696-b17d77f8-c4a1-42ea-89c5-be4e36b20344.jpg" height="500" img/>

### Current and Ongoing card

#### I tried to keep it very simple, but also pleasing to the eye, with tons of useful features that could help you in your early days at the university.

#### The main concept was to help students going from one class to another a little bit easier, so i decided to implement a feature which will show you your next class at all times on your Home screen, and if for some reason you are late from your class, no worries, there is a UI element for your ongoing class as well, so you can easily just open the app, and get the location of your current ongoing class.

![2022-09-14_10 59 32](https://user-images.githubusercontent.com/96139474/190110486-3f891453-9a54-4f34-a0a7-b2f6ca3a771d.jpg)

### Message page

#### On this page, i decided to fetch all of the messages of the user, to then save them locally in a Room database [Click here to learn more about it](https://developer.android.com/training/data-storage/room). Clicking on a message and reading it also marks the message read for neptun, so you dont have to read the message there. New messages have a blue stroke around them so you will not miss any of them. 

#### The design kinda resembles a any mailing application, but we dont have icons here, we only have text. 

<img src="https://user-images.githubusercontent.com/96139474/190134635-4694fabc-d5de-442a-9f31-339f2d1f3a7b.jpg" width="500" img/>

### Calendar page 

#### The calendar is quite simple in a way, because it looks almost like any other calendar app, but it only shows your courses exams, and any other event that you add yourself. The calendar itself is customizable, you can add custom events, and customize the colors of the course or exam events.

![2022-09-14_13 10 12](https://user-images.githubusercontent.com/96139474/190138923-1147d38e-bb19-40d9-89da-75a8928e2531.jpg)

teszt
