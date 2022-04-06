Requirements: JDK 11+, Kotlin 1.6.10+

How to run tests
* Use gradlew with `./gradlew clean build` command
* or use IDEA junit run configuration
* or use docker to run tests (will fail on Apple Silicon, because it requires another image):
  * open console in root project directory
  * `docker build --tag api-tests . `
  * `docker run api-tests`

Tech stack:
* RestAssured as REST client testing library
* Kotest as a test framework (with junit runner)


TODOs:
* Move endpoints to enum
* Move to app project as sub-package and run with testcontainers
* Add linter exception for RestAssured statements indents
* Move all data to generators (could be extended with faker)
* Extend data validation test
* Add swagger schema check

Test cases: 
In the project I just cover only basic behaviour of a part of Pets endpoints.
I cover only described in swagger status responses with small extension.
There are also at least 3 bugs and at least 2 strange behaviour that I mention in tests.
Failed tests are skipped in the run now.
Because of time and the app limitation, it makes no sense to cover it deeper, but I could explain how it could work in real app.

Comments

There are few important marks about tests project:
1. Because of `swagger-petstore` app uses in-memory storage to keep all the records, there is no easy to way to ensure some endpoints works fine. 
For example, if we create a new item with some POST request, there is only one way to ensure it has been really created - send GET request.
This is not a good practice, because of any issue related to GET request will break all the tests that uses it. In real life, we usually could have another ways to check result - with requests to DB for example.
2. So, when we could make check of data with some other ways (DB, Message Broker / Queue or other), we could separate tests to 2 types: endpoints tests with deep coverage and user scenarios tests (kind of e2e tests). 
3. The good practice is to keep tests close to the app source code.
In this case, I would prefer to keep tests as subpackage of `swagger-petstore` app.
It guarantees we will have the same version of tests / app code for any branch and easy to integrate with each other, for example, with tools like testcontainers and make tests part of app CI pipeline
