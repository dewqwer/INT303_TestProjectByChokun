<%-- 
    Document   : TestOtherLanguage
    Created on : Nov 13, 2018, 11:07:17 AM
    Author     : Dew2018
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        ${test.street}
        <form action="Register" method="post" accept-charset="utf-8">
            Email: <input type="email" name="email" required>
            <br>
            Password: <input type="password" name="password" required>
            <br>
            First name :<input type="text" name="firstName" required>
            <br>
            Last name :<input type="text" name="lastName" required>
            <br>
            Phone :<input type="tel" name="phone" required>
            <br>
            
            Address:
            Address No: <input type="text" name="addressNo" required>
            <br>
            Street: <input type="text" name="street" required>
            <br>
            Alley (ซอย): <input type="text" name="alley" required>
            <br>
            Subdistrict: <input type="text" name="subdistrict" required>
            <br>
            District: <input type="text" name="district" required>
            <br>
            Province: <input type="text" name="province" required>
            <br>
            Postcode: <input type="text" name="postcode" required>
            <br>
            <input type="submit" value="Confirm">
        </form>
    </body>
</html>
