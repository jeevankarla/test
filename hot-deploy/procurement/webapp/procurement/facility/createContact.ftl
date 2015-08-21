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


<script language="JavaScript" type="text/javascript">

	 $(document).ready(function(){
	 		$('#postalAddressTable').hide();
			$('#phoneNumberTable').hide();
			$('#emailTable').hide();
			
			
			$('.onlyNumbers').bind('keyup blur',function(){ 
    			$(this).val( $(this).val().replace(/[^\0-9]/g,''));}
			);
			
			$('.upperCaseOnly').bind('keyup blur',function(){ 
	    		var actualVal = $(this).val();
	    		$(this).val( actualVal.toUpperCase());}
			);
			
			
			
	 });
	 
  	function validateEmail(emailAddress) { 
    	var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    	return re.test(emailAddress);
	} 
	 

	function changeTable() {
	
		var preContactMechTypeId = $('#preContactMechTypeId').val();
		if(preContactMechTypeId == "POSTAL_ADDRESS" ){
			$('#postalAddressTable').show();
			$('#phoneNumberTable').hide();
			$('#emailTable').hide();
		}
		if(preContactMechTypeId == "TELECOM_NUMBER" ){
			$('#postalAddressTable').hide();
			$('#phoneNumberTable').show();
			$('#emailTable').hide();
		}
		if(preContactMechTypeId == "EMAIL_ADDRESS" ){
			$('#postalAddressTable').hide();
			$('#phoneNumberTable').hide();
			$('#emailTable').show();
		}
		
	}
	
  	$(document).ready(function call(){
		$('input[name=submitButton]').click (function call(){
			var preContactMechTypeId = $('#preContactMechTypeId').val();
			var contactMechPurposeTypeId = $('#contactMechPurposeTypeId').val();
			
			var postalCode = $('#postalCode').val();
			var address1 = $('#address1').val();
			var city = $('#city').val();
			
			var phoneNumber = $('#phoneNumber').val();
			var assistantPhoneNumber = $('#assistantPhoneNumber').val();
			
			if(preContactMechTypeId=="POSTAL_ADDRESS"){
				if(contactMechPurposeTypeId == ""){
		  			alert("Please Select ContactMechPurposeType");
		  			return false;
		  		}
		  		
		  		if(address1 == ""){
		  			alert("Please Enter address1");
		  			return false;
		  		}
		  		if(city == ""){
		  			alert("Please Enter city");
		  			return false;
		  		}
		  		if(postalCode == ""){
		  			alert("Please Enter postalCode");
		  			return false;
		  		}else{
		  			if(postalCode.length<6){
		  				alert("Invalid Postal Code, Check it once");
		  				return false;
		  			}
		  		}
			}
			if(preContactMechTypeId == "TELECOM_NUMBER"){
				if(phoneNumber == ""){
		  			alert("Please Enter PhoneNumber");
		  			return false;
		  		}else{
		  			if(phoneNumber.length<10){
		  				alert("Invalid phone number, Check it once");
		  				return false;
		  			}
		  		
		  		}
		  		if(assistantPhoneNumber == ""){
		  			alert("Please Enter Assistant PhoneNumber");
		  			return false;
		  		}else{
		  			if(assistantPhoneNumber.length<10){
		  				alert("Invalid Assistant PhoneNumber, Check it once");
		  				return false;
		  			}
		  		}
			}
			if(preContactMechTypeId=="EMAIL_ADDRESS"){
				if(!validateEmail(emailAddress)){
			    	alert("invalid email");
			    	$('#email').css('background', 'yellow'); 
			       	setTimeout(function () {
			           	$('#email').css('background', 'white').focus(); 
			       	}, 800);
			    	return false;
			    }
			}
		});
	});

	
</script>

<#if !preContactMechTypeId?has_content>
    <form method="post" action="<@ofbizUrl>createFacilityContactMech?facilityId=${partyId}</@ofbizUrl>" name="createcontactmechform" id="createcontactmechform">
      
      
      <input type="hidden" name="partyId" value="${partyId}" />
      <table class="basic-table" cellspacing="0">
        <tr>
          <td class="label"><FONT COLOR="#04B431"><b>${uiLabelMap.PartySelectContactType}</b></FONT></td>
          <td>
            <select name="preContactMechTypeId" id="preContactMechTypeId" onChange="changeTable()">
            	<option value="">Select</option>
            	<#list contactTypeList as contactType>
	                <#assign purpose = delegator.findOne("ContactMechType", {"contactMechTypeId" : contactType}, true)>
	                <option value="${contactType}">${purpose.description?if_exists} </option>
	           </#list>
            </select>
          </td>
        </tr>
     </table>
     
     
	 <table id="postalAddressTable" cellpadding="0" cellspacing="0">
		<tr>
		  <th size="100"><FONT COLOR="#04B431"><b> Postal Address: </b></FONT></th>
		</tr>
		<tr>
			<td class="label"><FONT COLOR="#045FB4"><b>ContactMechPurposeType</b></FONT></td>
			<td class="button-col">
		        <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId">
		          <option></option>
		          <#list contactMechPurposeTypeList as contactMechPurposeType>
		            <option value="${contactMechPurposeType.contactMechPurposeTypeId}">${contactMechPurposeType.get("description",locale)}</option>
		          </#list>
		        </select>
		     </td>
	 	</tr>
		<tr>
	      <td class="label"><FONT COLOR="#045FB4"><b>*${uiLabelMap.PartyToName}</b></FONT></td>
	      <td>
	        <input type="text" size="50" maxlength="100" name="toName"  />
	      </td>
	    </tr>
		<#--
		<tr>
	      <td class="label"><FONT COLOR="#045FB4"><b>*${uiLabelMap.PartyAttentionName}</b></FONT></td>
	      <td>
	        <input type="text" size="50" maxlength="100" name="attnName" />
	      </td>
	    </tr>-->
		<tr>
	      <td class="label"><FONT COLOR="#045FB4"><b>*${uiLabelMap.PartyAddressLine1} </b></FONT></td>
	      <td>
	        <input type="text" size="100" maxlength="255" name="address1" id="address1" />
	      </td>
	    </tr>
	    <tr>
	      <td class="label"><FONT COLOR="#045FB4"><b>${uiLabelMap.PartyAddressLine2}</b></FONT></td>
	      <td>
	        <input type="text" size="100" maxlength="255" name="address2" />
	      </td>
	    </tr>
		<tr>
	      <td class="label"><FONT COLOR="#045FB4"><b>*${uiLabelMap.PartyCity}</b></FONT></td>
	      <td>
	        <input type="text" size="50" maxlength="100" name="city" id="city" />
	      </td>
	    </tr>
		<tr>
	      <td class="label"><FONT COLOR="#045FB4"><b>${uiLabelMap.PartyState}</b></FONT></td>
	      <td>
	        <select name="stateProvinceGeoId" id="editcontactmechform_stateId">
	          <option></option>
	          ${screens.render("component://common/widget/CommonScreens.xml#states")}
	        </select>
	      </td>
	    </tr>
		<tr>
	      <td class="label"><FONT COLOR="#045FB4"><b>*${uiLabelMap.PartyZipCode} </b></FONT></td>
	      <td>
	        <input type="text" size="30" maxlength="6" name="postalCode" id="postalCode" class="onlyNumbers" />
	      </td>
	    </tr>
		
		
		<tr>
	      <td class="label"><FONT COLOR="#045FB4"><b>${uiLabelMap.CommonCountry}</b></FONT></td>
	      <td>
	        <select name="countryGeoId" id="editcontactmechform_countryId">
	         
	          <#assign defaultCountryGeoId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "country.geo.id.default")>
	          <option selected="selected" value="${defaultCountryGeoId}">
	          <#assign countryGeo = delegator.findByPrimaryKey("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",defaultCountryGeoId))>
	          ${countryGeo.get("geoName",locale)}
	          </option>
	          <option></option>
	          ${screens.render("component://common/widget/CommonScreens.xml#countries")}
	        </select>
	      </td>
	    </tr>
	    <#assign isUsps = Static["org.ofbiz.party.contact.ContactMechWorker"].isUspsAddress(mechMap.postalAddress)>
	    <tr>
	      <td class="label"><FONT COLOR="#045FB4"><b>${uiLabelMap.PartyIsUsps}</b></FONT></td>
	      <td><#if isUsps>${uiLabelMap.CommonY}<#else>${uiLabelMap.CommonN}</#if>
	      </td>
	    </tr>
	    <tr>
          <td></td>
          <td><input type="submit" class="smallSubmit" value="Submit" name="submitButton" onClick="javascript:call();"/></td>
        </tr>
	 </table>
	 
	 
	 <table id="phoneNumberTable" cellpadding="0" cellspacing="0">
		<tr>
		  <th><FONT COLOR="#04B431"><b> Phone Number: </b></FONT></th>
		</tr>
		<tr>
		    <td class="label"><FONT COLOR="#045FB4"><b>* Mobile Number</b></FONT></td>
		    <td>
		      	<input type="text" name="phoneNumber" id="phoneNumber" size="30" maxlength="10" class="onlyNumbers" autocomplete="off"/>
		    </td>
		</tr>
		<tr>
		    <td class="label"><FONT COLOR="#045FB4"><b> LandLine Number</b></FONT></td>
		    <td>
		      	<input type="text" name="landLineNumber" id="landLineNumber" size="30" maxlength="11" class="onlyNumbers" autocomplete="off"/>
		    </td>
		</tr>
		<tr>
		    <td class="label"><FONT COLOR="#045FB4"><b>* Assistant Mobile Number</b></FONT></td>
		    <td>
		      	<input type="text" name="assistantPhoneNumber" id="assistantPhoneNumber" size="30" maxlength="10" class="onlyNumbers" autocomplete="off"/>
		    </td>
		</tr>
		<tr>
          <td></td>
          <td><input type="submit" class="smallSubmit" value="Submit" name="submitButton" onClick="javascript:call();"/></td>
        </tr>
		
		
	 </table>
	 
	 
	 <table id="emailTable" cellpadding="0" cellspacing="0">
		 <tr>
		  	<th><FONT COLOR="#04B431"><b> Email Id: </b></FONT></th>
		 </tr>
		 <tr>
	      <td class="label"><FONT COLOR="#045FB4"><b>* E-mail Address</b></FONT></td>
	      <td>
	        <input type="text" size="60" maxlength="255" name="emailAddress" id="emailAddress" />
	      </td>
	    </tr>
	    <tr>
          <td></td>
          <td><input type="submit" class="smallSubmit" value="Submit" name="submitButton" onClick="javascript:call();"/></td>
        </tr>
	 </table>
	 
      
    </form>
</#if>