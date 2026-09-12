# Placelyt

> Tell us where you are. Tell us where you want to go. We'll show you the path.

Placelyt is a placement tracking and career roadmap platform designed to help students discover relevant internship and job opportunities and manage their placement journey.

The project is actively under development, with the current implementation focused on building a structured backend for authentication, student profiles, companies, jobs, eligibility, and applications.

## Overview

Finding relevant internships and jobs can be difficult for students because opportunities are spread across different platforms and it can be difficult to determine which opportunities are relevant and trustworthy.

Placelyt aims to bring opportunity discovery, student information, eligibility, applications, and career planning into a single platform.

The current backend exposes REST APIs for managing users, student profiles, companies, jobs, skills, and applications.

## Current Features

- User registration and authentication
- JWT-based authentication
- BCrypt password hashing
- Student profile management
- Education and experience management
- Skills and preferences
- Company management
- Job management
- Job eligibility based on branches, degree, CGPA, and graduation year
- Job application management
- Request validation
- Business-rule validation
- Centralized exception handling
- Ownership checks for protected resources
- RESTful API architecture

## Tech Stack

### Backend

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JJWT 0.12.6
- Spring Validation
- Maven

### Database

- MySQL
- Hibernate / JPA

### Development

- Git
- GitHub

## Architecture

Placelyt follows a layered backend architecture:

```text
Client
  |
  v
Controller Layer
  |
  v
Service Layer
  |
  v
Repository Layer
  |
  v
MySQL Database
