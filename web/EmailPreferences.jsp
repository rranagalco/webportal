<!DOCTYPE html>
<html lang="en-us">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <meta charset="utf-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <meta name="description" content="Galco Industrial Electronics | My Preferences">
   <meta name="author" content="Nicholas Cirullo">
   <title>My Preferences</title>
   <!--#include virtual="/topscripts.inc"-->
   <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
   <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
   <!-- <script src="./signin_files/ie-emulation-modes-warning.js"></script> -->
</head>

<jsp:include page="/htdocs/topscripts.inc" />
<jsp:include page="/htdocs/topnav.inc" />
<script src="countries.js"></script>
<script src="address-verification-portal.js"></script>

<style>
.form-group.subbrequired .control-label:before {
  content:"*";
  color:red;
}
</style>

<body cz-shortcut-listen="true" class="create-account create-billing">
   <!--#include virtual="/topnav.inc"-->
   <div class="middle-sec">
      <main id="panel">
         <div class="container">
            <div class="row">
               <div class="col-xs-12 col-sm-6 col-md-8">
                  <h1>My Preferences</h1>
               </div>

               <div class="col-xs-12 col-sm-6">


         <form action="#" name="preferences_form" id="preferences_form">
            <div class="row">
               <div class="col-xs-12">
                  <h4>Select the topics you want to receive email communications</h4>
                  <ul>
                     <li>
                        <input type="checkbox" name="product_promotion" id="product_promotion" value="y" <c:if test="${product_promotion}">checked="checked"</c:if> <c:if test="${active == false}">disabled="disabled"</c:if>>
                        <label for="product_promotion" <c:if test="${active == false}">style="color:gray"</c:if>>Product Promotions</label>
                     </li>
                     <li>
                        <input type="checkbox" name="repair_services" id="repair_services" value="y" <c:if test="${repair_services}">checked="checked"</c:if> <c:if test="${active == false}">disabled="disabled"</c:if>>
                        <label for="repair_services" <c:if test="${active == false}">style="color:gray"</c:if>>Repair Services</label>
                     </li>
                     <li>
                        <input type="checkbox" name="eng_services" id="eng_services" value="y" <c:if test="${eng_services}">checked="checked"</c:if> <c:if test="${active == false}">disabled="disabled"</c:if>>
                        <label for="eng_services" <c:if test="${active == false}">style="color:gray"</c:if>>Engineered Systems</label>
                     </li>
                     <li>
                        <input type="checkbox" name="product_reviews" id="product_reviews" value="y" <c:if test="${product_reviews}">checked="checked"</c:if> <c:if test="${active == false}">disabled="disabled"</c:if>>
                        <label for="product_reviews" <c:if test="${active == false}">style="color:gray"</c:if>>Product Reviews</label>
                     </li>
                     <li>
                        <input type="checkbox" name="product_recommendations" id="product_recommendations" value="y" <c:if test="${product_recommendations}">checked="checked"</c:if> <c:if test="${active == false}">disabled="disabled"</c:if>>
                        <label for="product_recommendations" <c:if test="${active == false}">style="color:gray"</c:if>>Product Recommendations</label>
                     </li>
                     <li>
                        <input type="checkbox" name="surveys" id="surveys" value="y" <c:if test="${surveys}">checked="checked"</c:if> <c:if test="${active == false}">disabled="disabled"</c:if>>
                        <label for="surveys" <c:if test="${active == false}">style="color:gray"</c:if>>Surveys</label>
                     </li>
                     <li>
                        <input type="checkbox" name="new_videos" id="new_videos" value="y" <c:if test="${new_videos}">checked="checked"</c:if> <c:if test="${active == false}">disabled="disabled"</c:if>>
                        <label for="new_videos" <c:if test="${active == false}">style="color:gray"</c:if>>New Videos</label>
                     </li>
                  </ul>
               </div>
            </div>
            <div class="row">
               <div class="col-xs-12">
                  <h4>How often would you like to receive emails?</h4>
                  <ul>
                     <li>
                        <input type="checkbox" name="receive_daily" id="receive_daily" value="y" <c:if test="${receive_daily}">checked="checked"</c:if> <c:if test="${active == false}">disabled="disabled"</c:if>>
                        <label for="receive_daily" <c:if test="${active == false}">style="color:gray"</c:if>>Daily</label>
                     </li>
                     <li>
                        <input type="checkbox" name="receive_weekly" id="receive_weekly" value="y" <c:if test="${receive_weekly}">checked="checked"</c:if> <c:if test="${active == false}">disabled="disabled"</c:if>>
                        <label for="receive_weekly" <c:if test="${active == false}">style="color:gray"</c:if>>Weekly</label>
                     </li>
                     <li>
                        <input type="checkbox" name="receive_monthly" id="receive_monthly" value="y" <c:if test="${receive_monthly}">checked="checked"</c:if> <c:if test="${active == false}">disabled="disabled"</c:if>>
                        <label for="receive_monthly" <c:if test="${active == false}">style="color:gray"</c:if>>Monthly</label>
                     </li>
                  </ul>
               </div>
            </div>
            
            <c:choose>
				<c:when test = "${active == true}">
		            <div class="row">
		               <div class="col-xs-12">
		                  <h4>Unsubscribe from all email communications</h4>
		                  <ul>
		                     <li>
		                        <input type="checkbox" name="unsubscribe" id="unsubscribe" value="y" <c:if test="${unsubscribe}">checked="checked"</c:if>>
		                        <label for="unsubscribe">I no longer wish to receive any future emails</label>
		                     </li>
		                  </ul>
		               </div>
		            </div>
				</c:when>
				<c:otherwise>
		            <div class="row">
		               <div class="col-xs-12">
		                  <h4>Resubscribe to email communications</h4>
		                  <ul>
		                     <li>
		                        <input type="checkbox" name="unsubscribe" id="unsubscribe" value="y" <c:if test="${unsubscribe}">checked="checked"</c:if>>
		                        <label for="unsubscribe">I wish to receive future emails</label>
		                     </li>
		                  </ul>
		               </div>
		            </div>
				</c:otherwise>
			</c:choose>
      
            <div class="row">
               <div class="col-xs-12">
                  <button type="submit" class="btn btn-primary">Update Preferences</button>
               </div>
            </div>
     		
     		<input type="hidden" name="formFunction" value="EmailPreferencesProcess">
         </form>

               </div>
            </div>
         </div>
      </main>
   </div>

</body>

<jsp:include page="/htdocs/tfoot.inc" />
<jsp:include page="/htdocs/bottomscripts.inc" />

</html>























