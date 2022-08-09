# Spring Cloud, Consul, Spring gateway + resource server and Auth-server
JBackend represents the attempt to create a ground point for starting any project.

The project is free to use, so you may use it in any way you'd like, except for selling it as is. 

My first project that I was inspired by had the ZuulProxy service, which combined the 
Gateway and Resource service. I tried to obtain the same result while building the current project.
Also, I use Redis for storing user tokens.

## ToDos:

- Test the jwt-jwk url for Spring gateway
- Scavenge the OAuthRemoteTokenService
- Move Role enum to a dedicated library
- Define the logic for the FilterWithoutJwt filter in gateway-service
- Redefine user creation while giving away oauth tokens
- Create a migration tool, for instance FlyWay with LiquiBase
- Create the Inbox and FileStorage services for storing temporary and permanent files accordingly
- Clean up unnecessary code