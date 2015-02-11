
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<style type="text/css">
 .labelFontCSS {
    font-size: 13px;
}

</style>

<script type="application/javascript">



	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
	function dialogue(content, title) {
		/* 
		 * Since the dialogue isn't really a tooltip as such, we'll use a dummy
		 * out-of-DOM element as our target instead of an actual element like document.body
		 */
		$('<div />').qtip(
		{
			content: {
				text: content,
				title: title
			},
			position: {
				my: 'center', at: 'center', // Center it...
				target: $(window) // ... in the window
			},
			show: {
				ready: true, // Show it straight away
				modal: {
					on: true, // Make it modal (darken the rest of the page)...
					blur: false // ... but don't close the tooltip when clicked
				}
			},
			hide: false, // We'll hide it maunally so disable hide events
			style: {name : 'cream'}, //'ui-tooltip-light ui-tooltip-rounded ui-tooltip-dialogue', // Add a few styles
			events: {
				// Hide the tooltip when any buttons in the dialogue are clicked
				render: function(event, api) {
				populateDate()
					$('button', api.elements.content).click(api.hide);
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});		
	}
 
	function Alert(message, title)
	{
		// Content will consist of the message and an cancel and submit button
		var message = $('<p />', { html: message }),
			cancel = $('<button />', { text: 'cancel', 'class': 'full' });
 
		dialogue(message, title );		
		
	}
	
	
	//disable the generate button once the form submited
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	var globalUomOptionList="";
  	var paymentMethodList;
  	var uomList=[];
  	var GlobalproductName;
	var GlobalproductId;
  	var GloballongDescription;
	 uomList = ${StringUtil.wrapString(dataJSONList)!'{}'};
	function showUpdateProductForm(productId,uomId) {
         var innerUomId=$.trim(uomId);
          var UomOptionList =[];
			GlobalproductId=productId;
    	 var productflag="y";
       var dataString="productflag=" + productflag + "&productId=" + productId ;
      $.ajax({
             type: "POST",
             url: "getproductName",
           	 data: dataString ,
           	 dataType: 'json',
           	 async: false,
        	 success: function(result) {
              if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
           	  		 alert(result["_ERROR_MESSAGE_"]);
              			}else{
             				  var productDetails=[];
              		 			//alert("=result==="+result);
           	  					 productDetails =result["productObj"];
           	  					 //alert("productDetails=========="+productDetails.longDescription);
          	 	 				GlobalproductName=productDetails.productName;
          	 					 GloballongDescription=productDetails.longDescription;
          	 	 
          					  }
               
          	} ,
         	 error: function() {
          	 	alert(result["_ERROR_MESSAGE_"]);
         	 }
          }); 
 		if(uomList != undefined && uomList != ""){
 		        UomOptionList.push('<option value=""></option>');
				$.each(uomList, function(key, item){
					//alert("item"+item.text);
					//alert("<option value="+item.value+">"+item.text+"</option>");
						if(item.value==innerUomId){
							 UomOptionList.push('<option value="'+item.value+'" selected="selected" >' +item.text+'</option>');
				 		}else{
				  	 		 UomOptionList.push('<option value="'+item.value+'">' +item.text+'</option>');
				    	} 
					});
	 	  
	 	    globalUomOptionList = UomOptionList;
           }
		var message = "";
		message += "<div style='width:100%;height:200px;overflow-x:auto;overflow-y:auto;' ><form action='updateProductDetails' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10  width='100%' > " ; 
			message +="<tr ><td align='right' class='h2' width='15%' >Uom Id: </td><td align='left' width='60%'><select name='quantityUomId' id='quantityUomId' class='h3'>"+
              		<#--<#list uomList as uom><option value='${uom.uomId}' <#if uom.uomId=='"+innerUomId+"'  > selected='selected'</#if> >${uom.description}[${uom.abbreviation}]</option></#list>"+   -->         
					"</select></td></tr>";
		   message += "<tr ><td align='right' class='h2' width='15%' >Product Name: </td><td align='left'  width='75%'  > <input type='text' size='70'  id='productName' name='productName'/><input type='hidden' id='productId'  name='productId'  /> </tr></tbody></table></td></tr>";
			 message += "<tr ><td width='100%' colspan='2' ><table  border='0' cellspacing='10' cellpadding='10'><tbody><tr><td width='15%' align='right' class='label labelFontCSS' >Specification: </td><td align='left'  width='75%'  >";
              message += "<textarea name='longDescription' id='longDescription' cols='70' rows='4'></textarea> </td></tr>";
			
	          message +="<tr ><td align='right' class='h3' width='15%' ><input type='submit' value='Update' id='updateProduct' class='smallSubmit'/></td><td width='20%' align='center' class='h3' ><span align='center'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
	          message += "</table></form>";
	      	
		message += "</div>";				
		var title = "<center  class='h2' >Update Material <center> ";
		Alert(message, title);
	};
	
	function populateDate(){
       jQuery("#productName").val(GlobalproductName);
		jQuery("#productId").val(GlobalproductId);
		jQuery("#longDescription").val(GloballongDescription);
	$('#quantityUomId').html(globalUomOptionList.join(''));

	};
</script>
