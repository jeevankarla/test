
<#if parameters.ajaxLookup?default("N") == "Y">
	${StringUtil.wrapString(treeNodesListJSON)}
<#else>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/ui/development-bundle/external/jquery.cookie.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jsTree/jquery.jstree.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
     
function createTree() {
	jQuery(function () {
        jQuery.cookie("jstree_select", "facilityTree");
        jQuery("#tree").jstree({
        "plugins" : [ "themes", "json_data", "cookies", "types"],
        "json_data" : {
        	"data" : ${StringUtil.wrapString(treeNodesListJSON)},
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
            	"rootType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/home.png"
                	}
            	},        	
            	"childType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/home.png"
                	}
            	},
            	"boothType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/booth.png"
                	}
            	},             	
            	"routeType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/direction.png"
                	}
            	}, 
            	"zoneType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/zone.png"
                	}
            	},             	            	           	
            	"factoryType": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/factory.png"
                	}
            	},             	            	           	
            	"distributorType": {
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
  
  
createTree();  
  
</script>
<div id="tree"></div>

</#if>
