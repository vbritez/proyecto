<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setLocale value="${iBean.locale}" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="web.client.login.screen.header.CSTigoName" /> - Error 401</title>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/screenFeatures.css" />
    <link type="image/x-icon" rel="shortcut icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico" />
</head>
<body class="bodyGray">
    <!-- //////////////////////// HEADER //////////////////////// -->
    <div id="header">
        <div id="header_in">
            <div id="logo">
                <img src="${pageContext.request.contextPath}/resources/images/logoTigo.png" alt="<fmt:message key="web.client.login.screen.header.CSTigoName" />" />
            </div>
            <div id="logo_title">
                <fmt:message key="web.client.login.screen.header.CSTigoName" />
            </div>
        </div>
    </div>
    <!-- //////////////////////// END HEADER //////////////////////// -->
    <!-- //////////////////////// CONTAINER //////////////////////// -->
    <div id="container_index">
        <div id="login">
            <div id="leftsmall"></div>
            <div id="centersmall">
                <div class="title">
                    <p>
                        <img src="${pageContext.request.contextPath}/resources/images/forbidden.png" width="48" height="48" alt="<fmt:message key="web.client.errorpages.title.AccessDenied401" />" align="absmiddle" /> <strong><fmt:message key="web.client.errorpages.title.AccessDenied401" /></strong>
                    </p>
                </div>
                <div align="center">
                    <table class="table_1" width="100%">
                        <tr>
                            <td class="txt_1">
                                <fmt:message key="web.client.errorPages.message.HasBeenDenied" />
                            </td>
                        </tr>
                        <tr>
                            <td class="txt_2" style="padding-bottom: 30px;">
                                <fmt:message key="web.client.errorPages.message.SystemAdministrator" />
                            </td>
                        </tr>
                        <tr>
                            <td class="txt_2">
                                <fmt:message key="web.client.errorPages.message.GoTo" />
                                <a href="${pageContext.request.contextPath}/domain/cstigowebclient/home.jsf">home</a>
                            </td>
                        </tr>
                        <tr>
                            <td class="txt_2">
                                <fmt:message key="web.client.errorPages.message.GoTo" />
                                <a href="${pageContext.request.contextPath}/loginclient.jsf">login</a>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <div id="rightsmall"></div>
        </div>
    </div>
    <!-- //////////////////////// END CONTAINER //////////////////////// -->
</body>
</html>
