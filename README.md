 # Custom user provider for keycloak
 

## How to use

Simply build the project with maven and copy the jar file to the keycloak server's `standalone/deployments` folder.

## TODO

As of now, the user service is hardcoded to return a list of 10 users.
This should be changed to connect to the microservice that is responsible for user management.

Same goes for the security server, which will be used to check password validity.
