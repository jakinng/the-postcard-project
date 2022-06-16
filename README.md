# The Postcard Project
The Postcard Project is an app allowing users to send digital postcards to anyone, anywhere. This app was created as the capstone project for Facebook University (Android) 2022.

## Table of Contents
0. [Project Plan](#Project-Plan)
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Project Plan
The project plan is broken down by week in this document: https://docs.google.com/document/d/1FIq4kLYuP2yt9hTPlbxsmhYxUyivNuinoOgq0iNs0sU/edit?usp=sharing

## Overview
### Description
<!-- [Description of your app] -->
<!-- Send a digital postcard or letter to a friend today! The Postcard Project puts a modern twist on the postcard. Send one letter a day and slow down.  -->
<!-- Can't keep a journal? Tired of short texts and scrolling through social media? Miss the experience of sending postcards to people? The Postcard Project lets users send one postcard a day to a friend on the app or someone else in a new location! The experience of sitting down and writing a meaningful message or reflecting on your day has been lost over time. Now, you have a modern twist on sending a postcard -- attach your own images, send music from your favorite artist, and find people from new locations. This app even integrates with text, email, or your favorite messaging app, to let you send a postcard to anyone! -->

Send digital postcards to your friends or people around the world!

### App Evaluation
<!-- [Evaluation of your app across the following attributes] -->
- **Category:** Social
- **Mobile:** The app is mobile-first. The instantaneous ability to send a digital postcard with an attached image from the camera or camera roll is central to the app. The app also depends on the location of the user and integrates a mobile map view. 
- **Story:** Allows users to send postcards with personal images and connect socially through an app. Users can send longer and more personal messages than just texts and integrate the postcard experience with modern capabilities such as including user-taken photos and connecting with strangers based on location. 
- **Market:** Anyone can use this app! Users who enjoy travelling or sharing photos of their day with friends, family, or other people based on location can enjoy sending e-postcards.
- **Habit:** Users can send postcards whenever they are interested in doing so. Moreover, they can explore and read postcards from other people.
- **Scope:** The minimum viable product allows users to create accounts, upload photos from their camera roll or take pictures in-app, send and receive postcards and see them in a feed as well as a detail view, and set location or find other users by location. Stretch features include integrating postcard sending capabilities with other text, email, or messaging apps on their phone, displaying an interactive map of postcards sent from different locations, sending push notifications daily, or adding "friend" or more social components to the app.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**
* Allows users to create accounts and log in/out
* Users can upload photos from their camera roll or take pictures in- app from their camera
* Allows users to send postcards 
* Allows users to receive postcards
* Users can see a feed of postcards they have sent and a separate feed of postcards they have received
* Users can see a detail view of postcards
* Users can set their location
* Users can find other users by location

**Optional Nice-to-have Stories/Stretch Goals**
* Users can send postcards through text/email/messaging apps
* Push notifications
* Users can see postcard feeds by location
* The app suggests images to include based on location
* Users can friend other users

### 2. Screen Archetypes

* Login
   * Users can create new accounts and log in to their account
* Stream
   * If no location selected, displays all possible matches with infinite pagination
   * If location and times selected (in Creation screen), displays potential matches based on these restrictions
* Detail
    * People can see the profile page of a potential match
    * Users can message matches
* Creation
    * Create a new request for a specific location and time
* Profile
    * Users can see their own profiles

### 3. Navigation
**Flow Navigation** (Screen to Screen)

* Login (LoginActivity)
   * Can log in to Stream
* Stream (HomeActivity)
    * Users can see all the postcards they have been sent
    * Can click on an item for a Detail view*
    * Can click on Profile to see all the postcards they have ever sent
    * Can click + button to go to Creation screen
* Detail (PostcardDetailActivity)
    * Can go back to Stream
* Creation (CreateActivity)
    * Can create a new postcard
    * Can also go back to Home and lose created postcard
* Profile (ProfileActivity)
    * Users can see postcards that they have sent
    * Can go back to Stream

## Wireframes

### Digital Wireframes & Mockups
The wireframing was done on Figma: https://www.figma.com/file/4HzdZHuMUNbvywe34ra96w/The-Postcard-Project?node-id=0%3A1

### Interactive Prototype
An interactive prototype was made using Figma:
https://www.figma.com/proto/4HzdZHuMUNbvywe34ra96w/The-Postcard-Project?node-id=10%3A137&scaling=scale-down&page-id=9%3A192
<!-- 
## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp] -->
