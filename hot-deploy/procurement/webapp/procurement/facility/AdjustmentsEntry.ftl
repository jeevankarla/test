<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<style type="text/css">
	.cell-title {
		font-weight: normal;
	}
	.cell-effort-driven {
		text-align: center;
	}
	
	.tooltip { /* tooltip style */
    background-color: #ffffbb;
    border: 0.1em solid #999999;
    color: #000000;
    font-style: arial;
    font-size: 80%;
    font-weight: normal;
    margin: 0.4em;
    padding: 0.1em;
}
.tooltipWarning { /* tooltipWarning style */
    background-color: #ffffff;
    border: 0.1em solid #FF0000;
    color: #FF0000;
    font-style: arial;
    font-size: 80%;
    font-weight: bold;
    margin: 0.4em;
    padding: 0.1em;
}	

.messageStr {
    background:#e5f7e3;
    background-position:7px 7px;
    border:4px solid #c5e1c8;
    font-weight:700;
    color:#005e20;    
    text-transform:uppercase;
}

</style>



<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/firebugx.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-1.4.3.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-ui-1.8.5.custom.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery.event.drag-2.0.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.core.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.editors.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangedecorator.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangeselector.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellselectionmodel.js</@ofbizContentUrl>"></script>		
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.groupitemmetadataprovider.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.dataview.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
	var shedJson = ${StringUtil.wrapString(shedJson)}	


$(document).ready(function() {		
		  	
		$("input").keyup(function(e){
	  		if(e.target.name == "unitCode" ){
	  			var unitJson = shedJson[$('[name=shedCode]').val()];
	  			var tempUnitJson = unitJson[e.target.value];
	  			
	  			  			
	  			if(tempUnitJson){
	  				$('span#unitToolTip').addClass("tooltip");
	  				$('span#unitToolTip').removeClass("tooltipWarning");
	  				unitName = tempUnitJson["name"];
	  				$('span#unitToolTip').html(unitName);
	  			}else{
	  				$('span#unitToolTip').removeClass("tooltip");
	  				$('span#unitToolTip').addClass("tooltipWarning");
	  				$('span#unitToolTip').html('Code not found');
	  			}	  			
	  		}
	  		
	  		if((e.target.name) == "centerCode"){
	  			var unitJson = shedJson[$('[name=shedCode]').val()];
	  			var tempCenterJson = (unitJson[ $('input[name=unitCode]').val() ] )["centers"];
	  			var centerName = tempCenterJson [$('input[name=centerCode]').val()];
	  						
	  			if(centerName){
	  				$('span#centerToolTip').removeClass("tooltipWarning");
	  				$('span#centerToolTip').addClass("tooltip");
	  				$('span#centerToolTip').html(centerName);
	  			}else{
	  				$('span#centerToolTip').removeClass("tooltip");
	  				$('span#centerToolTip').addClass("tooltipWarning");
	  				$('span#centerToolTip').html('Code not found');	  
	  			}				
	  		}
	  		
	}); 
	
	
		
	});

</script>

<div id="wrapper" style="width: 95%; height:100%">
	<div style="float: left;width: 30%; background:transparent;border: #F97103 solid 0.1em; valign:middle">
		<form method="post" name="AdjustmentsEntry" id="AdjustmentsEntry" action="<@ofbizUrl>${createAdjustmentTarget}</@ofbizUrl>">     
      		<table width="35%" border="0" cellspacing="0" cellpadding="0">
      			<tr>
          			<td>&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>shed Code:</div></td>
          			<#if shedId?has_content>
		          		<td  align='left'> <div class='h2'> ${shedName}</div></td>	
		          		<input type="hidden" name="shedCode" id="shedCode" value="${shedCode}">
		          	<#else>
	          			<td  align='left'> 
				      		<select name="shedCode" class='h2'>
				                <#list shedList as shed>    
				                  	<#assign isDefault = false>
									<option value='${shed.facilityCode}'<#if isDefault> selected="selected"</#if>>
				                    	${shed.facilityName}
				                  	</option>                  	
				      			</#list>            
							</select>
	          			</td>
	          		</#if>	
           			<td>&nbsp;</td>
        		</tr>  
        		<tr>
          			<td>&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Custom Time Period:</div></td>
          
		           	<td  align='left'> 
		      			<select name="customTimePeriodId" class='h2'>      			     			
			                <#list timePeriodList as timePeriod>
			                	<#if !timePeriodId?exists>    
				                  	<#assign isDefault = false>                  
			                    </#if>
			                    <#if timePeriodId?exists>
			                    	<#if timePeriodId == timePeriod.customTimePeriodId>
			      						<option  value="${timePeriodId}" selected="selected">${timePeriod.periodName}</option>
			      						<#else>
			      						<option value='${timePeriod.customTimePeriodId}'<#if isDefault> selected="selected"</#if>>
			                    			${timePeriod.periodName}
			      					</#if>	
			      					<#else>
			      						<option value='${timePeriod.customTimePeriodId}'<#if isDefault> selected="selected"</#if>>
			                    			${timePeriod.periodName}
			                  		</option>
			      				</#if>
			      			</#list>            
						</select>
		          	</td>
         			<td>&nbsp;</td>
         		</tr>   
		        <tr>
		        	<td>&nbsp;</td>
		        	<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.unitCode}:</div></td>
		          
		          	<td valign='middle' nowrap="nowrap">
		            	<div class='tabletext h2'>            
		             		<#if unitCode?has_content>
             					<h2>${unitCode}     <span class="tooltip">${unitName}</span></h2>
             					<input type="hidden" size="6" maxlength="6" name="unitCode" id="unitCode" autocomplete="off" value="${unitCode}" />
             				<#else>
		             			<input type="text" size="10" maxlength="15" name="unitCode" id="unitCode" value="${parameters.unitCode?if_exists}" autocomplete="off" required /><em>*</em><span class="tooltip" id ="unitToolTip">none</span>
		             		</#if>	              	   	
		           		</div>     
		          	</td>
		         	<td>&nbsp;</td>
		        </tr>
        		<tr>
          			<td>&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Adjustment Type:</div></td>          
          
          			<td  align='left'> 
      					<select name="adjustmentTypeId" class='h2'>
                			<#list adjustmentTypeList as adjustment>
                				<#if !adjustmentTypeId?exists>    
				                  	<#assign isDefault = false>                  
			                    </#if>
			                    <#if adjustmentTypeId?exists>
			                    	<#if adjustmentTypeId == adjustment.orderAdjustmentTypeId>
			      						<option  value="${adjustmentTypeId}" selected="selected">${adjustment.description}</option>
			      						<#else>
					      					<option value='${adjustment.orderAdjustmentTypeId}'<#if isDefault> selected="selected"</#if>>
		                    					${adjustment.description}
		                  					</option>
			      					</#if>	
			      						<#else>
				      					<option value='${adjustment.orderAdjustmentTypeId}'<#if isDefault> selected="selected"</#if>>
	                    					${adjustment.description}
	                  					</option>
			      				</#if>        				
									                  	
      						</#list>            
						</select>
          			</td>
         			<td>&nbsp;</td>
         		</tr>       
         		<tr>
          			<td>&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.centerCode}:</div></td>
          
          			<td valign='middle' nowrap="nowrap">
            			<div class='tabletext h2'>            
             				<input type="text" size="10" maxlength="15" name="centerCode" id="centerCode" autocomplete="off" required/><em>*</em><span class="tooltip" id ="centerToolTip">none</span>             	            	
            			</div>
          			</td>
        		</tr>        		
         		<tr>
          			<td>&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Amount:</div></td>          
          
          			<td valign='middle' nowrap="nowrap">
            			<div class='tabletext h2'>            
             				<input type="text" size="10" maxlength="15" name="amount" id="amount" autocomplete="off" required/>             	            	
            			</div>
          			</td>
        		</tr>          
      		</table>
      		<br/>
      <!-- Submit Button -->
      	<div align="center">
      	 	<table width="35%" border="0" cellspacing="0" cellpadding="0">
        		<tr>       
          			<td>&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'> &nbsp;</div></td>
          			<td valign='middle'> 
           				<div class='tabletext h2'>            
             				<input type="submit" class="smallSubmit" value="Add">        	
            			</div>
          			</td>
          			<td valign='middle'>          
          			</td>
         			<td>&nbsp;</td>
        		</tr>             
        		<tr><td><br/></td></tr>
      		</table> 
      	</div>	     
 	</form>
</div>
</div>



