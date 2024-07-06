About User Service:
```
1. Social Login Ex. Google, Facebook, Github
2. User Sign up
3. User Sign In
```
Application Details:
```
1. server port: 8080
2. Used Java Version 21
3. Spring Security , Filters, Oauth2
4. Database: MySql
5. Swagger URL: http://localhost:8080/swagger-ui/index.html
6. Actuator URL: http://localhost:8080/actuator/health
```
Application APIs:
```
1. Signup: curl --location 'http://localhost:8080/auth/sign-up' \
--header 'Content-Type: application/json' \
--data-raw '{
    "firstName": "Narahari",
    "lastName": "Nayak",
    "username": "asd890",
    "imageUrl": "https://locadfsd.png",
    "password": "n@ri123"   
}'

2. SignIn: curl --location 'http://localhost:8080/auth/sign-in' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "n16@gmail.com",
    "password": "n"
}'

3. Me: curl --location 'localhost:8080/user/me' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpQGdtYWlsLmNvbSIsImlhdCI6MTcwNDAyNzEzNSwiZXhwIjoxNzA0MDI4NTc1fQ.vBcrBA5TQub6hUp0cqFTsP3M44oGLkh293aR7FliSaU'

4. OAuth Login: http://localhost:8080/oauth_login
```
