<html>	<head>		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title>milkosoft- Login</title>	
        <link rel="shortcut icon" href="/images/vbiz.ico" />
        <link rel="stylesheet" href="/vasista/login.css" type="text/css"/> 	</head><#if requestAttributes.uiLabelMap?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#assign useMultitenant = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "multitenant")>

<#assign username = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
<#if username != "">
  <#assign focusName = false>
<#else>
  <#assign focusName = true>
</#if>	<body bgcolor="#ffffff">
        <#include "component://vasista/includes/messages.ftl"/>				<div align="center">			<p><img src="/vasista/complogos/milkosoft_logo.gif" alt="" height="60" width="262" align="middle" border="0"></p>			<table width="180" border="0" cellspacing="0" cellpadding="0">				<tr>					<td colspan="3"><img src="/vasista/images/login_top_border.gif" alt="" height="65" width="530" border="0"></td>				</tr>				<tr>					<td><img src="/vasista/images/login_left_border.gif" alt="" height="248" width="34" border="0"></td>					<td>						<div align="center">
							<form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform">														<table width="346" border="0" cellspacing="10" cellpadding="0">								<tr>									<td><font color="#999999" face="Verdana, Arial, Helvetica, sans-serif">User Name</font></td>									<td>										<div align="center">											<input type="text" name="USERNAME" value="${username}" size="24"></div>									</td>								</tr>								<tr>									<td><font color="#999999" face="Verdana, Arial, Helvetica, sans-serif">Password</font></td>									<td>										<div align="center">											<input type="password" name="PASSWORD"  value="" size="24"></div>									</td>								</tr>								<tr>									<td><font color="#999999" face="Verdana, Arial, Helvetica, sans-serif">Company ID</font></td>									<td>										<div align="center">											<input type="text" name="tenantId" value="${parameters.tenantId?if_exists}" size="24"></div>									</td>								</tr>																<tr>									<td></td>									<td>										<div align="center">											<br>											<input type="image" src="/vasista/images/login_button.gif" alt="Login" 
												type="image" onmousedown='this.src="/vasista/images/login_button_pressed.gif"' 
	   											onmouseup='this.src="/vasista/images/login_button.gif"'></div>									</td>								</tr>																<tr>									<td></td>									<td> <div align="center"> <br/><a style="color:#999999; font-family:Verdana, Arial, Helvetica, sans-serif" 
											href="<@ofbizUrl>forgotPassword</@ofbizUrl>">Forgot your password?</a></td></div>								</tr>							</table>
							<input type="hidden" name="JavaScriptEnabled" value="N"/>
							</form>						</div>					</td>					<td>						<div align="right">							<img src="/vasista/images/login_right_border.gif" alt="" height="248" width="35" border="0"></div>					</td>				</tr>				<tr>					<td colspan="3"><img src="/vasista/images/login_down_border.gif" alt="" height="42" width="530" border="0"></td>				</tr>			</table>			<p><a href="http://www.vasista.in"><img src="/vasista/powered_by_vasista.gif" alt="" height="54" width="255" border="0"></a></p>			<p><br>			</p>			<p><br>			</p>			<p><font size="1" color="#999999" face="Verdana, Arial, Helvetica, sans-serif"> Copyright &copy; 2011 Vasista Inc. All rights reserved</font> </p>			<p></p>		</div>
<script language="JavaScript" type="text/javascript">
  document.loginform.JavaScriptEnabled.value = "Y";
  <#if focusName>
    document.loginform.USERNAME.focus();
  <#else>
    document.loginform.PASSWORD.focus();
  </#if>
</script>				</body></html>