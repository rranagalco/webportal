<script>
function setSignInOutButtons() {
	document.getElementById("myAccountButtons").innerHTML = 
        '<a href="javascript:void(0)" style="text-decoration: none; padding-bottom:0px;" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">' +
        '   <img class="link" src="/images/myaccount.png" alt="myaccount" width="20" height="20">My Account <span class="ion-android-arrow-dropdown"></span>' +
        '   <div id="myAccountButtonsTitle">' +
        '      <a href="/portal/controller?formFunction=LogOff" style="padding-top:0px">' + '<div class="navsmall text-center" style="line-height:20px"><span class="glyphicon glyphicon-log-out" aria-hidden="true"></span>Logout</div>' +
        '      </a>' +	 
        '   </div>' +
        '</a>' +
        '<div class="dropdown-menu theme-features row" role="menu" align="center" style="margin-top:20px"> ' +
	    '   <div align = "center">' +
	    '      <a href="/portal/controller?formFunction=LogOff" class="btn btn-primary btn-sm headerbtn loginbtn"><span class="btnmyacct">Logout</span></a>' +                          
	    '      <a href="/portal/controller?formFunction=UpdateAccountInfo" class="btn btn-primary btn-sm headerbtn loginbtn"><span class="btnmyacct">Update Profile</span></a>' +                          
	    '      <a href="/portal/controller?formFunction=ChangePassword" class="btn btn-primary btn-sm headerbtn loginbtn"><span class="btnmyacct">Change Password</span></a>' +                          
	    '      <div style="width: 90px; float: left;"><img class="link" src="/images/package.png" alt="Orders" width="20" height="20"><p/><a href="/portal/controller?formFunction=OrderList&activeTab=orders" class="logio_s">My Orders</a></div>' +
	    '      <div style="margin-left: 5px;"><img class="link" src="/images/quote-icon.png" alt="Quotes" width="20" height="20"><p/><a href="/portal/controller?formFunction=OrderList&activeTab=quotes" class="logio_s">My Quotes</a></div>' +
	    '   </div>' +
	    '</div>';     

    // <a href="/portal/controller?formFunction=SignIn" class="btn btn-primary btn-sm headerbtn loginbtn"><span class="btnmyacct">LogIn</span></a>  
    // <a href="/portal/controller?formFunction=SignUp" class="btn btn-primary btn-sm headerbtn create_accbtn" ><span class="btnmyacct">Create Account</span></a>                              
}
</script>
