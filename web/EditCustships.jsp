<!DOCTYPE html>
<html lang="en-us">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <meta charset="utf-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <meta name="description" content="Galco Industrial Electronics | Enter Shiping Address">
   <meta name="author" content="Nicholas Cirullo">
   <title>Edit Shiping Addresses</title>
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
                        <h2 class="col-xs-12">Select a Shiping Address</h2>
                     </div>

			<c:choose>
    			<c:when test="${not empty dfltCustship}">

                     <div class="addressRow row selectedAddress" data-shipto="02">
                        <div class="col-xs-12">
                           <h3>Selected Shiping Address</h3>
                        </div>
                        <div class="backgroundColor">
                           <div class="col-xs-12 nameBlock">
                              <div>${dfltCustship.name}</div>
                           </div>
                           <div class="col-xs-12 col-sm-9 addressBlock">
                              <div>${cust.name}</div>
                              <div>${dfltCustship.address}</div>
                              <div>${dfltCustship.address2}</div>
                              <div>
                                 <span>${dfltCustship.city}</span>,
                                 <span>${dfltCustship.state}</span>
                                 <span>${dfltCustship.zip}</span>
                              </div>
                              <div>${dfltCustship.country}</div>
							  
							  <c:if test="${profileEditing == null}">
                                  <a href="/portal/controller?formFunction=SelectCustship&shipto_num=${dfltCustship.shipto_num}" class="btn btn-primary-outline">Use selected address</a>
							  </c:if>
                           </div>
                           <div class="col-xs-12 col-sm-3 alterationLinks text-right">
                              <a href="/portal/controller?formFunction=EditCustship&shipto_num=${dfltCustship.shipto_num}">Update</a>
                              <br>
                              <a href="/portal/controller?formFunction=RemoveCustship&shipto_num=${dfltCustship.shipto_num}">Remove</a>
                           </div>
                        </div>
                     </div>

    			</c:when>
    			<c:otherwise>

                     <div class="addressRow row selectedAddress" data-shipto="02">
                        <div class="col-xs-12">
                           <h3>Selected Shiping Address</h3>
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
                                  <a href="/portal/controller?formFunction=SelectCustship&shipto_num=" class="btn btn-primary-outline">Use selected address</a>
							  </c:if>
                           </div>
                        </div>
                     </div>    			

    			</c:otherwise>
			</c:choose>				


			<c:if test="${(not empty custshipAL) || (not empty dfltCustship)}">
                     <div class="addressRow row " data-shipto="01">
                        <div class="col-xs-12">
                           <h3>Additional Shiping Addresses</h3>
                        </div>

				<c:if test="${not empty dfltCustship}">

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
	                                   <input type="checkbox" name="makeDefaultShiping" id="makeDefaultShiping" shipto_num="" value="01">
	                                   <label for="makeDefaultShiping">Make this my default shiping address</label>
	                                </div>
							     </c:when>
							     <c:otherwise>
                                       <a href="/portal/controller?formFunction=ChangeDefaultCustship&shipto_num=" id="selectAddress" class="btn btn-primary-outline">Make this default address</a>
							     </c:otherwise>
							  </c:choose>                              
                              
							  <c:if test="${profileEditing == null}">
                                  <a href="/portal/controller?formFunction=SelectCustship&shipto_num=&makeDefault=n" id="selectAddress" class="btn btn-primary-outline">Select this address</a>
							  </c:if>
                           </div>
                        </div>
                        
				</c:if>
				
				<c:forEach items="${custshipAL}" var="custship">

                        <div class="backgroundColor">
                           <div class="col-xs-12 nameBlock">
                              <div>${dfltCustship.name}</div>
                           </div>
                           <div class="col-xs-12 col-sm-9 addressBlock">
                              <div>${cust.name}</div>
                              <div>${custship.address}</div>
                              <div>${custship.address2}</div>
                              <div>
                                 <span>${custship.city}</span>,
                                 <span>${custship.state}</span>
                                 <span>${custship.zip}</span>
                              </div>
                              <div>${custship.country}</div>
                              
							  <c:choose>
							     <c:when test="${profileEditing == null}">
                                    <div class="makeDefault">
                                       <input type="checkbox" name="makeDefaultShiping" id="makeDefaultShiping${custship.shipto_num}" shipto_num="${custship.shipto_num}" value="01">
                                       <label for="makeDefaultShiping${custship.shipto_num}">Make this my default shiping address</label>
                                    </div>
							     </c:when>
							     <c:otherwise>
                                       <a href="/portal/controller?formFunction=ChangeDefaultCustship&shipto_num=${custship.shipto_num}" id="selectAddress" class="btn btn-primary-outline">Make this default address</a>
							     </c:otherwise>
							  </c:choose>
                              
							  <c:if test="${profileEditing == null}">
                                  <a href="/portal/controller?formFunction=SelectCustship&shipto_num=${custship.shipto_num}&makeDefault=n" id="selectAddress${custship.shipto_num}" class="btn btn-primary-outline">Select this address</a>
							  </c:if>
                           </div>
                           <div class="col-xs-12 col-sm-3 alterationLinks text-right">
                              <a href="/portal/controller?formFunction=EditCustship&shipto_num=${custship.shipto_num}">Update</a>
                              <br>
                              <a href="/portal/controller?formFunction=RemoveCustship&shipto_num=${custship.shipto_num}">Remove</a>
                           </div>
                        </div>

				</c:forEach>

                     </div>

			</c:if>

		 		 <form role="form" action="/portal/controller?formFunction=AddCustship" id="SignUpShipForm" class="form-horizontal">
                     <div class="addAddress row bgPlusButton">
                        <button id="addOrUpdateAddressButton">
                           <span><i class="fa fa-plus-circle"></i>Add a Shiping Address</span>
                        </button>
                     </div>
                     <input type="hidden" name="formFunction" value="AddCustship">
                     
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
		if ($("#SignUpShipForm").valid() == true) {
			e.preventDefault();
			$("#SignUpShipForm").submit();
		}
	});
    </script>

    <script>
		$(function() {
			// Handler for .ready() called.
			// $('input').change	(
			// $('#makeDefaultShiping').change	(
			$('[id^=makeDefaultShiping]').change	(
				function() {
					// alert($(this).is('[disabled=disabled]'));
					// alert($(this).prop("disabled"));
					
			        if ($(this).is(':checked')) {
			        	if ($(this).is('[disabled=disabled]')) {
			        		// alert('disabled.');
			        	}
			        
						// $('input').attr('checked', false);					
						// $(this).attr('checked', true);
						
						// $("#makeDefaultShiping :checkbox:not(:checked)").attr('disabled', true);
						
						// $('input input[type=checkbox]').attr('disabled','true');

						$("input:checkbox:not(:checked)").attr('disabled','true');

				        var shipto_num = $(this).attr("shipto_num");
				        
				        var selectElement = $("#selectAddress" + shipto_num);
				        
				        var oldhref = selectElement.attr("href");
						var newhref = oldhref.replace("makeDefault=n", "makeDefault=y");                    

						selectElement.attr("href", newhref);
						
				        // alert(shipto_num);
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
                    
				        var shipto_num = $(this).attr("shipto_num");
				        
				        var selectElement = $("#selectAddress" + shipto_num);
				        
				        var oldhref = selectElement.attr("href");
						var newhref = oldhref.replace("makeDefault=y", "makeDefault=n");                    

						selectElement.attr("href", newhref);
						
				        // alert(shipto_num);
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

