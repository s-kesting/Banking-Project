# JwtTokenParser

add Authentication authentication to a parameter to a function in your controller class to get the jwtToken of the user that called that token:
getXbyY(Authentication authentication)

use jwtTokenParser to get user data from the token:
        JwtTokenParser parser = new JwtTokenParser();
        int userId = parser.getTokenUserId(authentication);
