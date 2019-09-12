<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>My Orders/Quotes</title>

<!--
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
-->

<link rel="stylesheet" href="bootstrap-dialog.css">

<jsp:include page="/htdocs/topscripts.inc" />

<link rel="stylesheet" href="order_list_display.css">

</head>

<script src="bootstrap-dialog.js"></script>

<script>
$(document).ready( function() {	
    $(document).on('click', 'A[rel="external"]', function(event) {
        event.preventDefault();
        window.open( $(this).attr('href') );
    });

});

function bigImg(x) {
	//alert(x.clientHeight);
	//alert(x.clientWidth);
    x.style.height = (x.clientHeight * 1.04) + "px";
    x.style.width = (x.clientWidth * 1.04) + "px";
}

function normalImg(x) {
    x.style.height = (x.clientHeight / 1.04) + "px";
    x.style.width = (x.clientWidth / 1.04) + "px";
}

// qqq

function popitup(url) {
	newwindow=window.open(url,'name','height=600,width=800,scrollbars=yes');
	if (window.focus) {newwindow.focus()}
	return false;
}

</script>

<body style="padding-top:248px" onload="setSignInOutButtons();">

<jsp:include page="/htdocs/topnav.inc" />


    <div class="container">
    <div class="row">
    <!-- navigation tabs -->
    <div class="positionTab"  id="tabContainer">
    <ul class="nav nav-tabs navbdr" id="sampleTabs" >
        <li role="presentation" <c:if test="${activeTab == 'orders'}">class="active"</c:if> id="ordersTab" ><a data-toggle="tab" href="#ordersCont"><strong><span class="greyText">My </span></strong><span class="orangeTxt">Orders</span></a></li>
        <li role="presentation" <c:if test="${activeTab == 'quotes'}">class="active"</c:if> id="quotesTab" ><a data-toggle="tab" href="#quotesCont"><strong><span class="greyText">My </span></strong><span class="orangeTxt">Quotes</span></a></li>
        <!-- 
        <li role="presentation" <c:if test="${activeTab == 'wishlists'}">class="active"</c:if> id="wishlistTab"><a data-toggle="tab" href="#wishCont"><strong><span class="greyText">My </span></strong><span class="orangeTxt">Wishlist</span></a></li>
        -->
		<!-- <li><div style="padding-top:15px; width: 200px; text-align: right;vertical-align: middle;"><c:choose><c:when test="${userFnLn != null}">Welcome, ${userFnLn}!</c:when><c:otherwise>Welcome!</c:otherwise></c:choose></div></div></li> --> 
    </ul>
    </div>


    <!-- begin tab content -->
    <div class="tab-content">

		<c:if test="${messageToUser != null}">
			<div class="alert alert-success">
		  		${messageToUser}
			</div>
		</c:if>

        <!-- Subbarao - begin my orders tab -->
        <!-- begin my orders section -->
        <div class="myorder containerpad tab-pane fade in <c:if test="${activeTab == 'orders'}">active</c:if>" id="ordersCont">
            <!-- column titles -->
            <div class="panel-group" id="accordion">
                <div  class="panel panel-default titlePanel bluebg straightbg positionPanel"  >
                    <div class=" panel-title row paddingA">
                        <div class="col-sm-2">Order Placed
                        </div>
                        <div class="col-sm-2">Galco Order #
                        </div>
                        <div class="col-sm-2">Status
                        </div>
                        <div class="col-sm-2">PO#
                        </div>
                        <div class="col-sm-2">Ordered By
                        </div>
                        <div class="col-sm-2 orangeTxt">
                        </div>
                    </div>
                </div>

                <!-- row titles -->
                <div id="scrollerOrd">
                    <div  class="panel panel-default straightbg" style="padding-top: 0px !important; margin-top: 0px !important;" >

						<c:if test="${ordersAL != null}">
						<c:forEach items="${ordersAL}" var="order">
                        <!-- Subbarao - Order S -->

                     <a class="orderDetailsToggler panelLinks" order_num="${order.order_num}" ship_loc="${order.ship_loc}"  ordered_by="${order.ordered_by}" orderStatus="${order.orderStatus}" data-toggle="collapse" data-parent="#accordion" href="#O${order.order_num}">
                        <div class="panel-heading whitebg">
                            <h4 class="panel-title row">
                                <div class="col-sm-2" id="t-orderPlaced">
                                    <span class="glyphicon glyphicon-triangle-right"></span>${order.order_date}
                                </div>
                                <div class="col-sm-2" id="to-orderNumber">${order.order_num}
                                </div>
                                <div class="col-sm-2" id="to-status">${order.orderStatus}
                                </div>
                                <div class="col-sm-2" id="to-poNum">${order.cust_ponum}
                                </div>
                                <div class="col-sm-2" id="to-orderBy">${order.ordered_by}
                                </div>
                            </h4>
                        </div>
		     		 </a>
						<div id="O${order.order_num}" style="display: none"></div>                        

                        <!-- Subbarao - Order E -->
						</c:forEach>
						</c:if>

                        <div id="lastDivForAjaxOrd">  </div>

            		</div>

				</div>
	    	</div>
		</div>
        <!-- Subbarao - end my orders tab -->



        <!-- Subbarao - begin my quotes tab -->
        <!-- begin my orders section -->
        <div class="myquote containerpad tab-pane fade in <c:if test="${activeTab == 'quotes'}">active</c:if>" id="quotesCont">
            <!-- column titles -->
            <div class="panel-group" id="accordion">
                <div class="panel panel-default titlePanel bluebg straightbg positionPanel" >
                    <div class=" panel-title row paddingA">
                        <div class="col-sm-2">Date
                        </div>
                        <div class="col-sm-2">Quote #
                        </div>
                        <div class="col-sm-2">Expired date
                        </div>
                        <div class="col-sm-2">Status
                        </div>
                    </div>
                </div>

                <!-- row titles -->
                <div id="scrollerQuo">
                    <div  class="panel panel-default straightbg" style="padding-top: 0px !important; margin-top: 0px !important;" >

						<c:if test="${quotesAL != null}">
						<c:forEach items="${quotesAL}" var="order">
                        <!-- Subbarao - Order S -->

                     <a class="quoteDetailsToggler panelLinks" order_num="${order.order_num}" ship_loc="${order.ship_loc}" ordered_by="${order.ordered_by}" orderStatus="${order.orderStatus}" data-toggle="collapse" data-parent="#accordion" href="#O${order.order_num}">
                        <div panelLinks class="panel-heading whitebg ">
                            <h4 class="panel-title row">
                                <div class="col-sm-2" id="t-orderPlaced">
                                    <span class="glyphicon glyphicon-triangle-right"></span>${order.order_date}
                                </div>
                                <div class="col-sm-2" id="to-orderNumber">${order.order_num}
                                </div>
                                <div class="col-sm-2" id="to-expire-date">${order.date_closed}
                                </div>
                                <div class="col-sm-2" id="to-status">${order.orderStatus}
                                </div>
                            </h4>
                        </div>
		             </a>
						<div id="O${order.order_num}" style="display: none"></div>                        

                        <!-- Subbarao - Order E -->
						</c:forEach>
						</c:if>


                        <div id="lastDivForAjaxQuo">  </div>

            		</div>

				</div>
	    	</div>
		</div>
        <!-- Subbarao - end my quotes tab -->

	<!-- begin mywishlist -->
	<div class="mywl containerpad tab-pane fade in <c:if test="${activeTab == 'wishlists'}">active</c:if>" id="wishCont">
    </div>
    
    </div>
    </div><!--end the row-->
    </div><!--end the container-->
<jsp:include page="/htdocs/bottomscripts.inc" />
<jsp:include page="/htdocs/tfoot.inc" />

    </body>

<jsp:include page="ChangeButtons.jsp" />

<script>

var cust_num = "${cust_num}";
var cont_no = "${cont_no}";

var lastOrderDisplayed = "${lastOrderDisplayed}";
var dateOfLastOrderDisplayed = "${dateOfLastOrderDisplayed}";
var orderDetailsAddedAlready = [];

var orderOrQuoteScrollIsHappening = 0;

// alert(location.hash);
// var activeTab = $('[href=' + location.hash + ']');
// alert(location.hash);
// activeTab && activeTab.tab('show');

function checkCartAndProcessReOrder(order_num) {
	// testOrders();
	// return;
	    					
	// BootstrapDialog.alert('I want banana!');
	// $( "#dialog" ).dialog();
	
	/*
	var myMsg = 'Johnny Test';
	$('<div id="container"><h3>Error</h3><p>abcd</p></div>').dialog(
			{
				title: "Error2",

				buttons: {
			        OK: function() {
			          $( this ).dialog( "close" );
			        }
			      }
		    }
	);
	*/

	// alert(order_num);

	$.ajax			(	
			{	url: "/scripts/cgiip.exe/wa/wcat/ajcartcount.r",
			cache: false,
			dataType: "xml",
			success:
				function(result) {
					// alert('abcd');
					var clearShopCart = 'n', noOfItemsInCart = 0;
  					var $d = $(result);
    				noOfItemsInCart = parseInt($d.find("cartitems").text(), 10) || 0;
					// alert(noOfItemsInCart);
					
					if (noOfItemsInCart > 0) {
						BootstrapDialog.show({
				            title: 'Please select',
				            message: 'Your cart is not empty. What do you want us to do?',
				            buttons: [{
				                label: 'Clear the cart',
				                action: function(dialog) {
				                	// alert("y");
				                	dialog.close();
				                    clearShopCart = 'y';
				                    processReOrder(order_num, clearShopCart);
				                }
				            }, {
				                label: 'Add items to the cart',
				                action: function(dialog) {
				                	// alert("n");
				                	dialog.close();
				                    clearShopCart = 'n';
				                    processReOrder(order_num, clearShopCart);				                    
				                }
				            }]
				        });
					} else {
						clearShopCart = 'n';
				    	processReOrder(order_num, clearShopCart);					
					}
				},
		    error:
			    function(xhr, textStatus, errorThrown){
		        	// alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);
					// BootstrapDialog.alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);

					BootstrapDialog.show({
		                type: BootstrapDialog.TYPE_DANGER,
		                title: 'Error',
		                message: 'Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown,
		                buttons: [{
		                    label: 'OK.',
		                    action: function(dialogItself) {
			                    dialogItself.close();
			                }
		                }]
		            });
		            
		            return;					
		     	}
			}
	  				);
}

function processReOrder(order_num, clearShopCart) {
	// alert(order_num + ' ' + clearShopCart);
	
	$.ajax			(	
			{	url: "/scripts/cgiip.exe/wa/wcat/reorder_processor.htm?order=" + order_num + "&clearShopCart=" + clearShopCart,
			cache: false,
			success:
				function(result) {
					var webSpeedGenByS = "<!--";
					var webSpeedGenByE = "-->";
					var index = result.indexOf(webSpeedGenByS);
					if (index >= 0) {
						result = result.substring(0, index);
					}
					// alert(result);
					
					if (result.indexOf("WebSpeed error") >= 0) {
						BootstrapDialog.show({
			                type: BootstrapDialog.TYPE_DANGER,
			                title: 'Error',
			                message: "There is an internal error, please contact Galco Customer Service at 800-337-1062.",
			                buttons: [{
			                    label: 'OK.',
			                    action: function(dialogItself) {
				                    dialogItself.close();
				                }
			                }]
			            });

	                    return;
					}

					result = jQuery.parseJSON(result);

					// alert(result);
					if ((result.msg == null) || (result.msg !== "")) {
						// alert("Error occurred processing your request.\n" + result.msg + "\n" + result.msgInt);
						// BootstrapDialog.alert("Error occurred processing your request.\n" + result.msg + "\n" + result.msgInt);
						// BootstrapDialog.alert("Error occurred processing your request.\n" + result.msg);

						BootstrapDialog.show({
			                type: BootstrapDialog.TYPE_DANGER,
			                title: 'Error',
			                message: result.msg,
			                buttons: [{
			                    label: 'OK.',
			                    action: function(dialogItself) {
				                    dialogItself.close();
				                }
			                }]
			            });

	                    return;
					}

					document.cookie="pid=" + result.pid + "; path=/";
					window.location.replace("/scripts/cgiip.exe/wa/wcat/shopcart.htm");
				},
		    error:
			    function(xhr, textStatus, errorThrown){
		        	// alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);
					// BootstrapDialog.alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);

					BootstrapDialog.show({
		                type: BootstrapDialog.TYPE_DANGER,
		                title: 'Error',
		                message: 'Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown,
		                buttons: [{
		                    label: 'OK.',
		                    action: function(dialogItself) {
			                    dialogItself.close();
			                }
		                }]
		            });
					
		     	}
			}
	  				);
}

/* ccc */

function guid() {
  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
    s4() + '-' + s4() + s4() + s4();
}

function s4() {
  return Math.floor((1 + Math.random()) * 0x10000)
    .toString(16)
    .substring(1);
}

function retrieveInvoiceAsPDF(clickedElem, invoice_num) {
	var uuid = guid();
	$(clickedElem).after('<img style="display: inline;" class="img-responsive" id="' + uuid + '" src="images/ajax-loader.gif">');
    $('#' + uuid).css("padding-left", "7px");
	var onclick_attr = clickedElem.getAttribute("onclick");
	var style_attr = clickedElem.getAttribute("style");
	/* alert(onclick_attr); */
	clickedElem.removeAttribute("onclick");	
	clickedElem.removeAttribute("style");	
	
	$.ajax			(	
			{	url: "/scripts/cgiip.exe/wa/wcat/invoice_gen.r?invoice_num=" + invoice_num,
			cache: false,
			success:
				function(result) {
					var webSpeedGenByS = "<!--";
					var webSpeedGenByE = "-->";
					var index = result.indexOf(webSpeedGenByS);
					if (index >= 0) {
						result = result.substring(0, index);
					}
					// alert(result);
					
					if (result.indexOf("WebSpeed error") >= 0) {
						$('#' + uuid).remove();
						clickedElem.setAttribute("onclick", onclick_attr);
						clickedElem.setAttribute("style", style_attr);

						BootstrapDialog.show({
			                type: BootstrapDialog.TYPE_DANGER,
			                title: 'Error',
			                message: "There is an internal error, please contact Galco Customer Service at 800-337-1062.",
			                buttons: [{
			                    label: 'OK.',
			                    action: function(dialogItself) {
				                    dialogItself.close();
				                }
			                }]
			            });

						return;
					}

					result = jQuery.parseJSON(result);

					// alert(result);
					if ((result.msg == null) || (result.msg !== "")) {
						// alert("Error occurred processing your request.\n" + result.msg + "\n" + result.msgInt);
						// BootstrapDialog.alert("Error occurred processing your request.\n" + result.msg + "\n" + result.msgInt);
						// BootstrapDialog.alert("Error occurred processing your request.\n" + result.msg);

						$('#' + uuid).remove();
						clickedElem.setAttribute("onclick", onclick_attr);
						clickedElem.setAttribute("style", style_attr);

						BootstrapDialog.show({
			                type: BootstrapDialog.TYPE_DANGER,
			                title: 'Error',
			                message: result.msg,
			                buttons: [{
			                    label: 'OK.',
			                    action: function(dialogItself) {
				                    dialogItself.close();
				                }
			                }]
			            });

	                    return;
					}

					popitup("/scripts/cgiip.exe/wa/wcat/invoice_retrieve.r?invoice_num=" + invoice_num);
					$('#' + uuid).remove();					
					clickedElem.setAttribute("onclick", onclick_attr);
					clickedElem.setAttribute("style", style_attr);
				},
		    error:
			    function(xhr, textStatus, errorThrown){
		        	// alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);
					// BootstrapDialog.alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);

					$('#' + uuid).remove();
					clickedElem.setAttribute("onclick", onclick_attr);
					clickedElem.setAttribute("style", style_attr);
					
					BootstrapDialog.show({
		                type: BootstrapDialog.TYPE_DANGER,
		                title: 'Error',
		                message: 'Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown,
		                buttons: [{
		                    label: 'OK.',
		                    action: function(dialogItself) {
			                    dialogItself.close();
			                }
		                }]
		            });
		     	}
			}
	  				);	  				
}

function retrieveAllInvoicesOfAnOrderAsPDF(clickedElem, invoice_num) {
	var uuid = guid();
	$(clickedElem).after('<img style="display: inline;" class="img-responsive" id="' + uuid + '" src="images/ajax-loader.gif">');
    $('#' + uuid).css("padding-left", "7px");	
	var onclick_attr = clickedElem.getAttribute("onclick");
	var style_attr = clickedElem.getAttribute("style");
	/* alert(onclick_attr); */
	clickedElem.removeAttribute("onclick");	
	clickedElem.removeAttribute("style");	
		
	$.ajax			(	
			{	url: "/scripts/cgiip.exe/wa/wcat/invoice_gen.r?order_num=" + invoice_num,
			cache: false,
			success:
				function(result) {
					var webSpeedGenByS = "<!--";
					var webSpeedGenByE = "-->";
					var index = result.indexOf(webSpeedGenByS);
					if (index >= 0) {
						result = result.substring(0, index);
					}
					// alert(result);
					
					if (result.indexOf("WebSpeed error") >= 0) {
						$('#' + uuid).remove();
						clickedElem.setAttribute("onclick", onclick_attr);
						clickedElem.setAttribute("style", style_attr);

						BootstrapDialog.show({
			                type: BootstrapDialog.TYPE_DANGER,
			                title: 'Error',
			                message: "There is an internal error, please contact Galco Customer Service at 800-337-1062.",
			                buttons: [{
			                    label: 'OK.',
			                    action: function(dialogItself) {
				                    dialogItself.close();
				                }
			                }]
			            });

						return;
					}

					result = jQuery.parseJSON(result);

					// alert(result);
					if ((result.msg == null) || (result.msg !== "")) {
						// alert("Error occurred processing your request.\n" + result.msg + "\n" + result.msgInt);
						// BootstrapDialog.alert("Error occurred processing your request.\n" + result.msg + "\n" + result.msgInt);
						// BootstrapDialog.alert("Error occurred processing your request.\n" + result.msg);
						$('#' + uuid).remove();
						clickedElem.setAttribute("onclick", onclick_attr);
						clickedElem.setAttribute("style", style_attr);

						BootstrapDialog.show({
			                type: BootstrapDialog.TYPE_DANGER,
			                title: 'Error',
			                message: result.msg,
			                buttons: [{
			                    label: 'OK.',
			                    action: function(dialogItself) {
				                    dialogItself.close();
				                }
			                }]
			            });

						return;
					}

					// qqq
					popitup("/scripts/cgiip.exe/wa/wcat/invoice_retrieve.r?order_num=" + invoice_num);
					$('#' + uuid).remove();
					clickedElem.setAttribute("onclick", onclick_attr);
					clickedElem.setAttribute("style", style_attr);
				},
			timeout: 120000,
		    error:
			    function(xhr, textStatus, errorThrown){
		        	// alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);
					// BootstrapDialog.alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);
					$('#' + uuid).remove();
					clickedElem.setAttribute("onclick", onclick_attr);
					clickedElem.setAttribute("style", style_attr);					

					BootstrapDialog.show({
		                type: BootstrapDialog.TYPE_DANGER,
		                title: 'Error',
		                message: 'Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown,
		                buttons: [{
		                    label: 'OK.',
		                    action: function(dialogItself) {
			                    dialogItself.close();
			                }
		                }]
		            });					
		     	}
			}
	  				);	  				
}

function getOrderDetailsHtmlOrd(order_num, ordered_by, orderStatus, totalPrice, noInvoicesAreAvailable, salesRep) {
	var convertButtonHtml = "";

	if (orderStatus == "Open") {
		convertButtonHtml = '                                        <button type="button" id="bq-orderQte" class="newBtns btn btn-danger " onclick="checkCartAndProcessReOrder(\'' + order_num + '\');">Re-Order</button> ';
	} else {
		convertButtonHtml = '                                        <button type="button" id="bq-orderQte" class="newBtns btn btn-danger " onclick="checkCartAndProcessReOrder(\'' + order_num + '\');">Re-Order</button> ';
	}
	
	var returnHtml =
'     <div class= "row"> ' +
'         <strong><div class="col-sm-6" >Order #:</div></strong> ' +
'         <div class="col-sm-6" id="bo-orderNum">' + order_num + '</div> ' +
'     </div> ' +
'     <div class= "row"> ' +
'         <strong><div class="col-sm-6">Ordered By:</div></strong> ' +
'         <div class="col-sm-6" id="bo-orderBy">' + ordered_by + '</div> ' +
'     </div> ' +
'     <div class= "row"> ' +
'         <strong><div class="col-sm-6">MDSE. Total:</div></strong> ' +
'         <div class="col-sm-6" id="bo-total">' + totalPrice + '</div> ' +
'     </div> ' +
'     <div class= "row"> ' +
'         <strong><div class="col-sm-6">Sales Rep.:</div></strong> ' +
'         <div class="col-sm-6" id="bo-total">' + salesRep + '</div> ' +
'     </div> ' +
'     <div class= "row"> ' +
'         <strong><div class="col-sm-6 orangeTxt">Invoice:</div></strong> ' +
'         <div class="col-sm-6"> ' +
             ((noInvoicesAreAvailable == "n")?
              ('<a style="cursor: pointer;" onclick="retrieveAllInvoicesOfAnOrderAsPDF(this, &quot;' + order_num + '&quot;)">Click To View</a>'):
              ('<p>Not yet ready</p>') 
             ) +    	
'         </div> ' +
'     </div> ' +
'     <div class= "row"> ' +
'         <strong><div class="col-sm-6 orangeTxt">Feedback:</div></strong> ' +
'         <div class="col-sm-6"> ' +
'             <a href="mailto:webmaster@galco.com">Click for Survey </a> ' +
'         </div> ' +
'     </div> ' +
'     <div class="row " > ' + convertButtonHtml + 
'     </div> ';

	/* alert(returnHtml); */
	return returnHtml;
}


function getItemDetailsHtmlOrd(item) {
	
	var dynamicHtml = "";

	dynamicHtml = dynamicHtml + 
' <div class="row flx"> ' +
'     <div class="col-sm-6 paddingA " > ' +
'         <div class= "row"> ' +
'             <strong><div class="col-sm-6">Item #:</div></strong> ' +
'             <div class="col-sm-6" id="bo-itemNum">' + ((item.itemDetailHref != "")?(' <a style="cursor: pointer;" onclick="return popitup(&quot;' + item.itemDetailHref + '&quot;)">'):('')) + item.partNum + ((item.itemDetailHref != "")?' </a>':'') + '</div> ' +
'         </div> ' +
'         <div class= "row"> ' +
'             <strong><div class="col-sm-6">Availability By:</div></strong> ' +
'             <div class="col-sm-6" id="bo-available">' + item.availability + '</div> ' +
'         </div> ' +
'         <div class= "row"> ' +
'             <strong><div class="col-sm-6">Unit:</div></strong> ' +
'             <div class="col-sm-6" id="bo-unit">' + item.billing_unit + '</div> ' +
'         </div> ' +
'         <div class= "row"> ' +
'             <strong><div class="col-sm-6">Unit Price:</div></strong> ' +
'             <div class="col-sm-6" id="bo-unitPrice">' + item.unit_price + '</div> ' +
'         </div> ' +
'         <div class= "row"> ' +
'             <strong><div class="col-sm-6">Extend Price:</div></strong> ' +
'             <div class="col-sm-6" id="bo-extendPrice">' + item.extendedPrice + '</div> ' +
'         </div> ' +
'         <div class= "row"> ' +
'             <strong><div class="col-sm-6">QTY Ordered:</div></strong> ' +
'             <div class="col-sm-6" id="bo-qty">' + item.qtyOrdered + '</div> ' +
'         </div> ' +
'         <div class= "row"> ' +
'             <strong><div class="col-sm-6">QTY shipped:</div></strong> ' +
'             <div class="col-sm-6" id="bo-qty">' + item.qtyShipped + '</div> ' +
'         </div> ' +
'         <div class= "row"> ' +
'             <strong><div class="col-sm-6">QTY BackOrder:</div></strong> ' +
'             <div class="col-sm-6" id="bo-backOrder">' + item.qtyBackOrder + '</div> ' +
'         </div> ';

    for (var shipment in item.shipments) {
		var trackingUrl = "";
		var fedexOrUPS = 0;
		if ((item.shipments)[shipment].carrier == "UPS") {
			trackingUrl = "http://wwwapps.ups.com/etracking/tracking.cgi?tracknum=" + (item.shipments)[shipment].trakCtrlnum;
			fedexOrUPS = 1;                                                
		} else if ((item.shipments)[shipment].carrier == "FEDEXP") {
			trackingUrl = "http://www.fedex.com/Tracking?action=track&amp;tracknumbers=" + (item.shipments)[shipment].trakCtrlnum;
			fedexOrUPS = 1;                                                
		}

		if (fedexOrUPS == 1) {
	    	dynamicHtml = dynamicHtml + 
'         <div class= "row"> ' +
'             <strong><div class="col-sm-6">Shipped Date:</div></strong> ' +
'             <div class="col-sm-6" id="bo-shipDate">' + (item.shipments)[shipment].shippedDate + '</div> ' +
'         </div> ' +
'         <div class= "row"> ' +
'             <strong><div class="col-sm-6">Tracking Num:</div></strong> ' +
'             <div class="col-sm-6" id="bo-carrier">' +
'                <a target="_blank" href="' + trackingUrl + '">' + (item.shipments)[shipment].trakCtrlnum + '</a>' +
'             </div> ' +
'         </div> ';
		} else {
	    	dynamicHtml = dynamicHtml + 
'         <div class= "row"> ' +
'             <strong><div class="col-sm-6">Shipped Date:</div></strong> ' +
'             <div class="col-sm-6" id="bo-shipDate">' + (item.shipments)[shipment].shippedDate + '</div> ' +
'         </div> ' +
'         <div class= "row"> ' +
'             <strong><div class="col-sm-6">Tracking Num:</div></strong> ' +
'             <div class="col-sm-6" id="bo-carrier">N/A' +
'             </div> ' +
'         </div> ';
		}
		
		/* ccc */
		
	    dynamicHtml = dynamicHtml + 
 '        <div class= "row"> ' +
 '            <strong><div class="col-sm-6">Invoice Num:</div></strong> ' +
 '            <div class="col-sm-6" id="bo-carrier">' +
					(((item.shipments)[shipment].inv_num != "")?	    
   					 ('                <a style="cursor: pointer;" onclick="return retrieveInvoiceAsPDF(this, &quot;' + (item.shipments)[shipment].inv_num + '&quot;);">' + (item.shipments)[shipment].inv_num + '</a>'):
   					 ('Not yet ready')
   					) +
 '            </div> ' +
 '        </div> ';	
	}

    dynamicHtml = dynamicHtml + 
'     </div> ' +
'     <div class="col-sm-6 paddingA borderR" > ' +
			((item.itemDetailHref != "")?(' <a style="cursor: pointer;" onclick="return popitup(&quot;' + item.itemDetailHref + '&quot;)">'):'') +
'         <div class="col-sm-6" ><img class="img-responsive" width="100%" id = "bo-img" src="' + item.imageFilePath + '" title=' + ((item.itemDetailHref != "")?('"Click for detailed info and to order."'):'""') + ' ></div> ' +
				((item.itemDetailHref != "")?' </a>':'') +
'         <div class="col-sm-6"> ' +
'             <div class="row sectMgn"><strong><u>' + ((item.itemDetailHref != "")?(' <a style="cursor: pointer;" onclick="return popitup(&quot;' + item.itemDetailHref + '&quot;)">'):('')) + item.brand + ((item.itemDetailHref != "")?' </a>':'') + '</strong></u></div> ' +
'             <div class="row sectMgn"><strong><u>' + ((item.itemDetailHref != "")?(' <a style="cursor: pointer;" onclick="return popitup(&quot;' + item.itemDetailHref + '&quot;)">'):('')) + item.alternateName + ((item.itemDetailHref != "")?' </a>':'') + '</strong></u></div> ' +
'             <div class="row sectMgn">' + ((item.itemDetailHref != "")?(' <a style="cursor: pointer;" onclick="return popitup(&quot;' + item.itemDetailHref + '&quot;)">'):('')) + item.itemDescription + ((item.itemDetailHref != "")?' </a>':'') + '</div> ' +
'         </div> ' +
'     </div> ' +
' </div> ';

	return dynamicHtml;
}

$(document.body).on('click', '.orderDetailsToggler' ,
		
function() {
	var order_num = $(this).attr('order_num');
	var ordered_by = $(this).attr('ordered_by');
	var ship_loc = $(this).attr('ship_loc');
	var orderStatus = $(this).attr('orderStatus');

	// alert($("#O" + order_num).css('display'));
	var $header = $(this);
    var $span = $header.find('span.glyphicon');
   	// if($("#O" + order_num).is(':visible')){
   	if ($("#O" + order_num).css('display') == 'none') { 
        $span.removeClass('glyphicon-triangle-right');
        $span.addClass('glyphicon-triangle-bottom');
    } else {
        $span.removeClass('glyphicon-triangle-bottom');
        $span.addClass('glyphicon-triangle-right');
    }

   	// alert(order_num);
	if (orderDetailsAddedAlready[order_num] == 1) {
	    return;
	}
	orderDetailsAddedAlready[order_num] = 1;


	var orderDetailsHtml = '';
	
	$.ajax			(	
			{url: "/portal/controller?formFunction=OrderDetails&order_num=" + order_num + "&ship_loc=" + ship_loc,
			cache: false,
			success:
				function(result) {
					// alert(result.session_expired);
					if (result.session_expired == "YES") {
						window.location.replace("/portal/controller");
	                    return;
					}
					
					var leftColOrderDetailsHtml = '';
					leftColOrderDetailsHtml = leftColOrderDetailsHtml + getOrderDetailsHtmlOrd(order_num, ordered_by, orderStatus, result.totalPrice, result.noInvoicesAreAvailable, result.salesRep);
					/* alert(leftColOrderDetailsHtml); */
					var midRightColOrderDetailsHtml = '';
					$.each	(result.item_details,
						function(i, item) {
							midRightColOrderDetailsHtml = midRightColOrderDetailsHtml + getItemDetailsHtmlOrd(item);
						}
					 		);
					 		
					orderDetailsHtml = orderDetailsHtml +
						' <div id="O' + order_num + '" class="panel-collapse collapse in" > ' +
						'     <div class="panel-body row flx greybg" > ' +
						'         <div class="col-sm-3 borderB borderT paddingA"  > ' +
						              leftColOrderDetailsHtml +
						'         </div> ' +	
						'         <div class="col-sm-9 borderS borderT borderB"> ' +
						              midRightColOrderDetailsHtml +																
						'         </div> ' +
						'     </div> ' +
						' </div> ';

					$("#O" + order_num).html(orderDetailsHtml);
					$("#O" + order_num).toggle();
				}
			}
	  				);
}
                        );

function customAlert(msg, duration) {
	var styler = document.createElement("div");
	styler.setAttribute("style","border: solid 5px Red;width:auto;height:auto;top:50%;left:40%;background-color:#444;color:Silver");
	styler.innerHTML = "<h1>"+msg+"</h1>";
	setTimeout(	function() {
   					styler.parentNode.removeChild(styler);
 				},
 				duration);
 	document.body.appendChild(styler);
}

var scrollFunction = 
	function() {
		if ($("ul#sampleTabs li.active").attr('id') == 'ordersTab') {
			orderScroll();
		} else if ($("ul#sampleTabs li.active").attr('id') == 'quotesTab') {
			quoteScroll();
		}
	};	
	
	
$(window).scroll(scrollFunction);

function orderScroll() {
    // console.log("Subban. b4  . lastOrderDisplayed:" + lastOrderDisplayed);

    if ((orderOrQuoteScrollIsHappening == 1) || (lastOrderDisplayed == "9999999")) {
		return;
	}
	
	// ---------------------------------------------------------------------------------------    

	var mostOfTheWayDown = ($(document).height() - $(window).height()) * 2 / 5;
    
    var top_of_element = $("#lastDivForAjaxOrd").offset().top;
    var bottom_of_element = $("#lastDivForAjaxOrd").offset().top + $("#lastDivForAjaxOrd").outerHeight();
    var bottom_of_screen = $(window).scrollTop() + window.innerHeight;
    var top_of_screen = $(window).scrollTop();
    
    if((bottom_of_screen > top_of_element) && (top_of_screen < bottom_of_element)){
    // if ($(window).scrollTop() >= mostOfTheWayDown) {
    
	// ---------------------------------------------------------------------------------------    
    
    
    	// console.log("Subban. aft . lastOrderDisplayed:" + lastOrderDisplayed);
    	
		$(window).unbind("scroll");
    	
		$.ajax	(	
			{	url: "/portal/controller?formFunction=OrderListSubsequent&quo=N&cust_num=" + cust_num + "&cont_no=" + cont_no +  "&dateOfLastOrderDisplayed=" + dateOfLastOrderDisplayed + "&lastOrderDisplayed=" + lastOrderDisplayed,
			cache: false,
			dataType:'json',
			error:
			    function(xhr, textStatus, errorThrown) {
		        	alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);
			    	orderOrQuoteScrollIsHappening = 0;
			    	$(window).scroll(scrollFunction);
		        	return;
		     	},
			success:
				function(result) {
					// customAlert("Scrolling.","1000");

					// console.log("result.session_expired:" + result.session_expired);						
					// console.log("result.reachedLastOrder:" + result.reachedLastOrder);						
					
					// alert(result.session_expired);
					if (result.session_expired == "YES") {
				    	orderOrQuoteScrollIsHappening = 0;
						window.location.replace("/portal/controller");				
                        return;
					}
					
					var noMoreResults = 1;
					/* alert(result); */
					
					$.each	(result.ord_quo_Array,
							 function(i, order) {
						    	$("#lastDivForAjaxOrd").before("<a " + 'class="orderDetailsToggler panelLinks" order_num="' + order.order_num + '" ship_loc="' + order.ship_loc + '"  ordered_by="' + order.ordered_by + '" orderStatus="' + order.orderStatus + '" ' + "data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#O" + order.order_num + "\"> <div class=\"panel-heading whitebg \"> <h4 class=\"panel-title row\"> <div class=\"col-sm-2\" id=\"t-orderPlaced\"> <span class=\"glyphicon glyphicon-triangle-right\"></span>" + order.order_date + "</div> <div class=\"col-sm-2\" id=\"to-orderNumber\">" + order.order_num + " </div> <div class=\"col-sm-2\" id=\"to-status\">" + order.orderStatus + "</div> <div class=\"col-sm-2\" id=\"to-poNum\">" + order.cust_ponum + "</div> <div class=\"col-sm-2\" id=\"to-orderBy\">" + order.ordered_by + "</div> </h4> </div></a>");
						    	$("#lastDivForAjaxOrd").before("<div id=\"O" + order.order_num + "\" style=\"display: none\"></div>");                        

						    	// console.log("returning order:" + order.order_num);
						    	lastOrderDisplayed = order.order_num;
						    	dateOfLastOrderDisplayed = order.order_date;
						    	noMoreResults = 0;
							 }
					 		);

			 		if (noMoreResults == 1) {
    					// console.log("reached the last order.");				 		
			 			lastOrderDisplayed = "9999999";
				 	}
					
					if (result.reachedLastOrder == "YES") {
						lastOrderDisplayed = "9999999";	
    					// console.log("reachedLastOrder:" + result.reachedLastOrder);
					}
					
			    	orderOrQuoteScrollIsHappening = 0;
					$(window).scroll(scrollFunction);			    	
				}				
			}
	  			);
    }
}

function orderScrollO() {
	if (orderOrQuoteScrollIsHappening == 1) {
		return;
	}
	
	var height = $(window).height();
	var scrollTop = $(window).scrollTop();
	alert (height + " " + scrollTop);
	
    // console.log("Subban. b4  . lastOrderDisplayed:" + lastOrderDisplayed);
    
    if ((lastOrderDisplayed != "9999999") && ((($("#scrollerOrd").scrollTop() + $("#scrollerOrd").innerHeight()) * 1.05) >= $("#scrollerOrd")[0].scrollHeight)) {
    	// console.log("Subban. aft . lastOrderDisplayed:" + lastOrderDisplayed);
    	
		$.ajax	(	
			{	url: "/portal/controller?formFunction=OrderListSubsequent&quo=N&cust_num=" + cust_num + "&cont_no=" + cont_no +  "&dateOfLastOrderDisplayed=" + dateOfLastOrderDisplayed + "&lastOrderDisplayed=" + lastOrderDisplayed,
			cache: false,
			dataType:'json',
			error:
			    function(xhr, textStatus, errorThrown) {
		        	alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);
			    	orderOrQuoteScrollIsHappening = 0;		        	
		        	return;
		     	},
			success:
				function(result) {
					// customAlert("Scrolling.","1000");

					// console.log("result.session_expired:" + result.session_expired);						
					// console.log("result.reachedLastOrder:" + result.reachedLastOrder);						
					
					// alert(result.session_expired);
					if (result.session_expired == "YES") {
				    	orderOrQuoteScrollIsHappening = 0;
						window.location.replace("/portal/controller");
                        return;
					}
					
					var noMoreResults = 1;
					/* alert(result); */
					
					$.each	(result.ord_quo_Array,
							 function(i, order) {
						    	$("#lastDivForAjaxOrd").before("<a " + 'class="orderDetailsToggler panelLinks" order_num="' + order.order_num + '" ship_loc="' + order.ship_loc + '"  ordered_by="' + order.ordered_by + '" orderStatus="' + order.orderStatus + '" ' + "data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#O" + order.order_num + "\"> <div class=\"panel-heading whitebg \"> <h4 class=\"panel-title row\"> <div class=\"col-sm-2\" id=\"t-orderPlaced\"> <span class=\"glyphicon glyphicon-triangle-right\"></span>" + order.order_date + "</div> <div class=\"col-sm-2\" id=\"to-orderNumber\">" + order.order_num + " </div> <div class=\"col-sm-2\" id=\"to-status\">" + order.orderStatus + "</div> <div class=\"col-sm-2\" id=\"to-poNum\">" + order.cust_ponum + "</div> <div class=\"col-sm-2\" id=\"to-orderBy\">" + order.ordered_by + "</div> </h4> </div></a>");
						    	$("#lastDivForAjaxOrd").before("<div id=\"O" + order.order_num + "\" style=\"display: none\"></div>");                        

						    	// console.log("returning order:" + order.order_num);
						    	lastOrderDisplayed = order.order_num;
						    	dateOfLastOrderDisplayed = order.order_date;
						    	noMoreResults = 0;
							 }
					 		);

			 		if (noMoreResults == 1) {
    					// console.log("reached the last order.");				 		
			 			lastOrderDisplayed = "9999999";
				 	}
					
					if (result.reachedLastOrder == "YES") {
						lastOrderDisplayed = "9999999";	
    					// console.log("reachedLastOrder:" + result.reachedLastOrder);						
					}
					
			    	orderOrQuoteScrollIsHappening = 0;
				}				
			}
	  			);
    }
}

function checkCartAndProcessConvertQuote(order_num, re_quote) {
	// testOrders();
	// return;
	    					
	// BootstrapDialog.alert('I want banana!');
	// $( "#dialog" ).dialog();
	
	/*
	var myMsg = 'Johnny Test';
	$('<div id="container"><h3>Error</h3><p>abcd</p></div>').dialog(
			{
				title: "Error2",

				buttons: {
			        OK: function() {
			          $( this ).dialog( "close" );
			        }
			      }
		    }
	);
	*/

	// alert(order_num);

	$.ajax			(	
			{	url: "/scripts/cgiip.exe/wa/wcat/ajcartcount.r",
			cache: false,
			dataType: "xml",
			success:
				function(result) {
					// alert('abcd');
					var clearShopCart = 'n', noOfItemsInCart = 0;
  					var $d = $(result);
    				noOfItemsInCart = parseInt($d.find("cartitems").text(), 10) || 0;
					// alert(noOfItemsInCart);
					
					if (noOfItemsInCart > 0) {
						BootstrapDialog.show({
				            title: 'Please select',
				            message: 'Your cart is not empty. What do you want us to do?',
				            buttons: [{
				                label: 'Clear the cart',
				                action: function(dialog) {
				                	// alert("y");
				                	dialog.close();
				                    clearShopCart = 'y';
				                    processConvertQuote(order_num, re_quote, clearShopCart);
				                }
				            }, {
				                label: 'Add items to the cart',
				                action: function(dialog) {
				                	// alert("n");
				                	dialog.close();
				                    clearShopCart = 'n';
				                    processConvertQuote(order_num, re_quote, clearShopCart);				                    
				                }
				            }]
				        });
					} else {
						clearShopCart = 'n';
				    	processConvertQuote(order_num, re_quote, clearShopCart);					
					}
				},
		    error:
			    function(xhr, textStatus, errorThrown){
		        	// alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);
					// BootstrapDialog.alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);

					BootstrapDialog.show({
		                type: BootstrapDialog.TYPE_DANGER,
		                title: 'Error',
		                message: 'Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown,
		                buttons: [{
		                    label: 'OK.',
		                    action: function(dialogItself) {
			                    dialogItself.close();
			                }
		                }]
		            });
		            
		            return;					
		     	}
			}
	  				);
}

function processConvertQuote(order_num, re_quote, clearShopCart) {
	// alert(order_num);

	$.ajax			(	
			{	url: "/scripts/cgiip.exe/wa/wcat/rfq_processor.htm?rfq=" + order_num + "&requote=" + re_quote + "&clearShopCart=" + clearShopCart,
			cache: false,
			success:
				function(result) {
					var webSpeedGenByS = "<!--";
					var webSpeedGenByE = "-->";
					var index = result.indexOf(webSpeedGenByS);
					if (index >= 0) {
						result = result.substring(0, index);
					}
					// alert(result);

					result = jQuery.parseJSON(result);

					// alert(result);
					if ((result.msg == null) || (result.msg !== "")) {
						BootstrapDialog.show({
			                type: BootstrapDialog.TYPE_DANGER,
			                title: 'Error',
			                message: result.msg,
			                buttons: [{
			                    label: 'OK.',
			                    action: function(dialogItself) {
				                    dialogItself.close();
				                }
			                }]
			            });
						// alert("Error occurred processing your request.\n" + result.msg + "\n" + result.msgInt);
						
	                    return;
					}

					document.cookie="pid=" + result.pid + "; path=/";
					window.location.replace("/scripts/cgiip.exe/wa/wcat/shopcart.htm");
				},
		    error:
			    function(xhr, textStatus, errorThrown){
		        	alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);
		     	}
			}
	  				);
}


/* Subbarao - Quotes Stuff */


var lastQuoteDisplayed = "${lastQuoteDisplayed}";
var dateOfLastQuoteDisplayed = "${dateOfLastQuoteDisplayed}";
var quoteDetailsAddedAlready = [];


function getOrderDetailsHtmlQuo(order_num, ordered_by, orderStatus, totalPrice, salesRep) {
	var convertButtonHtml = "";
	if (orderStatus == "Expired") {
		// convertButtonHtml = '                                        <button type="button" id="bq-orderQte" class="newBtns btn btn-danger" onclick="checkCartAndProcessConvertQuote(\'' + order_num + '\', \'y\');">Re-Quote</button> ';
		convertButtonHtml = '                                        <button type="button" id="bq-orderQte" class="newBtns btn btn-danger" onclick="checkCartAndProcessReOrder(\'' + order_num + '\');">Re-Quote</button> ';
	} else if (orderStatus == "Pending") {
		convertButtonHtml = '                                        <button type="button" id="bq-orderQte" class="newBtns btn btn-danger" disabled >Place Order</button> ';
	} else {
		convertButtonHtml = '                                        <button type="button" id="bq-orderQte" class="newBtns btn btn-danger" onclick="checkCartAndProcessConvertQuote(\'' + order_num + '\', \'n\');">Place Order</button> ';		
	}
	
	return '                                <div class="col-sm-3 borderB borderT paddingA"  > ' +
	'                                    <div class= "row"> ' +
	'                                        <strong><div class="col-sm-6" >Quote #:</div></strong> ' +
	'                                        <div class="col-sm-6" id="bo-orderNum">' + order_num + '</div> ' +
	'                                    </div> ' +
	'                                    <div class= "row"> ' +
	'                                        <strong><div class="col-sm-6">Requested By:</div></strong> ' +
	'                                        <div class="col-sm-6" id="bo-orderBy">' + ordered_by + '</div> ' +
	'                                    </div> ' +
	
	((orderStatus != "Expired")?
	 ('                                    <div class= "row"> ' +
	  '                                        <strong><div class="col-sm-6">MDSE. Total:</div></strong> ' +
	  '                                        <div class="col-sm-6" id="bo-total">' + totalPrice + '</div> ' +
	  '                                    </div> ') :
	  ('')) 
	+

	'                                    <div class= "row"> ' +
	'                                        <strong><div class="col-sm-6">Sales Rep.:</div></strong> ' +
	'                                        <div class="col-sm-6" id="bo-orderBy">' + salesRep + '</div> ' +
	'                                    </div> ' +
	
	'                                    <div class= "row"> ' +
	'                                        <strong><div class="col-sm-6 orangeTxt">Feedback:</div></strong> ' +
	'                                        <div class="col-sm-6"> ' +
	'                                            <a href="mailto:webmaster@galco.com">Click for Survey </a> ' +
	'                                        </div> ' +
	'                                    </div> ' +
	'                                    <div class="row" > ' +
	convertButtonHtml + 
	'                                   </div> ' +
	'                                </div> ';	
}


function getItemDetailsHtmlQuo(item, orderStatus) {
	
	// item.imageFilePath
	// item.subFamilyAndSeries
	// item.itemDescription
	
    return '                                    <div class="row flx"> ' +
    '                                        <div class="col-sm-6 paddingA " > ' +
    '                                            <div class= "row"> ' +
    '                                                <strong><div class="col-sm-6">Item #:</div></strong> ' +
    '                                                <div class="col-sm-6" id="bo-itemNum">' + ((item.itemDetailHref != "")?(' <a style="cursor: pointer;" onclick="return popitup(&quot;' + item.itemDetailHref + '&quot;)">'):('')) + item.partNum + ((item.itemDetailHref != "")?' </a>':'') + '</div> ' +
    '                                            </div> ' +
    '                                            <div class= "row"> ' +
    '                                                <strong><div class="col-sm-6">Availability By:</div></strong> ' +
    '                                                <div class="col-sm-6" id="bo-available">' + item.availability + '</div> ' +
    '                                            </div> ' +
    '                                            <div class= "row"> ' +
    '                                                <strong><div class="col-sm-6">Unit:</div></strong> ' +
    '                                                <div class="col-sm-6" id="bo-unit">' + item.billing_unit + '</div> ' +
    '                                            </div> ' +
    
    
    
	((orderStatus != "Expired")?
     (
     '                                            <div class= "row"> ' +
     '                                                <strong><div class="col-sm-6">Unit Price:</div></strong> ' +
     '                                                <div class="col-sm-6" id="bo-unitPrice">' + item.unit_price + '</div> ' +
     '                                            </div> ' +
     '                                            <div class= "row"> ' +
     '                                                <strong><div class="col-sm-6">Extend Price:</div></strong> ' +
     '                                                <div class="col-sm-6" id="bo-extendPrice">' + item.extendedPrice + '</div> ' +
     '                                            </div> '
     )
     :
     ""
    ) +
    
    
    
    '                                            <div class= "row"> ' +
    '                                                <strong><div class="col-sm-6">Quantity:</div></strong> ' +
    '                                                <div class="col-sm-6" id="bo-qty">' + item.qtyOrdered + '</div> ' +
    '                                            </div> ' +
    '                                        </div> ' +
    '                                        <div class="col-sm-6 paddingA borderR" > ' +
    ((item.itemDetailHref != "")?(' <a style="cursor: pointer;" onclick="return popitup(&quot;' + item.itemDetailHref + '&quot;)">'):'') +
	/* bbb */        
    '                                            <div class="col-sm-6" ><img class="img-responsive" width="100%" id = "bo-img" src="' + item.imageFilePath + '" title=' + ((item.itemDetailHref != "")?('"Click for detailed info and to order."'):'""') + ' ></div> ' +
    ((item.itemDetailHref != "")?' </a>':'') +
    '                                            <div class="col-sm-6"> ' +
    '                                                <div class="row sectMgn"><strong><u>' + ((item.itemDetailHref != "")?(' <a style="cursor: pointer;" onclick="return popitup(&quot;' + item.itemDetailHref + '&quot;)">'):('')) + item.brand + ((item.itemDetailHref != "")?' </a>':'') + '</strong></u></div> ' +
    '                                                <div class="row sectMgn"><strong><u>' + ((item.itemDetailHref != "")?(' <a style="cursor: pointer;" onclick="return popitup(&quot;' + item.itemDetailHref + '&quot;)">'):('')) +  item.alternateName + ((item.itemDetailHref != "")?' </a>':'') + '</strong></u></div> ' +
    '                                                <div class="row sectMgn">' + ((item.itemDetailHref != "")?(' <a style="cursor: pointer;" onclick="return popitup(&quot;' + item.itemDetailHref + '&quot;)">'):('')) +  item.itemDescription + ((item.itemDetailHref != "")?' </a>':'') + '</div> ' +
    '                                            </div> ' +
    '                                        </div> ' +
    /* bbb */
    '                                    </div> ';  
	
}

$(document.body).on('click', '.quoteDetailsToggler' ,
		
function() {
	var order_num = $(this).attr('order_num');
	var ordered_by = $(this).attr('ordered_by');
	var ship_loc = $(this).attr('ship_loc');
	var orderStatus = $(this).attr('orderStatus');
	
	// alert($("#O" + order_num).css('display'));
	var $header = $(this);
    var $span = $header.find('span.glyphicon');
   	// if($("#O" + order_num).is(':visible')){
   	if ($("#O" + order_num).css('display') == 'none') { 
        $span.removeClass('glyphicon-triangle-right');
        $span.addClass('glyphicon-triangle-bottom');
    } else {
        $span.removeClass('glyphicon-triangle-bottom');
        $span.addClass('glyphicon-triangle-right');
    }
	
	// alert(order_num);
	if (quoteDetailsAddedAlready[order_num] == 1) {
	    return;
	}
	quoteDetailsAddedAlready[order_num] = 1;


	var orderDetailsHtml = '';

	$.ajax			(	
			{url: "/portal/controller?formFunction=OrderDetails&order_num=" + order_num + "&ship_loc=" + ship_loc,
			cache: false,
			success:
				function(result) {
					// alert(result.session_expired);
					if (result.session_expired == "YES") {
						window.location.replace("/portal/controller");
	                    return;
					}
				
					// alert(result[0].partNum);
					orderDetailsHtml = orderDetailsHtml +
											'                        <div id="O' + order_num + '" class="panel-collapse collapse in" > ' +
											'                            <div class="panel-body row flx greybg" > ';
					//console.log(orderDetailsHtml);										
					orderDetailsHtml = orderDetailsHtml + getOrderDetailsHtmlQuo(order_num, ordered_by, orderStatus, result.totalPrice, result.salesRep);
					//console.log(orderDetailsHtml);
					
					orderDetailsHtml = orderDetailsHtml +
					
											'                                <div class="col-sm-9 borderS borderT borderB"> ';
					$.each	(result.item_details,																
						function(i, item) {
							orderDetailsHtml = orderDetailsHtml + getItemDetailsHtmlQuo(item, orderStatus);
						}
					 		);
	
					orderDetailsHtml = orderDetailsHtml +
											'                               </div> ';
					orderDetailsHtml = orderDetailsHtml +
											'                            </div> ' +
											'                        </div> ';

					$("#O" + order_num).html(orderDetailsHtml);
					$("#O" + order_num).toggle();
				}
			}
	  				);
}
                        );

function quoteScroll() {
    if ((orderOrQuoteScrollIsHappening == 1) || (lastQuoteDisplayed == "9999999")) {
		return;
	}
	
	// ---------------------------------------------------------------------------------------    

	var mostOfTheWayDown = ($(document).height() - $(window).height()) * 2 / 5;
    
    var top_of_element = $("#lastDivForAjaxQuo").offset().top;
    var bottom_of_element = $("#lastDivForAjaxQuo").offset().top + $("#lastDivForAjaxQuo").outerHeight();
    var bottom_of_screen = $(window).scrollTop() + window.innerHeight;
    var top_of_screen = $(window).scrollTop();
    
    if((bottom_of_screen > top_of_element) && (top_of_screen < bottom_of_element)){
    // if ($(window).scrollTop() >= mostOfTheWayDown) {
    
	// ---------------------------------------------------------------------------------------    

		// console.log("Subban. aft . lastQuoteDisplayed:" + lastQuoteDisplayed);
    	
		$(window).unbind("scroll");
        
		$.ajax	(	
			{	url: "/portal/controller?formFunction=OrderListSubsequent&quo=Y&cust_num=" + cust_num + "&cont_no=" + cont_no + "&dateOfLastQuoteDisplayed=" + dateOfLastQuoteDisplayed + "&lastQuoteDisplayed=" + lastQuoteDisplayed,
			async: false,
			error:
			    function(xhr, textStatus, errorThrown){
		        	alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);
			    	orderOrQuoteScrollIsHappening = 0;		        	
			    	$(window).scroll(scrollFunction);		        	
		        	return;
		     	},
			success:
				function(result) {
					// alert(result.session_expired);
					if (result.session_expired == "YES") {
				    	orderOrQuoteScrollIsHappening = 0;						
						window.location.replace("/portal/controller");
	                    return;
					}
					
					var noMoreResults = 1;
									
					$.each	(result.ord_quo_Array,
							 function(i, order) {
						    	$("#lastDivForAjaxQuo").before("<a " + 'class="quoteDetailsToggler panelLinks" order_num="' + order.order_num + '" ship_loc="' + order.ship_loc + '"  ordered_by="' + order.ordered_by + '" orderStatus="' + order.orderStatus + '" ' + "data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#O" + order.order_num + "\"> <div class=\"panel-heading whitebg \"> <h4 class=\"panel-title row\"> <div class=\"col-sm-2\" id=\"t-orderPlaced\"> <span class=\"glyphicon glyphicon-triangle-right\"></span>" + order.order_date + "</div> <div class=\"col-sm-2\" id=\"to-orderNumber\">" + order.order_num + " </div> <div class=\"col-sm-2\" id=\"to-expire-date\">" + order.date_closed + "</div> <div class=\"col-sm-2\" id=\"to-status\">" + order.orderStatus + "</div> </h4> </div></a>");
						    	$("#lastDivForAjaxQuo").before("<div id=\"O" + order.order_num + "\" style=\"display: none\"></div>");                        

						    	lastQuoteDisplayed = order.order_num;
								dateOfLastQuoteDisplayed = order.order_date;
								// console.log("in  " + lastQuoteDisplayed);
				    			noMoreResults = 0;
							 }
					 		);

					// console.log("aft " + lastQuoteDisplayed);

					if (noMoreResults == 1) {
			 			lastQuoteDisplayed = "9999999";
				 	}
				 	
					if (result.reachedLastOrder == "YES") {
						lastQuoteDisplayed = "9999999";	
    					// console.log("reachedLastOrder:" + result.reachedLastOrder);						
					}
					
					orderOrQuoteScrollIsHappening = 0;
					$(window).scroll(scrollFunction);
				}
			}
	  			);
    }
}

function quoteScrollO() {
    if ((lastQuoteDisplayed != "9999999") && ((($("#scrollerQuo").scrollTop() + $("#scrollerQuo").innerHeight()) * 1.05) >= $("#scrollerQuo")[0].scrollHeight)) {
		// console.log("b4  " + lastQuoteDisplayed);
        
		$.ajax	(	
			{	url: "/portal/controller?formFunction=OrderListSubsequent&quo=Y&cust_num=" + cust_num + "&cont_no=" + cont_no + "&dateOfLastQuoteDisplayed=" + dateOfLastQuoteDisplayed + "&lastQuoteDisplayed=" + lastQuoteDisplayed,
			async: false,
			error:
			    function(xhr, textStatus, errorThrown){
		        	alert('Request failed. ' + xhr + '.\n' + textStatus + '.\n' + errorThrown);
		        	return;
		     	},
			success:
				function(result) {
					// alert(result.session_expired);
					if (result.session_expired == "YES") {
						window.location.replace("/portal/controller");
	                    return;
					}
					
					var noMoreResults = 1;
									
					$.each	(result.ord_quo_Array,
							 function(i, order) {
						    	$("#lastDivForAjaxQuo").before("<a " + 'class="quoteDetailsToggler panelLinks" order_num="' + order.order_num + '" ship_loc="' + order.ship_loc + '"  ordered_by="' + order.ordered_by + '" orderStatus="' + order.orderStatus + '" ' + "data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#O" + order.order_num + "\"> <div class=\"panel-heading whitebg \"> <h4 class=\"panel-title row\"> <div class=\"col-sm-2\" id=\"t-orderPlaced\"> <span class=\"glyphicon glyphicon-triangle-right\"></span>" + order.order_date + "</div> <div class=\"col-sm-2\" id=\"to-orderNumber\">" + order.order_num + " </div> <div class=\"col-sm-2\" id=\"to-expire-date\">" + order.date_closed + "</div> <div class=\"col-sm-2\" id=\"to-status\">" + order.orderStatus + "</div> </h4> </div></a>");
						    	$("#lastDivForAjaxQuo").before("<div id=\"O" + order.order_num + "\" style=\"display: none\"></div>");                        

						    	lastQuoteDisplayed = order.order_num;
								dateOfLastQuoteDisplayed = order.order_date;
								// console.log("in  " + lastQuoteDisplayed);
				    			noMoreResults = 0;
							 }
					 		);

					// console.log("aft " + lastQuoteDisplayed);

					if (noMoreResults == 1) {
			 			lastQuoteDisplayed = "9999999";
				 	}
				 	
					if (result.reachedLastOrder == "YES") {
						lastQuoteDisplayed = "9999999";	
    					// console.log("reachedLastOrder:" + result.reachedLastOrder);						
					}				 	
				}
			}
	  			);
    }
}

// expand all orders
// eee

function testOrders() {
    var x = document.getElementsByClassName("orderDetailsToggler");
    
    for (var i = 0; i < x.length; i++) {
        eventFire(x[i], 'click');
    }
}

function eventFire(el, etype) {
  	if (el.fireEvent) {
		el.fireEvent('on' + etype);
	} else {
    	var evObj = document.createEvent('Events');
    	evObj.initEvent(etype, true, false);
    	el.dispatchEvent(evObj);
  	}
}

</script>

<script>
	var positionElements = function(){
		var height = 0;
		$(".navbar-fixed-top, #tabContainer, #accordion .titlePanel" ).each(function(idx,elem){
			height = height + $(elem).height();
			// console.log(height,idx,elem)
		});
		
		$("body").css("padding-top","height")
		
		// console.log(height)
	}
	$(function(){ 
  		positionElements()
		$(window).on("resize",positionElements);
	})

</script>



</html>


