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
        		"company": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/globe.png"
                	}
            	},
            	"HO": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/userGroup.png"
                	}
            	},              	
            	"Depots": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/zone.png"
                	}
            	}, 
            	"RO": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/home.png"
                	}
                },
            	"BO": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/depot.png"
                	}
            	},                 
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
