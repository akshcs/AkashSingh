# AkashSingh
Emergency Service for What3Words

Resources
--------------------------------------------------------------------------------

- Swagger Page :  https://emergency-production.up.railway.app/emergencyapi/swagger-ui.html#
- Github : https://github.com/akshcs/AkashSingh
  

How to run this Application
--------------------------------------------------------------------------------

- This application is running as a service. You can directly test the application from the swagger link provided above.
- The swagger link is provided above please use it to Test
- It is a Spring Boot Application, You can also run this application on your local machine by running EmergencyApplication class
- You can use any IDE (I would recommend Intellij)
- This application will start on Port 8080.

Notes/Assumptions
--------------------------------------------------------------------------------

- I was not very clear on the requirement for adding tests, to be on a safer side I have added all the unit and integration tests. 
- Since, the service is also deployed it should be reasonably less time-consuming to test and review for reviewers.
- I have assumed that persisting any information was not part of this exercise, and thus I have not used any DB to store any information. I can add a persistent layer and a DB if it is required as a part of the exercise
