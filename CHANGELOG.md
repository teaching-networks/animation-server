# Changelog

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
