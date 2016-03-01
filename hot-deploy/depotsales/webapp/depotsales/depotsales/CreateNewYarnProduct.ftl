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
		
		getChildCategories();
		getSubChildCategories();
		getAttributeTypes();
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
						$('.schemeCategory').remove();
					
						childCategoriesList = result["childCategoriesList"];
						productCatAssocMap = result["productCatAssocMap"];
						productCategoryAssocTypeList = result["productCategoryAssocTypeList"];
						
						
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
				      	
				      	if (productCategoryAssocTypeList) {
							$('#features tr:last').after('<tr class="schemeCategory"><td><FONT COLOR="#04B431"><b>Scheme Applicability</FONT></td></tr>');
							for(var i=0 ; i<productCategoryAssocTypeList.length ; i++){
								var prodCatAssocType = productCategoryAssocTypeList[i];
								var tableElement = '<tr class="schemeCategory"><td align="right" class="label">'+prodCatAssocType.description+':</td>';
								var tableElement = tableElement + '<td width="10%">';	
								
								var tableElement = tableElement + '<select name="'+prodCatAssocType.productCategoryAssocTypeId+'" id="'+prodCatAssocType.productCategoryAssocTypeId+'"> ';
								
								tableElement = tableElement + '<option value = "" ></option>';
								var prodCatAssocList = productCatAssocMap[prodCatAssocType.productCategoryAssocTypeId];
								for(var j=0 ; j<prodCatAssocList.length ; j++){
									var prodCatAssoc = prodCatAssocList[j];
									tableElement = tableElement + '<option value = "' + prodCatAssoc.productCategoryIdTo + '" >'+ prodCatAssoc.productCategoryIdTo+'</option>';
								}
								tableElement = tableElement + '</select>';	
								
								
								tableElement = tableElement + '</td>';		
								tableElement = tableElement + '</tr>';	
								
								if(i % 3 === 0){
									tableElement = tableElement + '<tr class="productAttributes"><td>&nbsp;</td></tr>';	
								}  
										 
								$('#features tr:last').after(tableElement);	
								//$('#categories').append(tableElement);
							}
							
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
		var childProductCategoryId = $('#childProductCategoryId').val();
		if((childProductCategoryId)){
			$('.productAttributes').remove();
			$.ajax({
				 type: "POST",
	             url: 'getAttributeTypesAjax',
	             data: {productCategoryId : childProductCategoryId},
	             dataType: 'json',
		            
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	                    alert('Error Fetching available types');
					}else{
						
						productAttributesMap = result["productAttributesMap"];
						productAttributeTypesList = result["productAttributeTypesList"];
						productCategoryAttributeTypesList = result["productCategoryAttributeTypesList"];
						if (productCategoryAttributeTypesList) {
							
							$('#features tr:last').after('<tr class="productAttributes"><td><FONT COLOR="#04B431"><b>Product Features</FONT></td></tr>');	
							
							for(var i=0 ; i<productCategoryAttributeTypesList.length ; i++){
								
								var attributeType = productCategoryAttributeTypesList[i];
								var tableElement = '<tr class="productAttributes"><td align="right" class="label">'+attributeType.description+':</td>';
								var tableElement = tableElement + '<td width="10%">';	
								
								if((attributeType.inputType == "Indicator")){
									var attributesList = productAttributesMap[attributeType.attrTypeId];
									var tableElement = tableElement + '<select name="'+attributeType.attrTypeId+'" id="'+attributeType.attrTypeId+'"> ';
									tableElement = tableElement + '<option value = "N" >N</option>';
									tableElement = tableElement + '<option value = "'+attributesList[0].attrCode+'" >Y</option>';
									tableElement = tableElement + '</select>';	
								}
								
								if((attributeType.inputType == "Number")){
									if((attributeType.attributeApplType == "Mandatory")){
										tableElement = tableElement + '<input type="text" name="'+attributeType.attrTypeId+'" id="'+attributeType.attrTypeId+'" size="30" maxlength="60" autocomplete="off" required/>';
									}
									else{
										tableElement = tableElement + '<input type="text" name="'+attributeType.attrTypeId+'" id="'+attributeType.attrTypeId+'" size="30" maxlength="60" autocomplete="off" />';
									}
								}
								
								if((attributeType.inputType == "Select")){
									if((attributeType.attributeApplType == "Mandatory")){
										var tableElement = tableElement + '<select name="'+attributeType.attrTypeId+'" id="'+attributeType.attrTypeId+'"> ';
									
										var attributesList = productAttributesMap[attributeType.attrTypeId];
										for(var j=0 ; j<attributesList.length ; j++){
											var attribute = attributesList[j];
											tableElement = tableElement + '<option value = "' + attribute.attrCode + '" >'+ attribute.attrName+'</option>';
										}
										tableElement = tableElement + '</select>';	
									}
									else{
										var tableElement = tableElement + '<select name="'+attributeType.attrTypeId+'" id="'+attributeType.attrTypeId+'"> ';
										tableElement = tableElement + '<option value = "" ></option>';
										
										var attributesList = productAttributesMap[attributeType.attrTypeId];
										for(var j=0 ; j<attributesList.length ; j++){
											var attribute = attributesList[j];
											tableElement = tableElement + '<option value = "' + attribute.attrCode + '" >'+ attribute.attrName+'</option>';
										}
										tableElement = tableElement + '</select>';	
									}
								}
								
								
								
								
								tableElement = tableElement + '</td>';		
								tableElement = tableElement + '</tr>';	
								
								if(i % 3 === 0){
									tableElement = tableElement + '<tr class="productAttributes"><td>&nbsp;</td></tr>';	
								}  
										 
								$('#features tr:last').after(tableElement);	
							
							}
							
						}
						$('#features tr:last').after('<tr class="productAttributes"><td>&nbsp;</td></tr><tr class="productAttributes"><td></td><td><input type="submit" class="smallSubmit" value="Submit"/></td></tr>');
						
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
			         <td align='right' class='label'>Select Product:</td>
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
			         <td align='right' class='label'>Product Type:</td>
			         <td valign='middle'>
	             		<select name="childProductCategoryId" id="childProductCategoryId" onchange="getAttributeTypes()">
						</select>	
			         </td>
			      </tr> 
			      
			      <#--
			      <tr>
			         <td align='right' class='label'>Sub Child Type:</td>
			         <td valign='middle'>
	             		<select name="subChildProductCategoryId" id="subChildProductCategoryId" onchange="getAttributeTypes()">
						</select>	
			         </td>
			      </tr>
			      -->
			      <tr>
			       	 <td>
			       	 	&nbsp;
			       	 </td>
			      </tr>
			      
			      
			      
			      
			     
					
					
					<#--
					<tr>
				      <td></td>
				      <td><input type="submit" class="smallSubmit" value="Submit"/></td>
				    </tr>
			      	-->
								      
      		</table>
     		<table id="categories" name="categories" width="100%" border="0" cellspacing="0" cellpadding="0">
     		</table>
		</form>

	 </div> 
    
</div>
