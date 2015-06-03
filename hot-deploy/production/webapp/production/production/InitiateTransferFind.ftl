<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.js</@ofbizContentUrl>"></script>
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="JavaScript" type="text/javascript">
 var facilityJSON = ${StringUtil.wrapString(facilityJSON)!'[]'};	
 var productNameJSON = ${StringUtil.wrapString(productNameJSON)!'[]'};
 var products="";
  
function setfacilityId(facility){
 var facilityId= facility.value;
 products = facilityJSON[facilityId];
 $("#productId").autocomplete({ source: products }).keydown(function(e){});
}
var productName;
	function displayName(selection){
	   value = $("#productId").val();
	   productName = productNameJSON[value];
	   $("#productName").html(productName);
	}

</script>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3><left>Find Product Inventory</left></h3>
    </div>
    <div class="screenlet-body">
    	  <form name="FindFacilityProducts" id = "FindFacilityProducts" action=""  method="post">
    	  <input type="hidden" name="hideSearch" id="hideSearch" value ="N"/>
    	   <table class="basic-table" cellspacing="0">
    	   	<tr>
    	   		<td class="label">Plant / Silo&nbsp;&nbsp;&nbsp;:</td>
    	   		<td><@htmlTemplate.lookupField  formName="FindFacilityProducts" event="onblur" action="javascript:setfacilityId(this);" name="facilityId" id="facilityId" fieldFormName="LookupFacility"/></td>
    	   	</tr>
    	   	<tr>
    	   		<td class="label">Product&nbsp;&nbsp;&nbsp;:</td>
    	   		<td><input type="text" name="productId" id="productId" onblur='javascript:displayName(this);'/><span  class="tooltip" id="productName"></span>
    	   	</tr>
    	   	<tr>
                <td>&nbsp;</td>
                <td>
                	<input type="submit" name="submit" id="submit" value="Find" style="buttontext"/> 
                </td>
              </tr>
    	   </table>
          </form>    
    </div>
</div>