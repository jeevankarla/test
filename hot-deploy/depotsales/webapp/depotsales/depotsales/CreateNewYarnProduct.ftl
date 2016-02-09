<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />


<style type="text/css">
 .labelFontCSS {
    font-size: 13px;
}

</style>

<script type="text/javascript">



	$(document).ready(function(){
	
		jQuery("input[name='ply']").parent().parent().hide();
		jQuery("input[name='count']").parent().parent().hide();
	});
	


	function getChildCategories() {
		var productCategoryId = $('#productCategoryId').val();
		
		if((productCategoryId)){

			$.ajax({
				 type: "POST",
	             url: 'getChildCategoriesAjax',
	             data: {primaryParentCategoryId : productCategoryId},
	             dataType: 'json',
		            
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	                    alert('Error Fetching available types');
					}else{
						childCategoriesList = result["childCategoriesList"];
						var paramName = 'childProductCategoryId';
						var optionList = '';   		
						var list= childCategoriesList;
						if (list) {	
							optionList += "<option value=''></option>";		       				        	
				        	for(var i=0 ; i<list.length ; i++){
								var innerList=list[i];	              			             
				                optionList += "<option value = " + innerList.productCategoryId + " >" + innerList.description + "</option>";          			
				      		}
				      	}		
				      	if(paramName){
				      		jQuery("[name='"+paramName+"']").html(optionList);
				      	}
				      	
					}								 
				},
				error: function(){
					alert("record not found");
				}							
			});
			
		}		
	}
	
	function getSubChildCategories() {
		var childProductCategoryId = $('#childProductCategoryId').val();
		
		if((childProductCategoryId)){

			$.ajax({
				 type: "POST",
	             url: 'getChildCategoriesAjax',
	             data: {primaryParentCategoryId : childProductCategoryId},
	             dataType: 'json',
		            
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	                    alert('Error Fetching available types');
					}else{
						childCategoriesList = result["childCategoriesList"];
						var paramName = 'subChildProductCategoryId';
						var optionList = '';   		
						var list= childCategoriesList;
						if (list) {	
							optionList += "<option value=''></option>";		       				        	
				        	for(var i=0 ; i<list.length ; i++){
								var innerList=list[i];	              			             
				                optionList += "<option value = " + innerList.productCategoryId + " >" + innerList.description + "</option>";          			
				      		}
				      	}		
				      	if(paramName){
				      		jQuery("[name='"+paramName+"']").html(optionList);
				      	}
				      	
					}								 
				},
				error: function(){
					alert("record not found");
				}							
			});
			
		}		
	}
	
	
	function getAttributeTypes() {
		var subChildProductCategoryId = $('#subChildProductCategoryId').val();
		alert("childProduct = "+subChildProductCategoryId);
		if((subChildProductCategoryId)){

			$.ajax({
				 type: "POST",
	             url: 'getAttributeTypesAjax',
	             data: {productCategoryId : subChildProductCategoryId},
	             dataType: 'json',
		            
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	                    alert('Error Fetching available types');
					}else{
						
						productAttributesMap = result["productAttributesMap"];
						productAttributeTypesList = result["productAttributeTypesList"];
						if (productAttributeTypesList) {
						
							for(var i=0 ; i<productAttributeTypesList.length ; i++){
								
								var attributeType = productAttributeTypesList[i];
								var tableElement = '<tr><td align="right" class="label">'+attributeType+':</td>';
								var tableElement = tableElement + '<td width="10%">';	
								
								if((attributeType == "PLY") || (attributeType == "COUNT") ){
									tableElement = tableElement + '<input type="text" name="'+attributeType+'" id="'+attributeType+'" size="30" maxlength="60" autocomplete="off" />';
								}
								else{
									var tableElement = tableElement + '<select name="'+attributeType+'" id="'+attributeType+'"> ';
									
									var attributesList = productAttributesMap[attributeType];
									for(var j=0 ; j<attributesList.length ; j++){
										var attribute = attributesList[j];
										tableElement = tableElement + '<option value = " + attribute.attrName + " >'+ attribute.attrName+'</option>';
									}
									tableElement = tableElement + '</select>';	
								}
								
								tableElement = tableElement + '</td>';		
								tableElement = tableElement + '</tr>';	
										 
								$('#features tr:last').after(tableElement);	
							
							}
							
						}
						$('#features tr:last').after('<tr><td></td><td><input type="submit" class="smallSubmit" value="Submit"/></td></tr>');
						
					}								 
				},
				error: function(){
					alert("record not found");
				}							
			});
			
		}		
	}
	
	
	
	
	


 
</script>

<div class="full">
      
    <div class="screenlet-body">
    	<form method="post" name="CreateNewProduct" action="<@ofbizUrl>createNewProduct</@ofbizUrl>" id="CreateNewProduct">  
	
      		<table id="features" name="features" width="100%" border="0" cellspacing="0" cellpadding="0">
        		 <tr>
			         <td align='right' class='label'>Type:</td>
			         <td valign='middle'>
	             		<select name="productCategoryId" id="productCategoryId" onchange="getChildCategories()">
			          		<option value=''></option>
			                <#list productCategoryList as productCategory>    
								<option value='${productCategory.productCategoryId}'>${productCategory.description}</option>                  	
			      			</#list>            
						</select>	
			         </td>
			      </tr>
			      
			      <tr>
			         <td align='right' class='label'>Child Type:</td>
			         <td valign='middle'>
	             		<select name="childProductCategoryId" id="childProductCategoryId" onchange="getSubChildCategories()">
						</select>	
			         </td>
			      </tr> 
			      
			      <tr>
			         <td align='right' class='label'>Sub Child Type:</td>
			         <td valign='middle'>
	             		<select name="subChildProductCategoryId" id="subChildProductCategoryId" onchange="getAttributeTypes()">
						</select>	
			         </td>
			      </tr>
			      
			      <tr>
			       	 <td>
			       	 	&nbsp;
			       	 </td>
			      </tr>
			      <tr>
			       	 <td>
			       	 	<FONT COLOR="#04B431"><b>
			       	 		Product Features
			       	 	</FONT>
			       	 </td>
			      </tr>
			      
			      
			      
			     
					
					
					<#--
					<tr>
				      <td></td>
				      <td><input type="submit" class="smallSubmit" value="Submit"/></td>
				    </tr>
			      	-->
								      
      		</table>
     
		</form>

	 </div> 
    
</div>
