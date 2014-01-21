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
	var unitJson = ${StringUtil.wrapString(unitJson)}	


$(document).ready(function() {		
		  	
		$("input").keyup(function(e){
			  		
	  		if(e.target.name == "unitCode" ){
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

<div id="wrapper" style="width: 100%; height:100%">
	<div>
		<form method="post" name="AdjustmentsEntry" id="AdjustmentsEntry" action="<@ofbizUrl>populateProcurementPeriodBilling</@ofbizUrl>">     
      		<table width="35%" border="0" cellspacing="0" cellpadding="0">
      			<tr>
          			<td>&nbsp;</td>
          			<td align='right' valign='middle' nowrap="nowrap"><div class='h2'>shed Code :</div></td>
                    <#if shedId?has_content>
          				<td  align='left'> <div class='h2'>&#160;     ${shedName}</div></td>	
          				<input type="hidden" name="shedId" id="shedId" value="${shedId}">
                    <#else>
                    <td align='left'> 
			      		<select name="shedId" class='h2'onchange="javascript:setBillingShedUnitsDropDown(this);">
			      		<option value="">
			                <#list shedList as shed>    
			                  	<#assign isDefault = false>
								<option value='${shed.facilityId}'<#if isDefault> selected="selected"</#if>>
			                    	${shed.facilityName}
			                  	</option>                  	
			      			</#list>            
						</select>
					</td>
					</#if>
				</tr>  
	                </td>
           			<td>&nbsp;</td>
        		</tr>  
        		<tr>
        		    <td>&nbsp;</td>
        			<td align='right' valign='middle' nowrap="nowrap"><div class='h2'>Unit :</div></td>
        			<#if unitCode?has_content>
             			<td align='left'>
             				<h2>&#160;   ${unitCode}<span class="tooltip">${unitName}</span></h2>
             				<input type="hidden" size="6" maxlength="6" name="facilityId" id="unitId"  value="${unitId}" />
             			</td>
             		<#else>	
        			<td align='left'>
                      <select name="facilityId" class='h4'>
                		<#list unitsList as units>    
                  	    	<option value='${units}' >
	                    		${units}
	                  		</option>
                		</#list>             
					</select>				
        			</td>
        			</#if>
        		</tr>
        		<tr>
          			<td>&nbsp;</td>
          			<td align='right' valign='middle' nowrap="nowrap"><div class='h2'>Custom Time Period :</div></td>
                 	<td  align='left'> 
		      			<select name="customTimePeriodId" class='h2'>      			     			
			                <#list timePeriodList as timePeriod>
			                	<#if !timePeriodId?exists>    
				                  	<#assign isDefault = false>                  
			                    </#if>
			                    <#if timePeriodId?exists>
			                    	<#if timePeriodId == timePeriod.customTimePeriodId>
			      						<option  value="${timePeriodId}" selected="selected">${timePeriod.periodName}</option>
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
<!-- Submit Button !-->
      	<div align="center">
      	 	<table width="35%" border="0" cellspacing="0" cellpadding="0">
        		<tr>       
          			<td>&nbsp;</td>
          			<td valign='middle' nowrap="nowrap"><div class='h2'> &nbsp;</div></td>
          			<td align='right'> 
           				<div class='tabletext h2'>            
             				<input type="submit" class="smallSubmit" value="Generate">        	
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
