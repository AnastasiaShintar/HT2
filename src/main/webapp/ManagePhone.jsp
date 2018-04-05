<?xml version="1.0" encoding="UTF-8" ?>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.epam.tat19.app.Person" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Управление данными о телефонном номере</title>
</head>
<body>

<%
    HashMap<String, String> jsp_parameters = new HashMap<String, String>();
    String error_message = "";
    Person current_person = (Person)request.getAttribute("current_person");

    if (request.getAttribute("jsp_parameters") != null) {
        jsp_parameters = (HashMap<String, String>) request.getAttribute("jsp_parameters");
    }

    error_message = jsp_parameters.get("error_message");
%>

<form action="<%=request.getContextPath()%>/" method="post">
    <input type="hidden" name="id" value="<%=request.getParameter("id")%>"/>
    <input type="hidden" name="phoneid" value="<%=request.getParameter("phoneid")%>"/>
    <table align="center" border="1" width="70%">
        <%
            if ((error_message != null) && (!error_message.equals(""))) {
        %>
        <tr>
            <td colspan="2" align="center"><span style="color:red"><%=error_message%></span></td>
        </tr>
        <%
            }
        %>
        <tr>
            <td colspan="2" align="center">Информация о телефоне владельца:&nbsp;<%=jsp_parameters.get("long_name")%></td>
        </tr>
        <tr>
            <td>Номер:</td>
            <td><input type="text" name="phonenumber" value="<%=request.getAttribute("phone_number")%>"/></td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                <input type="submit" name="<%=jsp_parameters.get("next_action")%>"
                       value="<%=jsp_parameters.get("next_action_label")%>"/><br/>
                <a href="<%=request.getContextPath()%>/?action=edit&id=<%=request.getParameter("id")%>">Вернуться к данным о человеке</a>
            </td>
        </tr>
    </table>
</form>
</body>
</html>