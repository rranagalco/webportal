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

   <title>Reset forgotten password</title>

   <jsp:include page="/htdocs/topscripts.inc" />

   <!-- CSS -->

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

   <jsp:include page="/htdocs/topnav.inc" />

<div class="middle-sec">
 <main id="panel">
 <div class="container">
 <div class="row-fluid">
 <div class="col-sm-12" style="padding-top: 45px; padding-bottom: 45px; padding-right: 0px; padding-left:0px;">

<div class="col-sm-6 col-sm-offset-3">

		<form id="SignInForm" action="/portal/controller" style="border: 1px solid #ccc">         
            <h2 class="form-signin-heading text-center">Please select your User ID</h2>

		    <c:if test="${messageToUser != null}">
		        <div class="alert alert-danger">
		            <strong>Error!</strong> ${messageToUser}
		        </div>
		    </c:if>

			<div class="row">
		    	<div class="form-group text-center col-sm-8 col-sm-offset-2" >
		        	<div><br>
		            	<p>We found multiple users who are using the same email address. Please select your user name from the following list, and click Continue.</p>
		          	</div>

					<div style="border: 1px solid #CCC" class="text-left">
						<div style="border: 15px solid #FFF" class="text-left">
							<c:forEach items="${usernames}" var="username">
								<div class="radio">
					  				<label><input type="radio" name="username" value="${username}">${username}</label>
								</div>
							</c:forEach>
						</div>
					</div>

		          	<div><br>
				  		<button class="btn btn-block btn-primary btn-block" type="submit">Continue</button><br>
		          	</div>
			</div>
		    <div class="col-sm-2"></div>
		    </div>
		    </div>
		    
        	<input type="hidden" name="formFunction" value="ForgotPasswordUsernameSelection"/>		    

         </form>

</div>
<div class="col-sm-3">
</>
</div>
</main>
</div>
</div>
</div>


    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="./signin_files/ie10-viewport-bug-workaround.js"></script>    
    
    <script src="jscripts/jquery-1.11.1.min.js"></script>
    <script src="jscripts/jquery.validate.min.js"></script>
    <script src="jscripts/additional-methods.min.js"></script>
    <script> 

<jsp:include page="/htdocs/tfoot.inc" />

<jsp:include page="/htdocs/bottomscripts.inc" />


</body>
</html>
