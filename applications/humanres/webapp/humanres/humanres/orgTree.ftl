
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/ui/development-bundle/external/jquery.cookie.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jsTree/jquery.jstree.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
     
function createTree() {
	jQuery(function () {
        jQuery.cookie("jstree_select", "orgTree");
        jQuery("#tree").jstree({
        "plugins" : [ "themes", "json_data", "cookies", "types"],
        "json_data" : {
                "data" : ${StringUtil.wrapString(treeNodesListJSON)}
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
                        "image" : "/images/jquery/plugins/jsTree/themes/home.png"
                	}
            	},        	
            	"department": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/userGroup.png"
                	}
            	},
            	"employee": {
					"icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/person.png"
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

