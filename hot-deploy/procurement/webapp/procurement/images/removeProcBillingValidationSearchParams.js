$(document).ready(function(){	
	$('.nav-pager a').each(function(){	
			var queryParameters = {};
			queryString = "";
			re = /([^&=]+)=([^&]*)/g ;
			var m;
			 orig = $(this).attr("href");
			var  newHref ="";	
			// Creates a map with the query string parameters			
			flag = false;
			while (m = re.exec(orig)) {
				
				if(decodeURIComponent(m[1]) != "centerId" && decodeURIComponent(m[1]) != "centerId_op"){
					flag = true;
					newHref = newHref+ decodeURIComponent(m[1])+"="+decodeURIComponent(m[2])+"&";
				}
			    
			}		
			if(flag){			
				//alert("true" +newHref);
				$(this).attr("href", $(this).attr("href").replace(orig, newHref));			
			}
			//location.search = $.param(queryParameters); // Causes page to reload		
	});
	
	
});

