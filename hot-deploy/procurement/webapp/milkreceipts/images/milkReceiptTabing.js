jQuery(document).ready(function() {
	
	var focusables = $(":focusable");
	focusables.keyup(function(e) {  
		var current = focusables.index(this);
		if(e.keyCode==9){
			var tempObject = focusables.eq(current-1).length ? focusables.eq(current-1) : focusables.eq(0);
    		current = focusables.index(tempObject);
    	}
    	var curentElName = focusables.eq(current).attr("name");
    	if(curentElName=="mccCode" || curentElName=="ackTime"){
    		var prevEl = focusables.eq(current-1).length ? focusables.eq(current-1) : focusables.eq(0);
    		var prevElName = prevEl.attr("name");
    		var tempTime = prevEl.val();
    		if(tempTime.length==0){
				alert('invalid Time formate. length should be 4');
				$('input[name='+curentElName+']').val('');
				prevEl.val('');
				prevEl.focus();
				return false;
			}
    		if(tempTime!=''){
    			if(tempTime.length<4 || tempTime.length==0){
    				alert('invalid Time formate. length should be 4');
    				$('input[name='+curentElName+']').val('');
    				prevEl.val('');
    				prevEl.focus();
    				return false;
    			}
    			var hh = tempTime.substring(0,2);
    			var mm = tempTime.substring(2,4);
    			if(hh>23 || mm>59){
    				alert('invalid Time formate');
    				prevEl.val('');
    				$('input[name='+curentElName+']').val('');
    				prevEl.focus();
    				return false;
    			}
    			
    			
    		}
		}
    	// auto tabbing for time tabs is added
    	if(curentElName == "ackTime" || curentElName == "sendTime"){
    		var timeVal = $('input[name='+curentElName+']').val();
    		var valLength = timeVal.length;
    		if(e.which == 110 || e.which == 190){
    			$(this).val( $(this).val().replace('.',''));
    		}
    		$(this).val( $(this).val().replace(/[^0-9\.]/g,''));
    		if(valLength == 4){
    			next = focusables.eq(current+1).length ? focusables.eq(current+1) : focusables.eq(0);
    			next.focus();
    		}
    	}
    	/*if(curentElName == "product"){
    		var timeVal = $('input[name='+curentElName+']').val();
    		var valLength = timeVal.length;
    		if(valLength == 3){
    			next = focusables.eq(current+1).length ? focusables.eq(current+1) : focusables.eq(0);
    			next.focus();
    		}
    	}*/
    	
    	
    	if(curentElName == "fcQtyLtrs" ){    		
    		prev = focusables.eq(current-1).length ? focusables.eq(current-1) : focusables.eq(0);
    		next = $('input[name=fcAckQtyLtrs]');    		
    	}else if(curentElName == "fcAckQtyLtrs" ){
    		prev = $('input[name=fcQtyLtrs]');
    		next = $('input[name=fcFat]');
    	}else if(curentElName == "fcFat" ){
    		prev = $('input[name=fcAckQtyLtrs]');
	    	next = $('input[name=fcAckFat]');
    	}else if(curentElName == "fcAckFat" ){
    		prev = $('input[name=fcFat]');
    		if(prev.val()>12){
    			alert('please check FC fat Value . You entered greater than 12');
    			$('input[name='+curentElName+']').val('');
    			prev.val('');
    			prev.focus();
    		}
    		next = $('input[name=fcSnf]');
    	}else if(curentElName == "fcSnf" ){
    		prev = $('input[name=fcAckFat]');
    		if(prev.val()>12){
    			alert('please check FC ACK FAT Value . You entered greater than 12');
    			$('input[name='+curentElName+']').val('');
    			prev.val('');
    			prev.focus();
    		}
    		next = $('input[name=fcAckSnf]');    		
    	}else if(curentElName == "fcAckSnf" ){
    		prev = $('input[name=fcSnf]');
    		if(prev.val()>12){
    			alert('please check FC SNF Value . You entered greater than 12');
    			$('input[name='+curentElName+']').val('');
    			prev.val('');
    			prev.focus();
    		}
    		next = $('input[name=fcClr]');
    	}else if(curentElName == "fcClr" ){
    		prev = $('input[name=fcAckSnf]');
    		if(prev.val()>12){
    			alert('please check FC ACK SNF Value . You entered greater than 12');
    			$('input[name='+curentElName+']').val('');
    			prev.val('');
    			prev.focus();
    		}
    		next = $('input[name=fcAckClr]');    		
    	}else if(curentElName == "fcAckClr" ){
    		prev = $('input[name=fcClr]');
    		next = $('input[name=fcAcid]');
    	}else if(curentElName == "fcAcid" ){
    		prev = $('input[name=fcAckClr]');
	    	next = $('input[name=fcAckAcid]');    		
    	}else if(curentElName == "fcAckAcid" ){
    		prev = $('input[name=fcAcid]');
    		next = $('input[name=fcTemp]');
    	}else if(curentElName == "fcTemp" ){
    		prev = $('input[name=fcAckAcid]');
	    	next = $('input[name=fcAckTemp]');    		
    	}else if(curentElName == "fcAckTemp" ){
    		prev = $('input[name=fcTemp]');
    		next = $('input[name=bcQtyLtrs]');
    	}else if(curentElName == "bcQtyLtrs" ){
    		prev = $('input[name=fcAckTemp]');
	    	next = $('input[name=bcAckQtyLtrs]');    		
    	}else if(curentElName == "bcAckQtyLtrs" ){
    		prev = $('input[name=bcQtyLtrs]');
    		next = $('input[name=bcFat]');
    	}else if(curentElName == "bcFat" ){
    		prev = $('input[name=bcAckQtyLtrs]');
	    	next = $('input[name=bcAckFat]');    		
    	}else if(curentElName == "bcAckFat" ){
    		prev = $('input[name=bcFat]');
    		var productJson =getProductJson();
    		if(prev.val()>12){
    			alert('please check BC FAT Value . You entered greater than 12');
    			$('input[name='+curentElName+']').val('');
    			prev.val('');
    			prev.focus();
    		}
    		next = $('input[name=bcSnf]');
    	}else if(curentElName == "bcSnf" ){
    		prev = $('input[name=bcAckFat]');
    		if(prev.val()>12){
    			alert('please check BC ACK FAT Value . You entered greater than 12');
    			$('input[name='+curentElName+']').val('');
    			prev.val('');
    			prev.focus();
    		}
	    	next = $('input[name=bcAckSnf]');    		
    	}else if(curentElName == "bcAckSnf" ){
    		prev = $('input[name=bcSnf]');
    		if(prev.val()>12){
    			alert('please check BC SNF Value . You entered greater than 12');
    			$('input[name='+curentElName+']').val('');
    			prev.val('');
    			prev.focus();
    		}
    		next = $('input[name=bcClr]');
    	}else if(curentElName == "bcClr" ){
    		prev = $('input[name=bcAckSnf]');
    		if(prev.val()>12){
    			alert('please check BC ACK SNF Value . You entered greater than 12');
    			$('input[name='+curentElName+']').val('');
    			prev.val('');
    			prev.focus();
    		}
	    	next = $('input[name=bcAckClr]');    		
    	}else if(curentElName == "bcAckClr" ){
    		prev = $('input[name=bcClr]');
    		next = $('input[name=bcAcid]');
    	}else if(curentElName == "bcAcid"){
    		prev = $('input[name=bcAckClr]');
	    	next = $('input[name=bcAckAcid]');    		
    	}else if(curentElName == "bcAckAcid" ){
    		prev = $('input[name=bcAcid]');
    		next = $('input[name=bcTemp]');
    	}else if(curentElName == "bcTemp" ){
    		prev = $('input[name=bcAckAcid]');
	    	next = $('input[name=bcAckTemp]');    		
    	}else if(curentElName == "bcAckTemp" ){
    		prev = $('input[name=bcTemp]');
    		next = $('input[name=mcQtyLtrs]');
    	}else if(curentElName == "mcQtyLtrs" ){
    		prev = $('input[name=mcAckTemp]');
	    	next = $('input[name=mcAckQtyLtrs]');    		
    	}else if(curentElName == "mcAckQtyLtrs" ){
    		prev = $('input[name=mcQtyLtrs]');
    		next = $('input[name=mcFat]');
    	}else if(curentElName == "mcFat" ){
    		prev = $('input[name=mcAckQtyLtrs]');
	    	next = $('input[name=mcAckFat]');    		
    	}else if(curentElName == "mcAckFat" ){
    		prev = $('input[name=mcFat]');
    		if(prev.val()>12){
    			alert('please check MC FAT Value . You entered greater than 12');
    			$('input[name='+curentElName+']').val('');
    			prev.val('');
    			prev.focus();
    		}
    		next = $('input[name=mcSnf]');
    	}else if(curentElName == "mcSnf" ){
    		prev = $('input[name=mcAckFat]');
    		if(prev.val()>12){
    			alert('please check MC ACK FAT Value . You entered greater than 12');
    			$('input[name='+curentElName+']').val('');
    			prev.val('');
    			prev.focus();
    		}
	    	next = $('input[name=mcAckSnf]');    		
    	}else if(curentElName == "mcAckSnf" ){
    		prev = $('input[name=mcSnf]');
    		if(prev.val()>12){
    			alert('please check MC SNF Value . You entered greater than 12');
    			$('input[name='+curentElName+']').val('');
    			prev.val('');
    			prev.focus();
    		}
    		next = $('input[name=mcClr]');
    	}else if(curentElName == "mcClr" ){
    		prev = $('input[name=mcAckSnf]');
    		if(prev.val()>12){
    			alert('please check MC ACK SNF Value . You entered greater than 12');
    			$('input[name='+curentElName+']').val('');
    			prev.val('');
    			prev.focus(); 
    		}
	    	next = $('input[name=mcAckClr]');    		
    	}else if(curentElName == "mcAckClr" ){
    		prev = $('input[name=mcClr]');
    		next = $('input[name=mcAcid]');
    	}else if(curentElName == "mcAcid" ){
    		prev = $('input[name=mcAckClr]');
	    	next = $('input[name=mcAckAcid]');    		
    	}else if(curentElName == "mcAckAcid" ){
    		prev = $('input[name=mcAcid]');
    		next = $('input[name=mcTemp]');
    	}else if(curentElName == "mcTemp" ){
    		prev = $('input[name=mcAckAcid]');
	    	next = $('input[name=mcAckTemp]');    		
    	}else if(curentElName == "mcAckTemp" ){
    		prev = $('input[name=mcTemp]');
    		next = $('input[name=submitButton]');
    	}else  	if(curentElName == "submitButton"){    		
	    	prev = $('input[name=bcTemp]');    		
    	}else{
    		prev = focusables.eq(current-1).length ? focusables.eq(current-1) : focusables.eq(0);
    		next = focusables.eq(current+1).length ? focusables.eq(current+1) : focusables.eq(0);
    	}
    	if (e.keyCode == 38 && (curentElName !="mccCode" && curentElName != "product")) {    		
           prev.focus();
   		}
   		if (e.keyCode == 9) {
   			next.focus();
   		}
    });
	
	
});
function checkFatSnfRanges(){
	var fcFat = 0;
	var fcAckFat = 0;
	var fcSnf = 0;
	var fcAckSnf = 0;
	
	var bcFat = 0;
	var bcAckFat = 0;
	var bcSnf = 0;
	var bcAckSnf = 0;
	
	var mcFat = 0;
	var mcAckFat = 0;
	var mcSnf = 0;
	var mcAckSnf = 0;
	
	fcFat = $('[name = fcFat]').val();
	fcAckFat = $('[name = fcAckFat]').val();
	fcSnf= $('[name = fcSnf]').val();
	fcAckSnf = $('[name = fcAckSnf]').val();
	
	
	bcFat = $('[name = bcFat]').val();
	bcAckFat = $('[name = bcAckFat]').val();
	bcSnf= $('[name = bcSnf]').val();
	bcAckSnf = $('[name = bcAckSnf]').val();
	
	mcFat = $('[name = mcFat]').val();
	mcAckFat = $('[name = mcAckFat]').val();
	mcSnf= $('[name = mcSnf]').val();
	mcAckSnf = $('[name = mcAckSnf]').val();
	
	
	var productJson =getProductJson();
	var fatValuesStr = '';
	var showConfirmation = false;
	if(productJson){
			var minFat = productJson['minFat'];
			var maxFat = productJson['maxFat'];
			var minSnf = productJson['minSnf'];
			var maxSnf = productJson['maxSnf'];
			var showAlert = false;
			if(fcFat){
				if(fcFat<minFat || fcFat>maxFat || fcFat<minFat || fcAckFat>maxFat ){
					showAlert = true;
					showConfirmation = true;
				}
			}
			if(fcSnf){
				if(fcSnf<minSnf || fcSnf>maxSnf || fcSnf<minSnf || fcAckSnf>maxSnf ){
					showAlert = true;
					showConfirmation = true;
				}
			}
			if(showAlert){
				alert("Please Check Fat,Snf Values for First Cell(FC)."+"\n"+" For this Product"+"\n"+" MinFat :"+minFat+" MaxFat:"+maxFat+"\n"+" MinSnf:"+minSnf+" MaxSnf:"+maxSnf );
				showAlert =false;
			}
			
			
			if(bcFat){
				if(bcFat<minFat || bcFat>maxFat || bcFat<minFat || bcAckFat>maxFat ){
					showAlert = true;
					showConfirmation = true;
				}
			}
			if(bcSnf){
				if(bcSnf<minSnf || bcSnf>maxSnf || bcSnf<minSnf || bcAckSnf>maxSnf ){
					showAlert = true;
					showConfirmation = true;
				}
			}
			if(showAlert){
				alert("Please Check Fat,Snf Values for Back Cell(BC)."+"\n"+" For this Product"+"\n"+" MinFat :"+minFat+" MaxFat:"+maxFat+"\n"+" MinSnf:"+minSnf+" MaxSnf:"+maxSnf );
				showAlert =false;
			}
			
			if(mcFat){
				if(mcFat<minFat || mcFat>maxFat || mcFat<minFat || mcAckFat>maxFat ){
					showAlert = true;
					showConfirmation = true;
				}
			}
			if(mcSnf){
				if(mcSnf<minSnf || mcSnf>maxSnf || mcSnf<minSnf || mcAckSnf>maxSnf ){
					showAlert = true;
					showConfirmation = true;
				}
			}
			if(showAlert){
				alert("Please Check Fat,Snf Values for Middle Cell(MC)."+"\n"+" For this Product"+"\n"+" MinFat :"+minFat+" MaxFat:"+maxFat+"\n"+" MinSnf:"+minSnf+" MaxSnf:"+maxSnf );
				showAlert =false;
			}
	}
	if(showConfirmation){
		if(confirm('Dou u want to Create Record ?')){
			return true;
		}else {
			return false;
		}
	}
	return true;
	
}

function clearFeilds(){
	$('input[name=sendTime]').val('');
	$('input[name=ackTime]').val('');
	
	$('input[name=product]').val('');
	$('input[name=productId]').val('');
	$('input[name=facilityId]').val('');
	$('input[name=mccCode]').val('');
	$('span#unitToolTip').html('none');
	$('input[name=tankerNo]').val('');
	
	$('input[name=fcQtyLtrs]').val('');
	$('input[name=fcAckQtyLtrs]').val('');
	$('input[name=fcFat]').val('');
	$('input[name=fcAckFat]').val('');
	$('input[name=fcSnf]').val('');
	$('input[name=fcAckSnf]').val('');
	$('input[name=fcClr]').val('');
	$('input[name=fcAckClr]').val('');
	$('input[name=fcAcid]').val('');
	$('input[name=fcAckAcid]').val('');
	$('input[name=fcTemp]').val('');
	$('input[name=fcAckTemp]').val('');
	
	$('input[name=mcQtyLtrs]').val('');
	$('input[name=mcAckQtyLtrs]').val('');
	$('input[name=mcFat]').val('');
	$('input[name=mcAckFat]').val('');
	$('input[name=mcSnf]').val('');
	$('input[name=mcAckSnf]').val('');
	$('input[name=mcClr]').val('');
	$('input[name=mcAckClr]').val('');
	$('input[name=mcAcid]').val('');
	$('input[name=mcAckAcid]').val('');
	$('input[name=mcTemp]').val('');
	$('input[name=mcAckTemp]').val('');
	
	$('input[name=bcQtyLtrs]').val('');
	$('input[name=bcAckQtyLtrs]').val('');
	$('input[name=bcFat]').val('');
	$('input[name=bcAckFat]').val('');
	$('input[name=bcSnf]').val('');
	$('input[name=bcAckSnf]').val('');
	$('input[name=bcClr]').val('');
	$('input[name=bcAckClr]').val('');
	$('input[name=bcAcid]').val('');
	$('input[name=bcAckAcid]').val('');
	$('input[name=bcTemp]').val('');
	$('input[name=bcAckTemp]').val('');
	
	
	
	$('input[name=submitButton]').attr("disabled",false);
	$('span#productToolTip').html('none');
}

$(function() {
    $('#submitEntry').click (function (){
    	if(!checkFatSnfRanges()){
    		return false;
    	}
    	var dateFormat = $('[name="sendDate"]').datepicker( "option", "dateFormat" );
 	   $('[name="sendDate"]').datepicker( "option", "dateFormat", "MM d, yy" );
 	   
 	  var recDateFormat = $('[name="receiveDate"]').datepicker( "option", "dateFormat" );
	   $('[name="receiveDate"]').datepicker( "option", "dateFormat", "MM d, yy" );
    	
	   var action = "createMilkReceiptEntryAjax";
        
        var dataString = $("#milkReceiptEntry").serialize();
        $('input[name=submitButton]').attr("disabled",true);
        $.ajax({
			 type: "POST",
			 url: action,
			 data: dataString,
			 dataType: 'json',
			 success:function(result){
				if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					 populateError(result["_ERROR_MESSAGE_LIST_"]);
					 alert('Error Message  ::'+result["_ERROR_MESSAGE_LIST_"]);
					 $('input[name=sendTime]').focus();
					 $('input[name=submitButton]').attr("disabled",false);
				}else{
					   $("div#ReceiptEntry_spinner").fadeIn();
				 	   $('div#ReceiptEntry_spinner').html();            	 
				 	   $('div#ReceiptEntry_spinner').addClass("messageStr");
				 	   $('div#ReceiptEntry_spinner').html('<label>'+result["_EVENT_MESSAGE_"]+'</label>');
				 	   $('div#ReceiptEntry_spinner').delay(10000).fadeOut('slow');
				 	   clearFeilds();
				 	   $('input[name=sendTime]').focus();
				 	   setUpRecentList(result["orderItemMap"]);
				}								 
			},
			error: function(){
				populateError(result["_ERROR_MESSAGE_"]);
			}	
		});
        
        
        
        $('[name="sendDate"]').datepicker( "option", "dateFormat", "dd-mm-yy" );
        $('[name="receiveDate"]').datepicker( "option", "dateFormat", "dd-mm-yy" );
        $('input[name=submitButton]').attr("disabled",false);
 	 $('input[name=sendTime]').focus();
 	      	   return false;
            
    });
});
function populateError(msg){
	$("div#ReceiptEntry_spinner").fadeIn();
	$('div#ReceiptEntry_spinner').removeClass("messageStr");
	$('div#ReceiptEntry_spinner').addClass("errorMessage");
	$('div#ReceiptEntry_spinner')
	.html('<label>'+msg +'</label>');
	$('div#ReceiptEntry_spinner').delay(10000).fadeOut('slow');
	
}
function fetchRecentChange(){
	var dataJson;
	var action = "fetchRecentReceiptAjax";
		$.ajax({
			 type: "POST",
             url: action,
             data: dataJson,
             dataType: 'json',
            
			success:function(result){
					var recentChnage = result["orderItemMap"]; 
	            	var count = 0;
	            	
	            	for(var key in recentChnage){
	            		count++;
	                }
					if(count != 0){
	            		setUpRecentList(recentChnage);
	            	}
			}
				
		});
	
}

function fetchMilkReceiptItems(){
	var dataJson = {"milkTransferId":$('[name=milkTransferId]').val(),
		 };
	var action = "fetchRecentReceiptAjax";
		$.ajax({
			 type: "POST",
             url: action,
             data: dataJson,
             dataType: 'json',
            
			success:function(result){
					var recentChnage = result["orderItemMap"];

					var i = 0;
				    for(var key in recentChnage){
				        ++i;
				    }
					if(i == 0){
						clearFeilds();	
						$('span#unitToolTip').html('none');
						$('span#productToolTip').html('none');
						$('input[name=milkTransferId]').focus();		
					}
	            	setUpMilkReceiptFeilds(recentChnage);	
			}
				
		});
	
}
function setUpMilkReceiptFeilds(recentChnage){
	var milkTransferItems ;
	milkTransferItems = recentChnage["milkTransferItems"];
	var qtyFlag=recentChnage["qtyKgsFlag"];	
	$('input[name=tankerNo]').val(recentChnage["tankerId"]);
	$('input[name=product]').val(recentChnage["productId"]);
	$('input[name=productId]').val(recentChnage["productId"]);
	$('input[name=mccCode]').val(recentChnage["mccCode"]);
	$('input[name=facilityId]').val(recentChnage["facilityId"]);
	$('input[name=sendDate]').val(recentChnage["sendDateStr"]);
	$('input[name=receiveDate]').val(recentChnage["receiveDateStr"]);
	$('span#unitToolTip').html(recentChnage["facilityName"]);
	$('span#productToolTip').html(recentChnage["productName"]);
	if(milkTransferItems){
		$.each(milkTransferItems, function(idx,obj){
			var cellType  = obj["cellType"];
			if(cellType == "FC"){
				$('input[name=sendTime]').val(obj["sendTime"]);
				$('input[name=ackTime]').val(obj["ackTime"]);
				if(qtyFlag=="Y"){
					$('input[name=fcQtyLtrs]').val(obj["quantity"]);
				}else{
					$('input[name=fcQtyLtrs]').val(obj["quantityLtrs"]);
				}
				
				$('input[name=fcFat]').val(obj["fat"]);
				$('input[name=fcSnf]').val(obj["snf"]);
				$('input[name=fcClr]').val(obj["sendLR"]);
				$('input[name=fcAcid]').val(obj["sendAcidity"]);
				$('input[name=fcTemp]').val(obj["sendTemparature"]);
				
				$('input[name=fcAckQtyLtrs]').val(obj["receivedQuantityLtrs"]);
				$('input[name=fcAckFat]').val(obj["receivedFat"]);
				$('input[name=fcAckSnf]').val(obj["receivedSnf"]);
				$('input[name=fcAckClr]').val(obj["receivedLR"]);
				$('input[name=fcAckAcid]').val(obj["receivedAcidity"]);
				$('input[name=fcAckTemp]').val(obj["receivedTemparature"]);
				
				
				$('input[name=fcSequenceNum]').val(obj["sequenceNum"]);
				var milkCondition = obj["milkCondition"];
				var soda = obj["sendSoda"];
				var cob = obj["sendCob"];
				$('#milkCondition').val(milkCondition).attr("selected",true);
				$('#soda').val(soda).attr("selected",true);
				$('#cob').val(cob).attr("selected",true);
				setQtyFlag();
				
			}
			if(cellType == "MC"){
				if(qtyFlag=="Y"){
					$('input[name=mcQtyLtrs]').val(obj["quantity"]);
				}else{
					$('input[name=mcQtyLtrs]').val(obj["quantityLtrs"]);
				}
				$('input[name=mcFat]').val(obj["fat"]);
				$('input[name=mcSnf]').val(obj["snf"]);
				$('input[name=mcClr]').val(obj["sendLR"]);
				$('input[name=mcAcid]').val(obj["sendAcidity"]);
				$('input[name=mcTemp]').val(obj["sendTemparature"]);
				
				
				$('input[name=mcAckQtyLtrs]').val(obj["receivedQuantityLtrs"]);
				$('input[name=mcAckFat]').val(obj["receivedFat"]);
				$('input[name=mcAckSnf]').val(obj["receivedSnf"]);
				$('input[name=mcAckClr]').val(obj["receivedLR"]);
				$('input[name=mcAckAcid]').val(obj["receivedAcidity"]);
				$('input[name=mcAckTemp]').val(obj["receivedTemparature"]);
				
				
				$('input[name=mcSequenceNum]').val(obj["sequenceNum"]);
				
				
			}
			if(cellType == "BC"){
				if(qtyFlag=="Y"){
					$('input[name=bcQtyLtrs]').val(obj["quantity"]);
				}else{
					$('input[name=bcQtyLtrs]').val(obj["quantityLtrs"]);
				}
				$('input[name=bcFat]').val(obj["fat"]);
				$('input[name=bcSnf]').val(obj["snf"]);
				$('input[name=bcClr]').val(obj["sendLR"]);
				$('input[name=bcAcid]').val(obj["sendAcidity"]);
				$('input[name=bcTemp]').val(obj["sendTemparature"]);
				
				
				
				$('input[name=bcAckQtyLtrs]').val(obj["receivedQuantityLtrs"]);
				$('input[name=bcAckFat]').val(obj["receivedFat"]);
				$('input[name=bcAckSnf]').val(obj["receivedSnf"]);
				$('input[name=bcAckClr]').val(obj["receivedLR"]);
				$('input[name=bcAckAcid]').val(obj["receivedAcidity"]);
				$('input[name=bcAckTemp]').val(obj["receivedTemparature"]);
			
				$('input[name=bcSequenceNum]').val(obj["sequenceNum"]);
				
			}
				
		});
	}
	
}



