<!DOCTYPE html>
<html lang="en-us">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <meta charset="utf-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <meta name="description" content="Galco Industrial Electronics | Enter Billing Address">
   <meta name="author" content="Nicholas Cirullo">
   <title>Edit Billing Addresses</title>
   <!--#include virtual="/topscripts.inc"-->
   <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
   <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
   <!-- <script src="./signin_files/ie-emulation-modes-warning.js"></script> -->
</head>

<jsp:include page="/htdocs/topscripts.inc" />
<jsp:include page="/htdocs/topnav.inc" />
<script src="countries.js"></script>
<script src="address-verification-portal.js"></script>

<body class="checkout">
   <!--#include virtual="/topnav.inc"-->
   <!--start of middle sec-->
   <div class="middle-sec">
      <main id="panel">
      
         <div class="container">
            <div class="row">
               <div class="col-xs-12">
                  <div class="selectShippingContent container">

				<c:if test="${messageToUser != null}">
					<div class="row alert alert-success">
				  		${messageToUser}
					</div>
				</c:if>      

                     <div class="row">
                        <h2 class="col-xs-12">Select a Billing Address</h2>
                     </div>

			<c:choose>
    			<c:when test="${not empty dfltCustbill}">

                     <div class="addressRow row selectedAddress" data-shipto="02">
                        <div class="col-xs-12">
                           <h3>Selected Billing Address</h3>
                        </div>
                        <div class="backgroundColor">
                           <div class="col-xs-12 nameBlock">
                              <div>${dfltCustbill.name}</div>
                           </div>
                           <div class="col-xs-12 col-sm-9 addressBlock">
                              <div>${cust.name}</div>
                              <div>${dfltCustbill.address}</div>
                              <div>${dfltCustbill.address2}</div>
                              <div>
                                 <span>${dfltCustbill.city}</span>,
                                 <span>${dfltCustbill.state}</span>
                                 <span>${dfltCustbill.zip}</span>
                              </div>
                              <div>${dfltCustbill.country}</div>
							  
							  <c:if test="${profileEditing == null}">
                                  <a href="/portal/controller?formFunction=SelectCustbill&billto_num=${dfltCustbill.billto_num}" class="btn btn-primary-outline">Use selected address</a>
							  </c:if>
                           </div>
                           <div class="col-xs-12 col-sm-3 alterationLinks text-right">
                              <a href="/portal/controller?formFunction=EditCustbill&billto_num=${dfltCustbill.billto_num}"<c:if test="${profileEditing != null}">&profileEditing=y</c:if>>Update</a>
                              <br>
                              <a href="/portal/controller?formFunction=RemoveCustbill&billto_num=${dfltCustbill.billto_num}">Remove</a>
                           </div>
                        </div>
                     </div>

    			</c:when>
    			<c:otherwise>

                     <div class="addressRow row selectedAddress" data-shipto="02">
                        <div class="col-xs-12">
                           <h3>Selected Billing Address</h3>
                        </div>
                        <div class="backgroundColor">
                           <div class="col-xs-12 nameBlock">
                              <div>${contact.co_name_f} ${contact.co_name_l}</div>
                           </div>
                           <div class="col-xs-12 col-sm-9 addressBlock">
                              <div>${cust.name}</div>
                              <div>${cust.address}</div>
                              <div>${cust.address2}</div>
                              <div>
                                 <span>${cust.city}</span>,
                                 <span>${cust.state}</span>
                                 <span>${cust.zip}</span>
                              </div>
                              <div>${cust.country}</div>
							  <c:if test="${profileEditing == null}">
                                  <a href="/portal/controller?formFunction=SelectCustbill&billto_num=" class="btn btn-primary-outline">Use selected address</a>
							  </c:if>
                           </div>
                        </div>
                     </div>    			

    			</c:otherwise>
			</c:choose>				


			<c:if test="${(not empty custbillAL) || (not empty dfltCustbill)}">
                     <div class="addressRow row " data-shipto="01">
                        <div class="col-xs-12">
                           <h3>Additional Billing Addresses</h3>
                        </div>

				<c:if test="${not empty dfltCustbill}">

                        <div class="backgroundColor">
                           <div class="col-xs-12 nameBlock">
                              <div>${contact.co_name_f} ${contact.co_name_l}</div>
                           </div>
                           <div class="col-xs-12 col-sm-9 addressBlock">
                              <div>${cust.name}</div>
                              <div>${cust.address}</div>
                              <div>${cust.address2}</div>
                              <div>
                                 <span>${cust.city}</span>,
                                 <span>${cust.state}</span>
                                 <span>${cust.zip}</span>
                              </div>
                              <div>${cust.country}</div>
                              
							  <c:choose>
							     <c:when test="${profileEditing == null}">
	                                <div class="makeDefault">
	                                   <input type="checkbox" name="makeDefaultBilling" id="makeDefaultBilling" billto_num="" value="01">
	                                   <label for="makeDefaultBilling">Make this my default billing address</label>
	                                </div>
							     </c:when>
							     <c:otherwise>
                                       <a href="/portal/controller?formFunction=ChangeDefaultCustbill&billto_num=" id="selectAddress" class="btn btn-primary-outline">Make this default address</a>
							     </c:otherwise>
							  </c:choose>
                              
							  <c:if test="${profileEditing == null}">
                                  <a href="/portal/controller?formFunction=SelectCustbill&billto_num=&makeDefault=n" id="selectAddress" class="btn btn-primary-outline">Select this address</a>
							  </c:if>
                           </div>
                        </div>
                        
				</c:if>
				
				<c:forEach items="${custbillAL}" var="custbill">

                        <div class="backgroundColor">
                           <div class="col-xs-12 nameBlock">
                              <div>${dfltCustbill.name}</div>
                           </div>
                           <div class="col-xs-12 col-sm-9 addressBlock">
                              <div>${cust.name}</div>
                              <div>${custbill.address}</div>
                              <div>${custbill.address2}</div>
                              <div>
                                 <span>${custbill.city}</span>,
                                 <span>${custbill.state}</span>
                                 <span>${custbill.zip}</span>
                              </div>
                              <div>${custbill.country}</div>
                              
							  <c:choose>
							     <c:when test="${profileEditing == null}">
                                    <div class="makeDefault">
                                       <input type="checkbox" name="makeDefaultBilling" id="makeDefaultBilling${custbill.billto_num}" billto_num="${custbill.billto_num}" value="01">
                                       <label for="makeDefaultBilling${custbill.billto_num}">Make this my default billing address</label>
                                    </div>
							     </c:when>
							     <c:otherwise>
                                       <a href="/portal/controller?formFunction=ChangeDefaultCustbill&billto_num=${custbill.billto_num}" id="selectAddress" class="btn btn-primary-outline">Make this default address</a>
							     </c:otherwise>
							  </c:choose>

							  <c:if test="${profileEditing == null}">
                                  <a href="/portal/controller?formFunction=SelectCustbill&billto_num=${custbill.billto_num}&makeDefault=n" id="selectAddress${custbill.billto_num}" class="btn btn-primary-outline">Select this address</a>
							  </c:if>
                           </div>
                           <div class="col-xs-12 col-sm-3 alterationLinks text-right">
                              <a href="/portal/controller?formFunction=EditCustbill&billto_num=${custbill.billto_num}"<c:if test="${profileEditing != null}">&profileEditing=y</c:if>>Update</a>
                              <br>
                              <a href="/portal/controller?formFunction=RemoveCustbill&billto_num=${custbill.billto_num}">Remove</a>
                           </div>
                        </div>

				</c:forEach>

                     </div>

			</c:if>

		 		 <form role="form" action="/portal/controller?formFunction=AddCustbill" id="SignUpBillForm" class="form-horizontal">
                     <div class="addAddress row bgPlusButton">
                        <button id="addOrUpdateAddressButton">
                           <span><i class="fa fa-plus-circle"></i>Add a Billing Address</span>
                        </button>
                     </div>
                     <input type="hidden" name="formFunction" value="AddCustbill">
                     
					 <c:if test="${profileEditing != null}">
                        <input type="hidden" name="profileEditing" value="y">				    		    
					 </c:if>  
					                          
				 </form>
                     
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

    <script>
	$("#addOrUpdateAddressButton").on('click', function() {
		if ($("#SignUpBillForm").valid() == true) {
			e.preventDefault();
			$("#SignUpBillForm").submit();
		}
	});
    </script>

    <script>
		$(function() {
			// Handler for .ready() called.
			// $('input').change	(
			// $('#makeDefaultBilling').change	(
			$('[id^=makeDefaultBilling]').change	(
				function() {
					// alert($(this).is('[disabled=disabled]'));
					// alert($(this).prop("disabled"));
					
			        if ($(this).is(':checked')) {
			            /*
			        	if ($("#profileEditing").val() == "y") {
			        		alert("profileEditing");
			        		window.location.replace("/portal/controller?formFunction=ChangeDefaultCustbill&billto_num=" + $(this).attr("billto_num"));
			        		return;
			        	}
			        	*/
			        
			        	if ($(this).is('[disabled=disabled]')) {
			        		// alert('disabled.');
			        	}
			        
						// $('input').attr('checked', false);					
						// $(this).attr('checked', true);
						
						// $("#makeDefaultBilling :checkbox:not(:checked)").attr('disabled', true);
						
						// $('input input[type=checkbox]').attr('disabled','true');

						$("input:checkbox:not(:checked)").attr('disabled','true');

				        var billto_num = $(this).attr("billto_num");
				        
				        var selectElement = $("#selectAddress" + billto_num);
				        
				        var oldhref = selectElement.attr("href");
						var newhref = oldhref.replace("makeDefault=n", "makeDefault=y");                    

						selectElement.attr("href", newhref);
						
				        // alert(billto_num);
				        // alert(oldhref);						
				        // alert(newhref);						
                    } else {
			        	if ($(this).is('[disabled=disabled]')) {
			        		// alert('disabled.');
			        	}
                    
                    	// $('input:checkbox').not(this).prop('checked', this.checked);
                    	// $("input:checkbox:not(:checked)").attr('disabled','false');
						// $("input:checkbox").attr('disabled','false');
						$("input:checkbox:not(:checked)").removeAttr("disabled");
                    
				        var billto_num = $(this).attr("billto_num");
				        
				        var selectElement = $("#selectAddress" + billto_num);
				        
				        var oldhref = selectElement.attr("href");
						var newhref = oldhref.replace("makeDefault=y", "makeDefault=n");                    

						selectElement.attr("href", newhref);
						
				        // alert(billto_num);
				        // alert(oldhref);						
				        // alert(newhref);	
                    }
			    }
			    						);  
		});    
    </script>

<jsp:include page="/htdocs/tfoot.inc" />
<jsp:include page="/htdocs/bottomscripts.inc" />
   
</html>

