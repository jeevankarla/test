
<input type="hidden" name="branchId" id="branchId" value="${branchId}">
<input type="hidden" name="partyId1" id="partyId1" value="${partyId}">
<input type="hidden" name="effectiveDate1" id="effectiveDate1" value="${effectiveDate}">


<script type="text/javascript">
	
	var branchProductSroreMap=${StringUtil.wrapString(branchProductSroreMap)!'[]'};
	
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
	
	  var branchId2 = $("#branchId2").val();
	
	
			var productStoreId = branchProductSroreMap[branchId2];
			
			
		      $("#partyId").autocomplete({ 
		      
		      		
		      		source: function( request, response ) {
	        			$.ajax({
	          					url: "LookupBranchCustomers",
	          					dataType: "html",
	          					data: {
	            					ajaxLookup: "Y",
	            					term : request.term,
	            					productStoreId : productStoreId
	          					},
	          					success: function( data ) {
	          						var dom = $(data);
	        						dom.filter('script').each(function(){
	            						$.globalEval(this.text || this.textContent || this.innerHTML || '');
	        						});
	            					response($.map(autocomp, function(v,i){
	    								return {
	                						label: v.label,
	                						value: v.value
	               						};
									}));
									
	          					}
	        			});
	        			
	      			},
	      			select: function(e, ui) {
			        	//$('#partyTooltip').val('<label>+ui.item.label+</label>');
			        	
			        	$("#partyTooltip").html("<font size=5>"+ui.item.label+"</font>");
			        	
			        	 $("p label").hover(function(){
							           $(this).animate({fontSize: "15px"}, 300)
							          }, function() {
							         $(this).animate({fontSize: "10"}, 300)  
							          })
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


        
      <form method="post" name="QuotaDashboard" id="QuotaDashboard" action="<@ofbizUrl>QuotaDashboard</@ofbizUrl> " class="basic-form">
        
		  <table width="60%" border="0" cellspacing="0" cellpadding="0" class="form-style-8">
				<tr>
				<td width="40%">
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
				
				<tr><td><br/></td></tr>
				
				<tr>
				  <td align='left' valign='middle' nowrap="nowrap">Customer Name:</td>
				  <td valign='middle'><font color="green">          
				     <input type='text' id='partyId' name='partyId' placeholder="Enter Customer Name" onfocus='javascript:autoCompletePartyId();' size='20'/><p><label  align="left" id="partyTooltip" style="color: blue"></label><p>  		
				  </td>
				  <td><br/></td>
				</tr>
			 
			   <tr><td><br/></td></tr>
			   
			   <tr>
				  <td align='left' valign='middle' nowrap="nowrap">Month :</td>
				  <td valign='middle'><font color="green">          
				    <input  type="text" size="20" id="effectiveDate" readonly  name="effectiveDate" onmouseover='monthPicker()' class="monthPicker" /> 		
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
 	

 