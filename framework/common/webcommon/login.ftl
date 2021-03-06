<html>

	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title>vbiz- Login</title>	
        <link rel="shortcut icon" href="/images/vbiz.ico" />
        <link rel="stylesheet" href="/vasista/login.css" type="text/css"/> 
	</head>
	<#if requestAttributes.uiLabelMap?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
	<#assign useMultitenant = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "multitenant")>
	
	<#assign username = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
	<#if username != "">
	  <#assign focusName = false>
	<#else>
	  <#assign focusName = true>
	</#if>
	<body background="<@ofbizContentUrl>/vasista/complogos/saree_BG2.jpg</@ofbizContentUrl>" height="100%" width="100%">
        <#include "component://vasista/includes/messages.ftl"/>
        <#if (errorMessage?has_content || errorMessageList?has_content || eventMessage?has_content || eventMessageList?has_content)>
		<#else>
			<div align="center">		
		    	<img id="welcomePage" src="/vasista/complogos/nhdc_bn.jpg" height="100%" width="100%">
		    </div>
		</#if>
		<div id="loginDiv" align="center">
			<p><img src="/vasista/complogos/logo_nhdc.png" alt="" height="75" width="63" align="middle" border="0"></p>
			<table width="180" border="0" cellspacing="0" cellpadding="0" style='background-color: white; border-radius:10px;'>
				<tr>
					<td colspan="3" ><img src="/vasista/images/login_top_border.gif" alt="" height="65" width="530" border="0"></td>
				</tr>
				<tr>
					<td ><img src="/vasista/images/login_left_border.gif" alt="" height="248" width="34" border="0"></td>
					<td >
						<div align="center">
							<form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform">							
							<table width="346" border="0" cellspacing="10" cellpadding="0">
								<tr>
									<td><font color="#999999" face="Verdana, Arial, Helvetica, sans-serif">User Name</font></td>
									<td>
										<div align="center">
											<input type="text" name="USERNAME" value="${username}" size="24"></div>
									</td>
								</tr>
								<tr>
									<td><font color="#999999" face="Verdana, Arial, Helvetica, sans-serif">Password</font></td>
									<td>
										<div align="center">
											<input type="password" name="PASSWORD"  value="" size="24"></div>
									</td>
								</tr>
								<tr>
									<td><font color="#999999" face="Verdana, Arial, Helvetica, sans-serif">Company ID</font></td>
									<td>
										<div align="center">
											<input type="text" name="tenantId" value="${parameters.tenantId?if_exists}" size="24"></div>
									</td>
								</tr>								
								<tr>
									<td></td>
									<td>
										<div align="center">
											<br>
											<input type="image" src="/vasista/images/login_button.gif" alt="Login" 
												type="image" onmousedown='this.src="/vasista/images/login_button_pressed.gif"' 
	   											onmouseup='this.src="/vasista/images/login_button.gif"'></div>
									</td>
								</tr>
								
								<tr>
									<td></td>
									<td> <div align="center"> <br/><a style="color:#999999; font-family:Verdana, Arial, Helvetica, sans-serif" 
											href="<@ofbizUrl>forgotPassword</@ofbizUrl>">Forgot your password?</a></td></div>
								</tr>
							</table>
							<input type="hidden" name="JavaScriptEnabled" value="N"/>
							</form>
						</div>
					</td>
					<td >
						<div align="right">
							<img src="/vasista/images/login_right_border.gif" alt="" height="248" width="35" border="0"></div>
					</td>
				</tr>
				<tr>
					<td colspan="3" ><img src="/vasista/images/login_down_border.gif" alt="" height="42" width="530" border="0"></td>
				</tr>
			</table>
		<#-- 	<p><a href="http://www.vasista.in"><img src="/vasista/powered_by_vasista.gif" alt="" height="54" width="255" border="0"></a></p> -->
			<p><br>
			</p>
			<p><br>
			</p>
			<p><font size="1" color="#999999" face="Verdana, Arial, Helvetica, sans-serif"> Copyright &copy; 2015 Vasista Enterprise Solutions Private Ltd. All rights reserved.</font> </p>
			<p></p>   
		</div>
		
		<script language="JavaScript" type="text/javascript">
		
		   
		    localStorage.setItem("on_load_counter", 0);
		
		    document.loginform.JavaScriptEnabled.value = "Y";
		    
		    var loginDiv = document.getElementById('loginDiv');
		    loginDiv.style.display = "none";
		   
		    var welcomePage = document.getElementById('welcomePage');
			if (!welcomePage) {
			    showLogin();
			}
			
			var animationComplete = true;
			window.onload=function(){setTimeout(hideImage,200)};
			
			function hideImage(){
			   if (animationComplete && welcomePage.style.opacity !== '0') {
			        animationComplete = false;
			        for (var i = 1; i <= 100; i++) {
			            setTimeout((function (x) {
			                return function () {
			                    function_fade_out(x)
			                };
			            })(100 - i), i * 15);
			        }
			        
			    }
			    setTimeout(showLogin,1500)
			}
			
			function function_opacity(opacity_value) {
			    welcomePage.style.opacity = opacity_value / 100;
			    welcomePage.style.filter = 'alpha(opacity=' + opacity_value + ')';
			}
			
			function function_fade_out(opacity_value) {
			    function_opacity(opacity_value);
			    if (opacity_value == 1) {
			        welcomePage.style.display = 'none';
			        animationComplete = true;
			    }
			}
			function showLogin() {
			    loginDiv.style.display = "block";
		        <#if focusName>
				    document.loginform.USERNAME.focus();
				<#else>
				    document.loginform.PASSWORD.focus();
				</#if>
			}
		  	//myDiv.style.display = "block";
		</script>			
	</body>
</html>







