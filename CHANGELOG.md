# Changelog

### 3.1.2
- Added the Levenshtein Calculator which calculates the "edit distance" between two words. This is used to group
  misspelled words together, for example "Weihnachten" and "Weinachten".
- Added two websocket endpoints for the frontend to receive updates about the poll status.  

### 3.1.1
- Added a timer that sets all polls that have been active for longer than 60 minutes to inactive

### 3.1.0
- Added the open-question-poll functionality.
- Rearranged database model to have the superclasses YAARSPoll and YAARSAnswer for easily adding new Poll models.
- Added the endpoint for the open-question-poll (own model and dao, own subscription manager).

### 3.0.0
- Added the yaars related endpoint
- Added database Tables and relationships
- Added REST endpoints for all Tables
- Implemented websocket endpoints for yaars related communication 

### 2.2.0
- Added settings controller

### 2.1.0 - Maintenance Update
- Migration from Java 8 to Java 13
- Updated Javalin + all used dependencies
- Needed to get rid of pac4j for authentication -> Wrote new JWT authentication mechanics

## 2.0.0
- Animation groups
- Animation info management (Editing title, URL, short description and description of any animation)
- Overhauled generic management GUI (used by the user, animation and group management components)
- Fixed critical security issues
- Fixed encoding problem. Now all JSONs returned by the API are encoded in `UTF-8`.

### 0.1.0
- First productive version of the server (Hooray!)
- Featuring:
    - REST API
    - User management
    - Animation management
    - Authentication using JSON Web Tokens and Basic Authentication
