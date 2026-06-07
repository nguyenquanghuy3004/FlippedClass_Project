# Thymeleaf-only frontend structure

This project now treats Spring Boot + Thymeleaf as the only UI delivery path.
React, Vite, `package.json`, `dist`, and root SPA entry files are not part of the application structure.

## Runtime model

- `src/main/java/.../controller/TemplateController.java` maps browser routes to Thymeleaf views.
- `src/main/resources/templates` contains server-rendered HTML templates.
- `src/main/resources/static/assets` contains CSS, JavaScript, images, fonts, and manifest files.
- Dynamic page data is loaded either by Thymeleaf model attributes or by browser `fetch()` calls to `/api/**`.

## Suggested template layout

```text
src/main/resources/templates/
  index.html                 # FlippedClass landing page
  Authen/
    signin.html              # login/signin page
    signup.html              # register/signup page
  student/
    student-dashboard.html   # student dashboard
    take-quiz.html           # quiz attempt page
  lecturer/
    quizzes.html             # quiz management
    quiz-builder.html        # question builder
  404-error.html             # error page
```

## Static assets

```text
src/main/resources/static/assets/
  css/
    home.css
    main.css
    auth.css
    quizzes.css
    quiz-builder.css
  js/
    main.js
  images/
```

## Routing aliases

- `/` -> `index`
- `/login` and `/signin` -> `Authen/signin`
- `/register` and `/signup` -> `Authen/signup`
- `/student/dashboard` -> `student/student-dashboard`
- `/student/take-quiz` -> `student/take-quiz`
- `/lecturer/quizzes` -> `lecturer/quizzes`
- `/lecturer/quizzes/{id}/builder` -> `lecturer/quiz-builder`

## Rule for future UI work

Do not add React/Vite entry points back into the project root. New UI pages should be added as Thymeleaf templates and mapped from a Spring MVC controller.
