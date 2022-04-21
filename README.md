## Political Preparedness

PolitcalPreparedness is an example application that provides civic data intended to provide educational opportunities to the U.S. electorate using data provided by the Google Civic Information API. Application will use data from this API to allow users to track information on target representatives and voting initiatives where applicable.

Users will be able to target a specific location and retrieve the associated civic data, displaying it to the user, and providing a clean user experience for consumption. Users will be able to save predefined locations for quick access and mark preferred representatives and policy outcomes. Where available, elections and voter information will also be provided, notifying the users on upcoming elections and providing access to associated information and saved data.

This app demonstrates the following views and techniques:

* [Retrofit](https://square.github.io/retrofit/) to make api calls to an HTTP web service.
* [Moshi](https://github.com/square/moshi) which handles the deserialization of the returned JSON to Kotlin data objects.
* [Glide](https://bumptech.github.io/glide/) to load and cache images by URL.
* [Room](https://developer.android.com/training/data-storage/room) for local database storage.

It leverages the following components from the Jetpack library:

* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [Data Binding](https://developer.android.com/topic/libraries/data-binding/) with binding adapters
* [Navigation](https://developer.android.com/topic/libraries/architecture/navigation/) with the SafeArgs plugin for parameter passing between fragments

## Demo

Upcoming Elections         |  Find representatives using location
:-------------------------:|:-------------------------:
![upcoming_elections_flow](https://user-images.githubusercontent.com/12608658/164539814-e7b3ec2b-5156-4dcc-bf1c-ecee5f7ec475.gif)  |  ![find_rep_location_flow](https://user-images.githubusercontent.com/12608658/164541772-e39ec171-ed74-4c95-88f5-ab190ccc2a23.gif)

Find representatives using address     | Preserving state on process kill and orientation changes 
:-------------------------:|:-------------------------:
![find_rep_using_address](https://user-images.githubusercontent.com/12608658/164543320-5c5ab29e-5d2e-4fa2-8ae9-571c23cc9080.gif) | ![Preserving motion layout and recycler view state](https://user-images.githubusercontent.com/12608658/164545759-ddff0fec-35a3-46a7-8495-5011b1a72ea2.gif)

## Setting up the Repository

To get started with this project, simply pull the repository and import the project into Android Studio. From there, deploy the project to an emulator or device.

## Project Specification
Following is the project rubric that lists all the requirements that are met by application.

![rubric](https://user-images.githubusercontent.com/12608658/164546404-97e6b904-afea-4acd-b18d-b299d1d2f9b7.jpeg)
