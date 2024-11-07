# Basic Demo Application Template (Java)

This template repository maintains a BigID basic demo application (backend only).
The application can be taken and used as a reference for building your own custom application.

## Prerequisites

- In order to build and run the application, you will need Docker.

## Setting git hooks

- In order to set our custom git hooks in order to validate app before commit - [click here](hooks/README.md)

## Running the application
* In order to create the docker image, you should run the following command when you are in the root directory of the repository:

``docker build -t bigexchange/basic-demo-application:latest .`` 

* Once the image is created, you should run the image with the command:

``docker run -itd -p 8083:8083 bigexchange/basic-demo-app:latest``

The container should be then up and running, and can be connected in BigID's environment with the application framework.
