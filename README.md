## Assumptions:
- A 'User' can only view 'Tweets' from his 'Followees' that are posted after the date the 'Follow' event happens.
- If a 'Tweet' contains more than 140 characters the REST endpoint should return 400 (Bad request) with a proper message.
- A 'User' cannot follow himself, if a request is sent with that scenario the REST endpoint should return 400 with a proper message.
- There is no 'Tweet' delete functionality.
- There is no 'Unfollow' functionality.
- There is no maximum number of 'Tweets' a 'User' can post or view in his 'Timeline' or 'Wall'.
- There is no maximum number of 'Followers' a 'User' can have.
- The application does not handle 'Users', it acts as if another service is responsible to create and validate them.
- A 'User' have a UUID which is the unique representation of the 'User' for the application.  

## Implementation decisions
- Lombok was used to reduce boiler plate code (https://projectlombok.org)
- Swagger was used for API documentation (https://swagger.io)
- To give priority to reads a Fan out on write technique was implemented (i.e. when a user post a tweet, this will be written/saved into all users timeline that are following him).  
- Data structures were set in place to store the data in memory simulating a 'cache' DB like Redis.
- Spotify maven plugin was used to generate the docker image.
- All Classes have 100% branch test coverage except for POJOs and Configuration files.
- For sake of simplicity the following decisions were made:
    - API error handling is kept to spring default.
    - The build phase has no test coverage or code analysis plugins.  
    - Dockerfile contains the minimum options to run. 
    - No Extensive use of DTOs and mappings were used throughout the 'layers' of the application to reduce boiler plate.
 

## How to run
### There are two ways to run the application (both ways use the port 8080)
1. Build and run the docker image
    - Navigate to the root of the project
    - Run the script './bin/build-docker.sh'
    - Run the script './bin/run-image.sh'

2. Build the jar and run with java command
    - Navigate to the root of the project
    - Run 'mvn clean install'
    - Run the script './bin/run-jar.sh'

### API documentation (Swagger)
- First run the Application
- Open your browser and go to 'localhost:8080/swagger-ui.html'.
- Navigate in 'rest-user-controller' to discover the endpoints. 