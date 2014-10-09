
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
			
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoNumeric/autoNumeric-1.6.2.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoTab/jquery.autotab-1.1b.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
}

	$(document).ready(function() {	
		makeDatePicker("findFromDate","thruDate");
		makeDatePicker("findThruDate","thruDate");
	
		var mccCodeJson = ${StringUtil.wrapString(mccCodeJson)}
		$("input").keyup(function(e){
		  		if(e.target.name == "mccCode" ){
					var tempUnitJson = mccCodeJson[$('[name=mccCode]').val()];
		  			if(tempUnitJson){
		  				$('span#unitToolTip').addClass("tooltip");
		  				$('span#unitToolTip').removeClass("tooltipWarning");
		  				unitName = tempUnitJson["name"];
		  				unitId = tempUnitJson["facilityId"];
		  				$('span#unitToolTip').html(unitName);
		  				$('[name=facilityId]').val(unitId);
		  			}else{
		  				$('[name=facilityId]').val('');
		  				$('span#unitToolTip').removeClass("tooltip");
		  				$('span#unitToolTip').addClass("tooltipWarning");
		  				$('span#unitToolTip').html('Code not found');
		  			}	  			
		  		}
		}); 
		
	});

function deleteTransferEntry(thisValue,milkTransferId){	
	var confirmationFlag=false;
	if(confirm('Dou u want to delete this Record?')){	
		confirmationFlag=true;
	}else{
		confirmationFlag=false;
	}	
	if(confirmationFlag){		
			if((milkTransferId !='')){	
				var action = "deleteTransferEntryAjax";
				var dataJson = {"milkTransferId":milkTransferId,													
							   };
				$.ajax({
					 type: "POST",
		             url: action,
		             data: dataJson,
		             dataType: 'json',	            
					success:function(result){
						if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){						
						}else{						
							alert("Sucessfully deleted");						
							$(thisValue).parent().parent().hide();	
						}								 
					},
					error: function(){
						
					}							
				});
			}	
			$(thisValue).parent().parent().hide();		
		}else{
			alert('cancelled');
			return false;
		}	
}

</script>
<div class="screenlet">
	 <div class="screenlet-title-bar">
      <h3>Find Milk Receipts</h3>
    </div>
    <div class="screenlet-body">
 		<form method="post" name="findMilkReceipts" id="findMilkReceipts" action="<@ofbizUrl>FindMilkReceipt</@ofbizUrl>">  
      		<table width="60%" border="0" cellspacing="0" cellpadding="0">     
        	<tr>
			  <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap">
	          	<table>
	          		<tr>
	          			<table>
	          				<tr>
	        					<td><span class='h3'>Record No: </span></td><td><input  size="12" type="text" id="milkTrnsfId" name="milkTransferId"/></td>
	        				</tr>
                         	<tr>
                         		<td><span class='h3'>Mcc Code:</span></td><td>
                         		<input type="hidden" size="6" id="facilityId" maxlength="6" name="facilityId"  autocomplete="off" value="" />
                         		<input type="text" size="6" maxlength="6" name="mccCode" id="mccCode" autocomplete="off"/><span class="tooltip" id ="unitToolTip">none</span></td>
                   			</tr>
        					<tr>	        	
					        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Milk Type</td><td>
						        	<select name="productId" class='h4' >
						        	<option value=''></option>
			                		<#list productsList as product>    
			                  	    	<option value='${product.productId}' >
				                    		${product.description?if_exists}
				                  		</option>
			                		</#list>            
								</select>
								</td>
        					</tr> 
	        				<tr>
	        					<td><span class='h3'>From Date: </span></td><td><input  size="12" type="text" id="findFromDate" name="fromDate"/></td>
	        				</tr>
	        				<tr>	
                         		<td><span class='h3'>Thru Date: </span></td><td><input size="12" type="text" id="findThruDate" name="thruDate"/></td>
	        				</tr>
						    <tr>
						      	<td><span class='h3'>userLogin </span></td><td><input  name="createdByUserLogin" size="15" type="text" /></td>
						    </tr>	  
						    <tr><td align='right'><span class='h2'><input type="submit"  size="10" value="Find" class="buttontext h1"/></span> </td></tr>      					 
                      	</table>
	          		</tr>
	          	</table>
	         </td>  
	       </tr>
      	</table>
	</form>
   </div>
</div>  