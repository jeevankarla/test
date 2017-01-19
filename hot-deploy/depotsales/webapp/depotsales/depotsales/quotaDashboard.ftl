
<input type="hidden" name="branchId" id="branchId" value="${branchId}">
<input type="hidden" name="partyId1" id="partyId1" value="${partyId}">
<input type="hidden" name="effectiveDate1" id="effectiveDate1" value="${effectiveDate}">


<script type="text/javascript">
	
	
	//for Month Picker
$(document).ready(function(){
    $(".monthPicker").datepicker( {
       changeMonth: true,
       changeYear: true,
       showButtonPanel: true,
        minDate: new Date("04-01-2016"),
        maxDate: new Date("03-01-2017"),
       dateFormat: 'yy-mm',
       onClose: function(dateText, inst) { 
           var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
           var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
           $(this).datepicker('setDate', new Date(year, month, 1));
       }
});
$(".monthPicker").focus(function () {
       $(".ui-datepicker-calendar").hide();
       $("#ui-datepicker-div").position({
           my: "center top",
           at: "center bottom",
           of: $(this)
       });    
    });
});
	
	

	
	
$(document).ready(function(){

autoCompletePartyId();
var branchId = $("#branchId").val();
var partyId = $("#partyId1").val();
var effectiveDate1 = $("#effectiveDate1").val();

  var branchList = "";
  <#list formatList as eachList>
	branchList = branchList + "<option value='${eachList.payToPartyId}' >${eachList.productStoreName}</option>";
  </#list>

     $("#branchId2").html(branchList);
     
     
     $("#branchId2").val(branchId);
     $("#partyId").val(partyId);
      $("#effectiveDate").val(effectiveDate1);
     
		
	});
	
	
	function autoCompletePartyId(){
	
		var stateWise = $("#stateWise").val();
		
		
		var dataJson = {"stateWise": stateWise};
		//$('div#orderSpinn1').html('<img src="/images/loadingImage.gif" height="70" width="70">');
		jQuery.ajax({
                url: 'getPartiesBasedOnBranch',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
					
					
					var	partyNameObj = result["partyNameObj"];
				   
				    $('div#orderSpinn1').html('');
				    
					 $("#partyId").autocomplete({ source: partyNameObj }).keydown(function(e){});
						
               		}
               	}							
		});
	
	 
	 
		 }
	
	
	 function clearData(){
	 
	   $("#partyId").val('');
	   
	   
	   if(($("#partyId").val()).length == 0)
	   $("#partyTooltip").html('');
	 
	 }
	
	
</script>


<style type="text/css">

	<style type="text/css">
	 	.labelFontCSS {
	    	font-size: 13px;
		}
		.form-style-8{
		    max-width: 400px;
		    max-height: 280px;
		    max-right: 10px;
		    margin-top: 10px;
			margin-bottom: -5px;
		    padding: 15px;
		    box-shadow: 1px 1px 25px rgba(0, 0, 0, 0.35);
		    border-radius: 15px;
		    border: 1px solid #305A72;
		}
		
	</style>

</style>			

     <div align='center' name ='displayMsg' id='orderSpinn1'/></div>
        
      <form method="post" name="QuotaDashboard" id="QuotaDashboard" action="<@ofbizUrl>QuotaDashboard</@ofbizUrl> " class="basic-form">
        
		  <table width="60%" border="0" cellspacing="0" cellpadding="0" class="form-style-8">
				<tr>
				<td width="40%">
				
				<#--
				<tr>
				  <td align='left' valign='middle' nowrap="nowrap">${uiLabelMap.Branch} :</td>
				  
				  <#if productStoreId?exists && productStoreId?has_content>  
								  	  		   
				  <td valign='middle'><font color="green">          
				    <select name="branchId2" id="branchId2" onchange="javascript:clearData();" >
				    <option value >         		
				  </td>
				  
				  <#else>
				  <td valign='middle'><font color="green">          
				    <select name="branchId2" id="branchId2" onchange="javascript:clearData();" />         		
				  </td>
				   </#if>
				  
				  <td><br/></td>
				</tr>
				 -->
				 
				 <tr>
				  <td align='left' valign='middle' nowrap="nowrap">State :</td>
				  
				  <#if stateWise?exists && stateWise?has_content>  
								  	  		   
				  <td valign='middle'><font color="green"> 
				   <select name="stateWise" id="stateWise" onchange='javascript:autoCompletePartyId();'>
				   <option value="${stateWise}">${stateName}</option>    
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select>         		
				  </td>
				  
				  <#else>
				  <td valign='middle'><font color="green">          
				    <select name="stateWise" id="stateWise" onchange='javascript:autoCompletePartyId();'>
				     <option value="IN-TN">TAMILNADU</option>
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select>       		
				  </td>
				   </#if>
				  
				  <td><br/></td>
				</tr>
				
				
				
				<tr><td><br/></td></tr>
				
				
				<tr>
				  <td align='left' valign='middle' nowrap="nowrap">Customer Name:</td>
				  <td valign='middle'><font color="green">          
				     <input type='text' id='partyId' name='partyId' placeholder="Enter Customer Name"  size='20'/><p><label  align="left" id="partyTooltip" style="color: blue"></label><p>  		
				  </td>
				  <td><br/></td>
				</tr>
			
			 
			   <tr><td><br/></td></tr>
			   <#--
			    <tr>
				  <td align='left' valign='middle' nowrap="nowrap">Party Classification :</td>
				  
				  <#if partyClasificationName?exists && partyClasificationName?has_content>  
								  	  		   
				  <td valign='middle'><font color="green"> 
				   <select name="partyClassificationId2" id="partyClassificationId2" >
				   <option value="${partyClassification}">${partyClasificationName}</option>    
				     <#list  partyClassificationList as partyClassificationList>
						<option value='${partyClassificationList.partyClassificationTypeId?if_exists}'>${partyClassificationList.description?if_exists}</option>
					 </#list> 
				  </select>         		
				  </td>
				  
				  <#else>
				  <td valign='middle'><font color="green"> 
				    <select name="partyClassificationId2" id="partyClassificationId2" >
				    <option value=""></option>     
				     <#list  partyClassificationList as partyClassificationList>
						<option value='${partyClassificationList.partyClassificationTypeId?if_exists}'>${partyClassificationList.description?if_exists}</option>
					 </#list> 
				  </select>       		
				  </td>
				   </#if>
				  
				  <td><br/></td>
				</tr>
			   
			   <tr><td><br/></td></tr>
			   -->
			   
			   <tr>
				  <td align='left' valign='middle' nowrap="nowrap">Month :</td>
				  <td valign='middle'><font color="green">          
				    <input  type="text" size="18pt" id="effectiveDate" readonly  name="effectiveDate" onmouseover='monthPicker()' class="monthPicker" /> 		
				  </td>
				  <td><br/></td>
				</tr>
			   
			   <tr><td><br/></td></tr>
			   
			   <tr>
				  <td align='left' valign='middle' nowrap="nowrap"></td>
				  <td valign='middle'><font color="green">          
				     <input type="submit" style="padding:.3em" value="Find" name="submit" id="submit" onclick= 'javascript:formSubmit(this);' />     		
				  </td>
				  <td><br/></td>
				</tr>
			  
		</table>
            </form>
 	

 