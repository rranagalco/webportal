<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Portal Dashboard</title>

<link rel="stylesheet" href="bootstrap-dialog.css">

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

<link rel="stylesheet" href="order_list_display.css">

<style media="screen" type="text/css">
.fs12px {
   font-size: 12px;
}
.fs14px {
   font-size: 12px;
}
</style>

</head>

<script src="bootstrap-dialog.js"></script>

<body style="padding-top: 1px;">

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

    <h2 class="text-primary signin-header"><center><h2>Search results: </center></h2>
               
    <div class="tab-content">

		<c:if test="${messageToUser != null}">
			<div class="alert alert-success">
		  		${messageToUser}
			</div>
		</c:if>

        <!-- Subbarao - begin my orders tab -->
        <div class="panel-group" id="accordion">
            <div  class="panel panel-default titlePanel bluebg straightbg"  >
                <div class=" panel-title row paddingA">
                    <div class="fs14px col-sm-1">Customer#
                    </div>
                    <div class="fs14px col-sm-1">Contact#
                    </div>
                    <div class="fs14px col-sm-2">Company Name
                    </div>
                    <div class="fs14px col-sm-1">First Name, Last Name
                    </div>
                    <div class="fs14px col-sm-2">E-Mail
                    </div>
                    <div class="fs14px col-sm-1">Terms
                    </div>
                    <div class="fs14px col-sm-1">Web Account?
                    </div>
                    
                    <div class="fs14px col-sm-1">Order No.
                    </div>
                    <div class="fs14px col-sm-1">On credit hold?
                    </div>
                    <div class="fs14px col-sm-1">On hold?
                    </div>

                </div>
            </div>

            <div  class="panel panel-default straightbg" style="padding-top: 0px !important; margin-top: 0px !important; cursor:default;">
				<c:if test="${dashboardAL != null}">
					<c:forEach items="${dashboardAL}" var="dashboard">
                    <!-- Subbarao - Order S -->
                    <div class="panel-heading whitebg" style="cursor:default;">
                        <h4 class="panel-title row">
						<div class="fs12px col-sm-1" id="cust_num">${dashboard.cust_num}
						</div>
						<div class="fs12px col-sm-1" id="cust_num">${dashboard.cont_no}
						</div>
						<div class="fs12px col-sm-2" id="name">${dashboard.name}
						</div>
						<div class="fs12px col-sm-1" id="co_name_l" style="word-wrap: break-word;">${dashboard.co_name_f},${dashboard.co_name_l}
						</div>
						<div class="fs12px col-sm-2" id="e_mail_address" style="word-wrap: break-word;">${dashboard.e_mail_address}
						</div>
						<div class="fs12px col-sm-1" id="terms">${dashboard.terms}
						</div>
						<div class="fs12px col-sm-1" id="hasWebAccount">${dashboard.hasWebAccount}
						</div>

						<c:choose>
							<c:when test="${(dashboard.order_num != null) && (dashboard.order_num != '')}">
								<div class="fs12px col-sm-1" id="order_num">${dashboard.order_num}
								</div>
								<div class="fs12px col-sm-1" id="on_cr_hold"><c:choose><c:when test="${dashboard.on_cr_hold == true}">Yes</c:when><c:otherwise>No</c:otherwise></c:choose>
								</div>
								<div class="fs12px col-sm-1" id="on_hold"><c:choose><c:when test="${dashboard.on_hold == true}">Yes</c:when><c:otherwise>No</c:otherwise></c:choose>
								</div>
							</c:when>
							<c:otherwise>
								<div class="fs12px col-sm-1" id="order_num">
								</div>
								<div class="fs12px col-sm-1" id="on_cr_hold">
								</div>
								<div class="fs12px col-sm-1" id="on_hold">
								</div>
							</c:otherwise>
						</c:choose>

                        </h4>
                    </div>
                    <!-- Subbarao - Order E -->
					</c:forEach>
				</c:if>
			</div>
    	</div>

        <!-- Subbarao - end my orders tab -->
        
        <form role="form" action="/portal/controller?formFunction=DashboardSearch" id="DashCustSearch" class="form-horizontal">
	        <div class="col-xs-12 col-sm-11 col-md-7 col-md-offset-4 col-lg-4 input-group">
	           <button id="createAccountButton" class="btn btn-block btn-primary" type="submit">Search Customer Database Again</button>
	           </div>
	        </div>
		    <input type="hidden" name="formFunction" value="DashboardSearch">

		    <input type="hidden" name="cust_num" value="${cust_num}">
		    <input type="hidden" name="name" value="${name}">
		    <input type="hidden" name="co_name_f" value="${co_name_f}">
		    <input type="hidden" name="e_mail_address" value="${e_mail_address}">
		    <input type="hidden" name="order_num" value="${order_num}">
		</form>	

    </div>
    </div><!--end the row-->
    </div><!--end the container-->

   </main>
   </div>
</body>
</html>


