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
   <title>Portal Dashboard - Order Search</title>
   <style>

   .featherlight {
      background: rgba(0, 0, 0, 0.8);
   }

   </style>
</head>

<link rel="stylesheet" href="/htdocs/css/bootstrap.css">
<link rel="stylesheet" href="/htdocs/css/bootstrap.less">
<link rel="stylesheet" href="/htdocs/css/galco.css">
<link rel="stylesheet" href="/htdocs/css/jquery-ui.css">
<link rel="stylesheet" href="/htdocs/css/jquery-ui.min.css">
<link rel="stylesheet" href="/htdocs/css/newlyadded.css">
<link rel="stylesheet" href="zold_css/dashboard.css">

<script src="js/jquery-3.2.1.slim.min.js"></script>
<script src="js/popper.min.js" ></script>
<script src="js/bootstrap.min.js"></script>


<body style="padding-top: 1px;" cz-shortcut-listen="true" class="create-account">
   <!--#include virtual="/topnav.inc"-->
   <div class="middle-sec">
      <main id="panel">
         <div class="container">
            <div class="row">
              <div class="col-xs-12 col-sm-6 col-md-12">
                  <center><h1>Portal Dashboard</h1></center>
               </div>
            
               <div class="col-xs-12 col-sm-6 col-md-12">
                  <jsp:include page="DashboardNavbar.html" />
               <div>    
                        
            
               <div class="col-xs-12 col-sm-6 col-md-12">
                  <h2 class="text-primary signin-header"><center>Order Search</center></h2>
               </div>
               <div class="col-xs-12 col-sm-6 col-md-18">
                  <h3 class="text-primary signin-header"><center>Coming soon ....</center></h3>
               </div>
               
               <!-- 
               <div class="col-xs-12 col-sm-6">
                  <form role="form" action="/portal/controller?formFunction=DashboardSearch" id="DashboardSearch" class="form-horizontal">
                     <h3 class="text-primary signin-header">Enter your search criteria below</h3>
                     
					<c:if test="${messageToUser != null}">
						<div class="alert col-md-13 form-group alert-danger">
					  		${messageToUser}
						</div>
					</c:if>
	                     
                     <div class="form-group">
                        <label for="e_mail_address" class="control-label col-md-4 visible-md visible-lg">Email Address<i class="text-primary fa fa-envelope" aria-hidden="true"></i></label>
                        <div class="col-xs-12 col-sm-11 col-md-7 col-lg-8 input-group">
                           <div class="input-group-addon hidden-md hidden-lg"><i class="text-primary fa fa-envelope"></i></div>
                           <input type="text" name="e_mail_address" id="e_mail_address" class="form-control input-lg" placeholder="Email Address" autofocus <c:choose><c:when test="${e_mail_address != null}">value="${e_mail_address}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     
                     <div class="form-group">
                        <div class="col-xs-12 col-sm-11 col-md-7 col-md-offset-4 col-lg-8 input-group">
                           <button id="createAccountButton" class="btn btn-block btn-primary" type="submit">Search Customer Database</button>
                        </div>
                     </div>
					 <input type="hidden" name="formFunction" value="DashboardSearch">	
					 <input type="hidden" name="subFunction" value="Conf">	
                  </form>
               </div>
               <div class="col-xs-12 col-sm-6 col-md-5 col-lg-5 col-lg-offset-1 create-account">
               </div>
               -->
            </div>
         </div>
      </main>
   </div>
   
   <script>

   jQuery(document).ready(function($) {
   });

   </script>
</body>

    <script>

	function pausecomp(millis) {
    	var date = new Date();
    	var curDate = null;
    	do { 
    		curDate = new Date(); 
    	} while(curDate-date < millis);
	}

	$("#createAccountButton").on('click', function() {
		if (($("#e_mail_address").val() == ""			) &&
			($("#cust_num").val() == ""			) &&
			($("#name").val() == ""		) &&
			($("#co_name_f").val() == ""		) &&
			($("#co_name_l").val() == ""			)    ) {
			Alert("Please enter some value into at least one field.");
	    	return false;
		} else {
			$("#DashboardSearch").submit();
	    	return false;
		}
	});    
    
    </script>
	
</html>

