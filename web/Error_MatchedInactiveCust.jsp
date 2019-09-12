<!DOCTYPE html>
<html lang="en-us">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <meta charset="utf-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <meta name="description" content="Galco Industrial Electronics | Sign in to account page">
   <meta name="author" content="Nicholas Cirullo">
   <title>Create Account | Sign In</title>
   <!--#include virtual="/topscripts.inc"-->
   <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
   <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
   <!-- <script src="./signin_files/ie-emulation-modes-warning.js"></script> -->
</head>

<jsp:include page="/htdocs/topscripts.inc" />
<jsp:include page="/htdocs/topnav.inc" />

<body class="manage-account confirm-account">
   <div class="middle-sec">
      <main id="panel">
         <div class="container">
            <div class="row">
               <div class="col-xs-12">
                  <h1>Create Account</h1>
               </div>
               <div class="row">
                  <div class="col-xs-12 text-center">
                     <h3 style="color:red;">Thank you for trying to register for a Galco account!</h3>
                     <p style="color:red;">An error occurred while trying to register you as a new user. Please contact customer service at 800-575-5562.</p>
                  </div>
               </div>
            </div>
         </div>
         <!-- End Container -->
      </main>
      <!-- End Panel -->
   </div>
   <!-- End of middle sec-->
   <!--start of btm sec-->
   <!--#include virtual="/tfoot.inc"-->
   <!--end of btm sec-->
   <!--#include virtual="/bottomscripts.inc"-->
</body>

<jsp:include page="/htdocs/tfoot.inc" />
<jsp:include page="/htdocs/bottomscripts.inc" />
	
</html>

