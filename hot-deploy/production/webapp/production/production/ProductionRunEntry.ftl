<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />

<style type="text/css">
#customTable {
	color:#333333;
	border-width: 0.1px;
	border-color: #999999;
	border-collapse: collapse;
	border-spacing:10px 50px
}

.myButton {
	border-radius:3px;
	border:1px solid #BAD0EE;
	display:inline-block;
	cursor:pointer;
	color:#ffffff;
	font-family:arial;
	font-size:10px;
	padding:4px 10px;
	text-decoration:none;
	width: 70px;
}
.myButton:hover {
	background-color:#BAD0EE;
}

</style>


<script type="text/javascript">
	
	function datetimepick(){
		
		var currentTime = new Date();
	 	// First Date Of the month 
	 	var startDateFrom = new Date(currentTime.getFullYear(),currentTime.getMonth(),1);
	 	// Last Date Of the Month 
	 	var startDateTo = new Date(currentTime.getFullYear(),currentTime.getMonth() +1,0);
	  
		 $("#startDate").datetimepicker({
			dateFormat:'dd-mm-yy',
			changeMonth: true,
		    minDate: startDateFrom,
		   // maxDate: startDateTo
		 });	
				
		$('#ui-datepicker-div').css('clip', 'auto');
		
		//$('#startDate').val('2015-04-21 10:50:20');	
	}
	
	$(document).ready(function(){
		$('#addMaterialDiv').hide();
		$('#declareTaskOutDiv').hide();
		
	});
		
</script>
<#include "ProductionRunEntryInc.ftl"/>
<#if productionRunId?has_content><h1> [Id: ${productionRunId}]</h1></#if>
<div class="full">
	<div class="screenlet">
			<div class="screenlet-title-bar">
         		<div class="grid-header" style="width:100%">
					<center><label>Production Header</label><center>
				</div>
		     </div>
		     
		     <div class="screenlet-body">
		     	<form method="post" name="productionRunEntryInit" action="<@ofbizUrl>processProductionRun</@ofbizUrl>" id="productionRunEntryInit">  
			    	<table width="100%" border="0" cellspacing="0" cellpadding="0">
				        
				        <tr>
				        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Production Name:</div></td>
					      	<#if productionRunData?exists && productionRunData.workEffortName?has_content>
			      				<input type="hidden" name="workEffortName" id="workEffortName" value="${productionRunData.workEffortName}">
				      			<td>
				      				<div class='tabletext h3'>${productionRun.workEffortName?if_exists}</div>
				      			</td>
				      		<#else>
				      			<td><input type="text" name="workEffortName" id="workEffortName" ></td>
					      	</#if>
					      	
					      	<td> &nbsp;</td>
					      	
					      	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Store:</div></td>
					      	<#if productionRunData?exists &&  productionRunData.facilityId?has_content>
			      				<input type="hidden" name="facilityId" id="facilityId" value="${productionRunData.facilityId}">
				      			<td>
				      				<div class='tabletext h3'>${productionRun.facilityId}</div>
				      			</td>
				      		<#else>
				      			<td><input type="text" name="facilityId" id="facilityId" ></td>
					      	</#if>
					      	
					      	<td> &nbsp;</td>
					      	
					      	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Routing:</div></td>
					      	<#if routingId?has_content>
			      				<input type="hidden" name="routingId" id="routingId" value="${routingId}">
				      			<td>
				      				<div class='tabletext h3'>${routingId?if_exists} [${routingId}]</div>
				      			</td>
				      		<#else>
				      			<td><@htmlTemplate.lookupField value="${productId?if_exists}" formName="productionRunEntryInit" name="routingId" id="routingId" fieldFormName="LookupRouting"/></td>
					      	</#if>
					    </tr>
					    <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
					    <tr>
				        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Product:</div></td>
					      	<#if productionRunData?exists &&  productionRunData.productId?has_content>
			      				<input type="hidden" name="productId" id="productId" value="${productionRunData.productId}">
				      			<td>
				      				<div class='tabletext h3'>${productionRunData.productId}</div>
				      			</td>
				      		<#else>
				      			<td><@htmlTemplate.lookupField value="${productId?if_exists}" formName="productionRunEntryInit" name="productId" id="productId" fieldFormName="LookupProduct"/></td>
					      	</#if>
					      	
					      	<td> &nbsp;</td>
					      	
					      	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Qty to Produce:</div></td>
					      	<#if productionRunData?exists &&  productionRunData.quantity?has_content>
			      				<input type="hidden" name="pRQuantity" id="pRQuantity" value="${productionRunData.quantity}">
				      			<td>
				      				<div class='tabletext h3'>${productionRunData.quantity?if_exists}</div>
				      			</td>
				      		<#else>
				      			<td><input type="text" name="pRQuantity" id="pRQuantity" ></td>
					      	</#if>
					      	
					      	<td> &nbsp;</td>
					      	
					      	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Start Date:</div></td>
					      	<#if productionRunData?exists &&  productionRunData.estimatedStartDate?has_content>
			      				<input type="hidden" name="startDate" id="startDate" value="${productionRunData.estimatedStartDate}">
				      			<td>
				      				<div class='tabletext h3'>${productionRunData.estimatedStartDate?if_exists}</div>
				      			</td>
				      		<#else>
				      			<td><input class='h3' type="text" name="startDate" id="startDate" onmouseover='datetimepick()' readOnly/></td>
					      	</#if>
					      	
					      	<#if productionRunId?exists>
					      		<td> &nbsp;</td>
						      	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Status:</div></td>
					      		<#if productionRunData.currentStatusId?has_content>
				      				<td>
				      					<div class='tabletext h3'>${productionRunData.currentStatusId?if_exists}</div>
				      				</td>
				      			</#if>
				      		</#if>
					      	
					    </tr>
					    <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
	    			</table>
	    			<#if !productionRunId?exists>
		    			<div align="center">
				    		<input type="submit" style="padding:.5em" id="submit" value="Create Run" onclick="javascript:processProductionHeader('productionRunEntryInit');"/>
				    	</div>
			    	</#if>
				</form>
				<#if productionRunId?exists && productionRunData.currentStatusId == "PRUN_CREATED">
	    			<div align="center">
	    				<form id = "tempProcessForm' name = "tempProcessForm" method="post" action = "<@ofbizUrl>confirmProductionRunStatus</@ofbizUrl>">
							<input type="hidden" name="productionRunId" value='${productionRunId?if_exists}'>
							<input type="submit" style="padding:.5em" id="confirmSubmit" value="Confirm Run"/>
						</form>
			    	</div>
		    	</#if>
		    	<#if productionRunId?exists && productionRunData.currentStatusId != "PRUN_CREATED">
	    			<div align="center">
	    				<a class="buttontext" href="<@ofbizUrl>PrintProductionRun?productionRunId=${productionRunId?if_exists}</@ofbizUrl>" target="_blank"> Print </a>
			    	</div>
		    	</#if>
			</div>
		</div>		
</div>
<#if productionRunId?has_content && productionRunData.currentStatusId != "PRUN_CREATED">
	<div class="full">
		<div class="screenlet">
	    	<div class="screenlet-body">
				<div class="grid-header" style="width:100%">
			 		<label>Routing Tasks</label>
				</div>
				<table width="100%" cellspacing="15" cellpadding="15" id="customTable">
								        
					<tr class='h2' style="border:1pt solid black;width:100%">
			        	<td style='float: left;width:5%;' >Sl.</td>
			        	<td style='float: left;width:35%;'>Name</td>
			        	<td style='float: left;width:20%;'>Machine</td>
			        	<td style='float: left;width:15%;'>Status</td>
			        	<td style='float: center;width:15%;'>Start</td>
			        	<td style='float: center;width:10%;'>Add Material</td>
			        	<td style='float: center;width:10%;'>Declare</td>
			        	<td style='float: center;width:10%;'>Finish</td>
			        </tr>
			        <#assign sl=1>
			        <#assign allow = "yes">
			        <#list productionRunRoutingTasks as eachTask>
			        	<#assign statusId = eachTask.currentStatusId>
			        	<#if (statusId == 'PRUN_DOC_PRINTED' || statusId == 'PRUN_RUNNING') && allow == "yes">
					        <tr class='h3' 
					        	<#if statusId == 'PRUN_DOC_PRINTED'>
					        		style="border-bottom:1pt dotted black;background-color:#FAF998; line-height: 25px;">
					        	<#else>
					        		style="border-bottom:1pt dotted black;background-color:#7FFFC7; line-height: 25px;">
					        	</#if>
					        	
					        	<td style='float: left;width:5%;'> ${sl?if_exists}</td>
					        	<td style='float: left;width:35%;'> ${eachTask.workEffortName?if_exists}</td>
						      	<td style='float: left;width:20%;'> ${eachTask.fixedAssetId?if_exists}</td>
						      	<td style='float: left;width:15%;'> ${eachTask.currentStatusId?if_exists}</td>
						      	<#if statusId == "PRUN_RUNNING">
						      		<td style='width:15%;'>&nbsp;</td>
						      		<td style='float: center;width:10%;padding: 5px 5px 5px 5px;'> <input class="myButton" type="button" name="issueMaterial" id="issueMaterialBtn" value="Add" onclick="javascript: addMaterial('${eachTask.workEffortId}')"/></td>
						      		<td style='float: center;width:10%;padding: 5px 5px 5px 5px;'> <input class="myButton" type="button" name="declareTask" id="declareTask" value="Declare" onclick="javascript: declareTaskOut('${eachTask.workEffortId}')""/></td>
						      		<td style='float: center;width:10%;padding: 5px 5px 5px 5px;'> 
						      			<form name="taskFinishForm" id="taskFinishForm" method="post" action="changeRoutingTaskStatus">
						      				<input type="hidden" name="workEffortId" value="${eachTask.workEffortId?if_exists}"> 
						      				<input type="hidden" name="productionRunId" value="${productionRunId?if_exists}">
						      				<input type="hidden" name="statusId" value="PRUN_COMPLETED">
						      				<input class="myButton" type="submit" name="completeTask" id="completeTask" value="Finish" />
						      			</form>	
						      		</td>
						      	<#else>
						      		<td style='float: center;width:15%;padding: 5px 5px 5px 5px;'>
						      			<form name="taskStartForm" id="taskStartForm" method="post" action="changeRoutingTaskStatus">
						      				<input type="hidden" name="workEffortId" value="${eachTask.workEffortId?if_exists}">
						      				<input type="hidden" name="productionRunId" value="${productionRunId?if_exists}"> 
						      				<input type="hidden" name="statusId" value="PRUN_RUNNING">
						      				<input class="myButton" type="submit" name="startTaskBtn" id="startTaskBtn" value="Start"/></td>
						      			</form>
						      		<td style='width:10%;'>&nbsp;</td>
						      		<td style='width:10%;'>&nbsp;</td>
						      		<td style='width:10%;'>&nbsp;</td>
						      	</#if>
					    	</tr>
					    	<#assign allow = "no">
					    <#else>
					    	<tr class='h3'
					    		<#if statusId ==  "PRUN_COMPLETED">
					        		style="border-bottom:1pt dotted black;background-color:#ECECEC; line-height: 25px;">
					        	<#else>
					        		style="border-bottom:1pt dotted black; line-height: 25px;">
					        	</#if>
					        	<td style='float: left;width:5%;'> ${sl?if_exists}</td>
					        	<td style='float: left;width:30%;'> ${eachTask.workEffortName?if_exists}</td>
						      	<td style='float: left;width:25%;'> ${eachTask.fixedAssetId?if_exists}</td>
						      	<td style='float: left;width:10%;'>${eachTask.currentStatusId?if_exists}</td>
						      	<td style='width:10%;'>&nbsp;</td>
						      	<td style='width:10%;'>&nbsp;</td>
						      	<td style='width:10%;'>&nbsp;</td>
						      	<td style='width:10%;'>&nbsp;</td>
					    	</tr>
				    	</#if>
				    	<#assign sl = sl+1>
				    </#list>
		    	</table>
			</div>
		</div>
	</div>
	
	<div class='clear'>&nbsp;</div>
	<div class='full' id='addMaterialDiv'>
		<div class="lefthalf">
			<div class="screenlet">
				<div class="screenlet-title-bar">
	         		<div class="grid-header" style="width:100%">
						<label>Material Required</label>
					</div>
			     </div>
	      
	    		<div class="screenlet-body">
					<div id="myGrid1" style="width:100%;height:120px;"></div>
					<div id='grid1Msg' style="font-color:green;"></div>
					<div align="center">
		    			<input type="submit" style="padding:.3em" id="issueMaterialSave" value="Submit" onclick="javascript:processIssueComponentEntry();"/>
		    		</div>
	    		</div>
			</div>
		</div>
	</div>
	<div class='clear'>&nbsp;</div>
	<div class="full" id='declareTaskOutDiv'>
		<div class="screenlet-title-bar">
	 		<div class="grid-header" style="width:100%">
				<center><label>Production Run Declaration</label></center>
			</div>
		</div>
		<div class="lefthalf" id='declareTaskOutDiv'>
			<div class="screenlet">
				<div class="screenlet-title-bar">
	         		<div class="grid-header" style="width:100%">
						<label>Deliverable</label>
					</div>
			     </div>
	    		<div class="screenlet-body">
					<div id="myGrid2" style="width:100%;height:120px;"></div>
					<div align="center">
		    			<input type="submit" style="padding:.3em" id="declareSave" value="Submit" onclick="javascript:processDeclareComponentEntry();"/>
		    		</div>
	    		</div>
			</div>
		</div>
		<div class="righthalf" id='returnMaterialDiv'>
			<div class="screenlet">
	    		<div class="screenlet-body">
			 		<div class="grid-header" style="width:100%">
			 			<label>Return Unused Materials To Warehouse</label>
					</div>
					<div id="myGrid3" style="width:100%;height:120px;"></div>
					<div align="center">
		    			<input type="submit" style="padding:.3em" id="returnMaterialSave" value="Submit" onclick="javascript:processReturnComponentEntry();"/>
		    		</div>
				</div>
			</div>
		</div>
	</div>
</#if>

<script type="application/javascript">
   
	  function processProductionHeader(form){
			var dateFormat = $('[name="startDate"]').val();
			var dateArr = dateFormat.split(' ');
			var dateStr = dateArr[0];
			var timeStr = dateArr[1];
			var dateTokens = dateStr.split('-');
			var dateTimestamp = dateTokens[2]+'-'+dateTokens[1]+'-'+dateTokens[0]+' '+timeStr+':00';
			if($('#startDate').val()){
				$('#startDate').val(dateTimestamp);
			}
      }
      
      function addMaterial(effortId){
      		$('#declareTaskOutDiv').hide();
	  		$('#addMaterialDiv').show();
	  		
	  		var action = "getRoutingTaskNeededMaterial";
			var dataJson = {"workEffortId":effortId};
			$.ajax({
				 type: "POST",
	             url: action,
	             data: dataJson,
	             dataType: 'json',
	             async: false,
				success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
						msg = result["_ERROR_MESSAGE_"];
						if(result["_ERROR_MESSAGE_LIST_"] =! undefined){
							msg =msg+result["_ERROR_MESSAGE_LIST_"] ;
						}
					}else{
						var issueProductJSON = result['issueProductItemsJSON'];
						var displayButton = result['displayButton'];
						if(displayButton && displayButton == 'N'){
							$("#issueMaterialSave").hide();
							$("#issueMaterialBtn").hide();
							$("#addMaterialDiv").show();
						}
						prepareIssueGrid(issueProductJSON, effortId, displayButton);
					}					
				},
				error: function (xhr, textStatus, thrownError){
					alert("record not found :: Error code:-  "+xhr.status);
				}							
			});
      }
      
      function declareTaskOut(effortId){
	  		addMaterial(effortId);
	  		$('#declareTaskOutDiv').show();
  		
	  		var action = "getRoutingTaskDeclarableMaterial";
			var dataJson = {"workEffortId":effortId, "productionRunId": '${productionRunId?if_exists}'};
			$.ajax({
				 type: "POST",
	             url: action,
	             data: dataJson,
	             dataType: 'json',
	             async: false,
				success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
						msg = result["_ERROR_MESSAGE_"];
						if(result["_ERROR_MESSAGE_LIST_"] =! undefined){
							msg =msg+result["_ERROR_MESSAGE_LIST_"] ;
						}
					}else{
						var returnProductJSON = result['returnProductItemsJSON'];
						var returnBtn = result['returnDisplayButton'];
						if(returnBtn && returnBtn == 'N'){
							$("#returnMaterialSave").hide();
						}
						prepareReturnGrid(returnProductJSON, effortId, returnBtn);
						var declareProductJSON = result['declareProductItemsJSON'];
						var declareBtn = result['declareDisplayButton'];
						if(declareBtn && declareBtn == 'N'){
							$("#declareSave").hide();
						}
						prepareDeclareGrid(declareProductJSON, effortId, declareBtn);
						
						if(returnBtn == "N" && declareBtn=="N"){
							$("#declareTask").hide();
						}
						
					}					
				},
				error: function (xhr, textStatus, thrownError){
					alert("record not found :: Error code:-  "+xhr.status);
				}							
			});
      }
      
</script>

