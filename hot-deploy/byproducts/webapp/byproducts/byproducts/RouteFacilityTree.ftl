<#if parameters.ajaxLookup?default("N") == "Y">
	${StringUtil.wrapString(treeRouteNodesListJSON)}
<#else>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/ui/development-bundle/external/jquery.cookie.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jsTree/jquery.jstree.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
     
function createTree1() {
	jQuery(function () {
        jQuery.cookie("jstree_select", "facilityTree");
        jQuery("#routeTree").jstree({
        "plugins" : [ "themes", "json_data", "cookies", "types"],
        "json_data" : {
        	"data" : ${StringUtil.wrapString(treeRouteNodesListJSON)},
			"ajax" : {
					"url" : "facilitySubTree",
					"data": function (n) { 
						return {
							"ajaxLookup":"Y",
							"facilityId":$(n).attr("id")
						}
					},
					"success": function (new_data) {
                    	return jQuery.parseJSON(new_data.treeNodeJSON);
                	}
			}
		},
		"themes" : {
				"theme" : "classic",
        },            
        "cookies" : {
                "save_opened" : false
        },
    	"types": {
        	"type_attr": "nodetype",
        	"types": {
        		"frosType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/booth.png"
                	}
            	},
            	"dewsParlorType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/booth.png"
                	}
            	},
            	"institutionType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/booth.png"
                	}
            	},   
            	"avmFrosType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/booth.png"
                	}
            	},      	
            	"mccsType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/booth.png"
                	}
            	},
            	"parlourType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/booth.png"
                	}
            	},
            	"wholesaleType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/booth.png"
                	}
            	},
            	"kfrosType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/booth.png"
                	}
            	},             	
            	"otherType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/booth.png"
                	}
            	}, 
            	"routeType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/truck.png"
                	}
            	}             	            	
        	}
        }        
      });        
    });     
  }
  
function callDocument(url) {
    jQuery(location).attr('href', url);
}
  
  
createTree1();  
  
</script>
<div id="routeTree"></div>

</#if>
