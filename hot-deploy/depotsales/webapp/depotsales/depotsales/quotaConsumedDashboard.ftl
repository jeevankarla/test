
<input type="hidden" name="branchId" id="branchId" value="${branchId}">
<input type="hidden" name="partyId1" id="partyId1" value="${partyId}">

<script type="text/javascript">
	
	var branchProductSroreMap=${StringUtil.wrapString(branchProductSroreMap)!'[]'};
	
$(document).ready(function(){

var branchId = $("#branchId").val();
var partyId = $("#partyId1").val();
  var branchList = "";
  <#list formatList as eachList>
	branchList = branchList + "<option value='${eachList.payToPartyId}' >${eachList.productStoreName}</option>";
  </#list>

     $("#branchId2").html(branchList);
     
     
     $("#branchId2").val(branchId);
     $("#partyId").val(partyId);
     
		
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


        
      <form method="post" name="utilizedQuotaDashboard" id="utilizedQuotaDashboard" action="<@ofbizUrl>utilizedQuotaDashboard</@ofbizUrl> " class="basic-form">
        
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
				  <td align='left' valign='middle' nowrap="nowrap">Customer :</td>
				  <td valign='middle'><font color="green">          
				     <input type='text' id='partyId' name='partyId' onfocus='javascript:autoCompletePartyId();' size='13'/><p><label  align="left" id="partyTooltip" style="color: blue"></label><p>  		
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
 	

 