# BetterNeptun for Obuda University Neumann Information Technology students

#### An application which is meant to replace the official Neptun application. 
(I will make it work for any hungarian university student in the long run)

## The Goal

#### My goal with this app was pretty simple. I got tired of always having to go through the process of either opening the neptun in a browser or in the official app (which more often doesn't work then it does work) and try to search for relevant informations releated to classes or exams.

#### This app was made exclusively for university students and to make their lives just a little bit easier.

## Features in a nutshell

#### I implemented some very useful features for the mentioned goal above, but i'll get back to those later. Let's just sum them up in a nutshell.
- UI element for your next classes and ongoing classes with useful informations
- UI element for your currently unread messages
- Page for reading your messages in better format in a modern material design.
- Page for all your calendar events.
- Page for all of your current courses with relevant informations from them.
- Page for all semester averages and credits with cool looking charts.

#### Future pages
- Page for exams
- Page for timelines

## The UI (Dark and Light mode are both available)

### Home page

#### The home page contains all of the clickable menu navigations that you can access, and some basic information about your data, and your next class or ongoing classes. The white tinted space contains your name, and neptun code.

- Current and Ongoing carousels
  - I tried to keep it very simple, but also pleasing to the eye, with tons of useful features that could help you in your early days at the university.

<img height="700" alt="image" src="https://github.com/user-attachments/assets/f2146699-2ba4-4a05-bb10-16996908fe8c" />


### Message page

#### On this page, I implemented a paginated API request system that fetches messages 20 at a time. Clicking on a message and reading it also marks the message read for neptun, so you dont have to read the message there. New messages are maked with a small dot and have their title bolded a bit. 

#### The design kinda resembles any mailing application, but there are no swipe gestures here :D

<img height="700" alt="Screenshot_20260606_095635_OE Neptun" src="https://github.com/user-attachments/assets/783fc11d-cc55-437f-8cfb-069e9ca1eb1b" />
<img height="700" alt="Screenshot_20260606_095703_OE Neptun" src="https://github.com/user-attachments/assets/b566cf34-77ad-4fad-9cc9-ec71a56b5550" />

### Calendar page 

#### The calendar is quite simple in a way, because it looks almost like any other calendar app, but it only shows your courses exams, and any other event that you add yourself. The calendar itself is customizable, you can add custom events, and customize the colors of the course or exam events.

#### Only custom events can be deleted and fully modified. For neptun calendar events only the color can be modified.

<img height="700" alt="Screenshot_20260606_095719_OE Neptun" src="https://github.com/user-attachments/assets/9d40205f-d36e-4016-8f1d-72139f350da6" />
<img height="700" alt="Screenshot_20260606_095722_OE Neptun" src="https://github.com/user-attachments/assets/d91dfddb-365d-4b40-bb46-cefa2bd2d12d" />
<img height="700" alt="Screenshot_20260606_095730_OE Neptun" src="https://github.com/user-attachments/assets/dba4471b-e636-459d-b128-69858ef804bd" />
<img height="700" alt="Screenshot_20260606_095806_OE Neptun" src="https://github.com/user-attachments/assets/0b5b8d86-b6ae-475b-8500-f6348b74243d" />
<img height="700" alt="Screenshot_20260606_095816_OE Neptun" src="https://github.com/user-attachments/assets/e8aa09d1-cb73-40b9-8348-89af4f51b34c" />
<img height="700" alt="Screenshot_20260606_095812_OE Neptun" src="https://github.com/user-attachments/assets/8c443fea-eeb9-4ae4-b9c4-b3881c24c488" />

### Subjects page

#### A simple filtering subjects page where subjects can be filtered by the current selected semester.

<img height="700" alt="Screenshot_20260606_100002_OE Neptun" src="https://github.com/user-attachments/assets/70d92e93-c934-4b1a-afc0-b91025ffb987" />

### Credit and mark averages page

https://github.com/user-attachments/assets/1ea7735f-d185-4bbd-aa7d-12ad1a80f887

### Lastly the Settings page

#### Nothing to explain here, just plain settings stuff.

<img height="700" alt="Screenshot_20260606_100034_OE Neptun" src="https://github.com/user-attachments/assets/3b68a2d1-3367-4953-bfa4-e5c361599f64" />
