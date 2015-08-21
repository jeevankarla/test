<#include "CreateNewPriceChartInc.ftl"/>
<#--
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>
-->
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/flexselect.css</@ofbizContentUrl>">
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/liquidmetal.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/jquery.flexselect.js</@ofbizContentUrl>"></script>
<script type="application/javascript">

		
		
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
	function makeDayDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
	
    $(document).ready(function(){
            $("#wizard-2").steps({
                headerTag: "h3",
                bodyTag: "section",
                transitionEffect: "slideLeft",
                onStepChanging: function (event, currentIndex, newIndex)
                {	
                	if(currentIndex == 0 && newIndex == 1){
                	
                		//populate SlickGrid starts here
						    gridShowCall();
						 	setupGrid1();
				        jQuery(".grid-header .ui-icon")
				            .addClass("ui-state-default ui-corner-all")
				            .mouseover(function(e) {
				                jQuery(e.target).addClass("ui-state-hover")
				            })
				            .mouseout(function(e) {
				                jQuery(e.target).removeClass("ui-state-hover")
				            });		
						jQuery("#gridContainer").resizable();	   			
				    	var tabindex = 1;
				    	jQuery('input,select').each(function() {
				        	if (this.type != "hidden") {
				            	var $input = $(this);
				            	$input.attr("tabindex", tabindex);
				            	tabindex++;
				        	}
				    	});
				
				    	var rowCount = jQuery('#myGrid1 .slick-row').length;
						if (rowCount > 0) {			
							$(mainGrid.getCellNode(rowCount-1, 0)).click();		   
				    	}
    	 		      //populate SlickGrid ends here
                	
                		return true;
                	}
                	if(currentIndex == 1 && newIndex == 2){
                	
                		//populate SlickGrid starts here
						    gridShowCall();
						 	setupGrid2();
				        jQuery(".grid-header .ui-icon")
				            .addClass("ui-state-default ui-corner-all")
				            .mouseover(function(e) {
				                jQuery(e.target).addClass("ui-state-hover")
				            })
				            .mouseout(function(e) {
				                jQuery(e.target).removeClass("ui-state-hover")
				            });		
						jQuery("#gridContainer").resizable();	   			
				    	var tabindex = 1;
				    	jQuery('input,select').each(function() {
				        	if (this.type != "hidden") {
				            	var $input = $(this);
				            	$input.attr("tabindex", tabindex);
				            	tabindex++;
				        	}
				    	});
				
				    	var rowCount = jQuery('#myGrid1 .slick-row').length;
						if (rowCount > 0) {			
							$(mainGrid.getCellNode(rowCount-1, 0)).click();		   
				    	}
    	 		      //populate SlickGrid ends here
                	
                	
                	     return true;
                	}
                	if(currentIndex == 2 && newIndex == 3){
                	
                	
                		//populate SlickGrid starts here
						    gridShowCall();
						 	setupGrid3();
				        jQuery(".grid-header .ui-icon")
				            .addClass("ui-state-default ui-corner-all")
				            .mouseover(function(e) {
				                jQuery(e.target).addClass("ui-state-hover")
				            })
				            .mouseout(function(e) {
				                jQuery(e.target).removeClass("ui-state-hover")
				            });		
						jQuery("#gridContainer").resizable();	   			
				    	var tabindex = 1;
				    	jQuery('input,select').each(function() {
				        	if (this.type != "hidden") {
				            	var $input = $(this);
				            	$input.attr("tabindex", tabindex);
				            	tabindex++;
				        	}
				    	});
				
				    	var rowCount = jQuery('#myGrid1 .slick-row').length;
						if (rowCount > 0) {			
							$(mainGrid.getCellNode(rowCount-1, 0)).click();		   
				    	}
    	 		      //populate SlickGrid ends here
                	
                	
                		return true;
                	}
                	
                	return true;
                },
                onFinishing: function (event, currentIndex)
                {	
                    return true;
                },
                onFinished: function (event, currentIndex)
                {
                   
				    processPriceChartEntryInternal("CreateNewPriceChart", "CreateNewPriceChart");
				    var form = ($(this)).parent();
                	form.submit();
                }
            });
	}); 
	
	$(document).ready(function(){
		
		makeDatePicker("fromDate","fromDateId");
        makeDatePicker("thruDate","fromDateId");
		//$(this.target).find('input').autocomplete();
		$('#ui-datepicker-div').css('clip', 'auto');
	});
	  
	    
</script>

<style type="text/css">
.styled-button {
	-webkit-box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	-moz-box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	color:#333;
	background-color:#FA2;
	border-radius:5px;
	-moz-border-radius:5px;
	-webkit-border-radius:5px;
	border:none;
	font-family:'Helvetica Neue',Arial,sans-serif;
	font-size:25px;
	font-weight:700;
	height:32px;
	padding:4px 16px;
	text-shadow:#FE6 0 1px 0
}
</style>
	
<#assign orderInfo = {}>
<#assign quoteInfo = {}>
<#assign orderAdjInfo = {}>
<#assign orderTermInfo = {}>
<#assign orderPayTermInfo = []>
<#assign orderShipTermInfo = []>
<#assign bedCheck = "">
<#assign quoteTerm = "">
<#assign orderTypeId = "">
<#if orderEditParam?has_content>
	<#assign orderInfo = orderEditParam.get("orderHeader")?if_exists>
	<#assign quoteInfo = orderEditParam.get("quoteDetails")?if_exists>
	<#if quoteInfo.quoteId?has_content>
	<#assign quoteTerm = delegator.findByAnd("QuoteTerm", {"quoteId" : quoteInfo.quoteId, "termTypeId" : "BED_PUR" })>
	<#if quoteTerm!= "undefined" && quoteTerm?has_content>
		<#assign bedCheck = quoteTerm[0].termTypeId?if_exists>
	</#if>
	</#if>
	<#assign orderAdjInfo = orderEditParam.get("orderAdjustment")?if_exists>
	<#assign orderTermInfo = orderEditParam.get("orderTerms")?if_exists>
	<#assign orderTypeId = orderInfo.orderTypeId>
	<#if orderTermInfo?has_content>
		<#assign orderPayTermInfo = orderTermInfo.get("paymentTerms")>
		<#assign orderShipTermInfo = orderTermInfo.get("deliveryTerms")>
	</#if>
</#if>
	
<form id="CreateNewPriceChart"  action="<@ofbizUrl>CreateNewPriceChart</@ofbizUrl>" name="CreateNewPriceChart" method="post">

<div id="wizard-2">
    <h3>Price Chart Information</h3>
    <section>
      <fieldset>
            <table cellpadding="15" cellspacing="15" class='h3' width="50%" height="50%">
        	   	   <input type="hidden" name="param" id="param" value="${param?if_exists}"/>
        	   	   <input type="hidden" name="procPriceChartId" id="procPriceChartId" value="${procPriceChartId?if_exists}"/>
                   <tr>
						<td class="label">Location(<font color="red">*</font>) :</td>
						<td>
				            <@htmlTemplate.lookupField formName="CreateNewPriceChart" name="regionId" id="regionId" fieldFormName="ProcurementUnitLookupFacility" value="${regionId?if_exists}"/>
				        </td>
					</tr>
					
					<tr>
					    <td class="label">Product(<font color="red">*</font>) : </td>
					    <td>
			      			<select name="productId" id="productId">
			      				<option value='${productId?if_exists}' selected="select">${productId?if_exists}</option>
						        <#list productList as eachProduct>
							         <option value='${eachProduct.productId}'>  ${eachProduct.get("description")} </option>
						        </#list>
		      				</select>
			    		</td>
					</tr>
					<#if param?has_content>
					<tr>
					    <td class="label"><b>Date: </b></td>
					    <td valign='middle'>   
		            		<input class='h2' type="text" name="fromDate" id="fromDate" value="${fromDate?if_exists}" autocomplete="off" readonly="readonly"/>          
		          		</td>
					</tr>
					<#else>
						<tr>
					    <td class="label"><b>Date : </b></td>
					    <td valign='middle'>   
		            		<input class='h2' type="text" name="fromDate" id="fromDate" autocomplete="off"/>          
		          		</td>
					</tr>
					</#if>
					<tr>
						<td class="label"><b>Use Base SNF: </b></td>
						<td>
					      	<input type="text" name="useBaseSnf" id="useBaseSnf" size="30" maxlength="1" value="${useBaseSnf?if_exists}" class="alphaonly" autocomplete="off"/>
					    </td>
					</tr>
					<tr>
						<td class="label"><b>Use Base FAT </b></td>
						<td>
					      	<input type="text" name="useBaseFat" id="useBaseFat" size="30" maxlength="1" value="${useBaseFat?if_exists}" class="alphaonly" autocomplete="off"/>
					    </td>
					</tr>
					
					<tr>
						<td class="label"><b>Use Total Solids </b></td>
						<td>
					      	<input type="text" name="useTotalSolids" id="useTotalSolids" size="30" maxlength="1" value="${useTotalSolids?if_exists}" class="alphaonly" autocomplete="off"/>
					    </td>
					</tr>
					
					
					
                  </table>
                </fieldset>  
             </section>
		
		<#-- Working area-->
        <h3>Slab Based Information</h3>
        <section>
               <div class="full" style="width:100%;height:800px;">
				<div class="screenlet">
	    			<div class="screenlet-title-bar">
	 					<div class="grid-header" style="width:100%">
							<label>Slab Based Information</label>
						</div>
			 			<div class="screenlet-body" id="FieldsDIV" >
				        	<div class="screenlet-title-bar">
								<div id="myGrid1" style="width:43%;height:450px;">
									<div class="grid-header" style="width:50%">
									</div>
			             		</div>
							</div>
						</div>
					</div>
				</div>
			</div>
        </section>
         
           
       <h3>SNF Deductions</h3>
       <section>
               <div class="full" style="width:100%;height:500px;">
				<div class="screenlet">
	    			<div class="screenlet-title-bar">
	 					<div class="grid-header" style="width:100%">
							<label>SNF Deductions</label>
						</div>
			 			<div class="screenlet-body" id="FieldsDIV" >
				        	<div class="screenlet-title-bar">
								<div id="myGrid2" style="width:43%;height:450px;">
									<div class="grid-header" style="width:80%">
									</div>
			             		</div>
							</div>
						</div>
					</div>
				</div>
			</div>
        </section>
        <h3>FAT Deductions</h3>
        <section>
           <div class="full" style="width:100%;height:500px;">
				<div class="screenlet">
	    			<div class="screenlet-title-bar">
	 					<div class="grid-header" style="width:100%">
							<label>FAT Deductions</label>
						</div>
			 			<div class="screenlet-body" id="FieldsDIV" >
				        	<div class="screenlet-title-bar">
								<div id="myGrid3" style="width:43%;height:450px;">
									<div class="grid-header" style="width:80%">
									</div>
			             		</div>
							</div>
						</div>
					</div>
				</div>
			</div>
        </section>
    </form>
                
 <script type="application/javascript">
 
 $(document).ready(function(){
    
    
    	setupGrid2();
   
    
	
});  
	         
 </script>               
			