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

   <title>Signin</title>

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

		<form id="SignInForm" action="/portal/controller?formFunction=SignUp" style="border: 1px solid #ccc">         
            <h2 class="form-signin-heading text-center">Please Sign In</h2>

			<c:if test="${messageToUser != null}">
			
				<c:choose>
  					<c:when test="${messageIsNotAnError == true}">
				<div class="alert">
					</c:when>
					<c:otherwise>
				<div class="alert alert-danger">
					</c:otherwise>
				</c:choose>			
			
			  		${messageToUser}
				</div>
			</c:if>

		    <div class="row">
		        <div class="form-group text-center col-sm-8 col-sm-offset-2" >
		          
		           <div class="input-group">
		             <span class="input-group-addon text-center"><i class="glyphicon glyphicon-envelope"></i> </span>
		             <input name="userName" type="email" id="inputEmail" class="form-control" placeholder="Email address" autofocus="" <c:choose><c:when test="${userName != null}">value="${userName}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
		
		          </div>
		          <div class="input-group">
		             <span class="input-group-addon text-center"><i class="glyphicon glyphicon-lock"></i> </span>
		            <input name="password" type="password" id="inputPassword" class="form-control" placeholder="Password">
		          </div>       
		          <div><br>
		            <button class="btn btn-block btn-primary btn-block" type="submit">Sign In</button><br>
		           <p>We upgraded our web login system to a new web portal. Please use your email address and password to sign in.</p>
		           <a class="form-signin-heading" href="/portal/controller?formFunction=ForgotPassword">Forgot your password?</a><br>
		           <p>No account?&nbsp&nbsp&nbsp<a class="form-signin-heading" href="controller?formFunction=SignUp">Register one!</a></p>
		          </div>
		    </div>
		    <div class="col-sm-2"></div>
		    </div>
		    </div>
		    
			<input type="hidden" name="rfq" <c:choose><c:when test="${rfq != null}">value="${rfq}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>				    		    
			<input type="hidden" name="checkoutstep" <c:choose><c:when test="${checkoutstep != null}">value="${checkoutstep}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>				    		    

         </form>

<br>

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

    $( "#SignInForm" ).validate({
      rules: {
		userName: {
			required: true,
			email: true,
			maxlength: 60			
		},
		password: {
			required: true,
			minlength: 7,
			maxlength: 20
		}
      },

      messages: {
			userName: {
				required: "Please enter a valid user name, this typically is your email.",
				maxlength: "User name can't have more than 60 characters."
			},			
			password: {
				required: "Please provide a password",
				minlength: "Your password must be at least 7 characters long",
				maxlength: "Your password can't have more than than 20 characters"
			}
	  }

      
    });
    </script>    

<jsp:include page="/htdocs/tfoot.inc" />

<jsp:include page="/htdocs/bottomscripts.inc" />


</body>
</html>
