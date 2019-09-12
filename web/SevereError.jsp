<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <meta charset="utf-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
   <meta name="description" content="">
   <meta name="author" content="">

   <title>Error Page</title>

   <!-- CSS -->
   <link rel="stylesheet" type="text/css" href="css/bootstrap.min.css" media="screen">

   <!-- Custom styles for this template -->
   <link rel="stylesheet" type="text/css" href="css/SignIn.css" media="screen">

   <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
   <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
   <!-- <script src="./signin_files/ie-emulation-modes-warning.js"></script> -->

   <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
   <!--[if lt IE 9]>
     <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
     <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
   <![endif]-->
</head>

   <body cz-shortcut-listen="true">

      <div class="container">
		 <div class="alert alert-danger">
    	 	<strong>Error!</strong> Incident Number: ${incidentNumber}. There is a problem with your account. Please call 248 542 9090 for support, or
    	 	send an email to WebReport@Galco.com. Please provide us the incident number (${incidentNumber}).
  		 </div>
     </div>

<!-- /container -->


    <script src="js/jquery-1.11.1.min.js"></script>
    <script src="js/jquery.validate.min.js"></script>
    <script src="js/additional-methods.min.js"></script>


</body>
</html>
