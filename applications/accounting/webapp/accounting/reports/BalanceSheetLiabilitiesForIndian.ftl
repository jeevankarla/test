<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/ui/development-bundle/external/jquery.cookie.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jsTree/jquery.jstree.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
<#-- some labels are not unescaped in the JSON object so we have to do this manuely -->
function unescapeHtmlText(text) {
    return jQuery('<div />').html(text).text()
}
createTree();
<#-- creating the JSON Data -->
var rawdata = [
	<#if (glAccountClassIdsLiability?has_content)>
    	<@fillLiabilityTree LiabilityCatalogs = glAccountClassIdsLiability/>
    </#if>
    	<#macro fillLiabilityTree LiabilityCatalogs>
        	<#if (LiabilityCatalogs?has_content)>
            	<#list LiabilityCatalogs as Liabilitycatalog>
                <#assign glAccountClassId = Liabilitycatalog.glAccountClassId/>
                <#if !glAccountClassId?has_content>                    
                </#if>
                <#assign glAccountClassId = Liabilitycatalog.glAccountClassId/>
                {
                <#if glAccountClassId?has_content>
                    "data": {"title" : unescapeHtmlText("<#if Liabilitycatalog.glAccountClassId?has_content>${Liabilitycatalog.glAccountClassId}<#else>${glAccountClassId}</#if> <#if Liabilitycatalog.glAccountClassId?has_content></#if>"), "attr": {"href": "<@ofbizUrl>/Editxyz?glAccountClassId=${glAccountClassId}</@ofbizUrl>", "onClick" : "callDocument('<@ofbizUrl>/Editxyz?glAccountClassId=${glAccountClassId}</@ofbizUrl>');"}},
                    "attr": {"id" : "${glAccountClassId}", "rel" : "root"},
                </#if>
                <#if liabilityAccountBalanceList?has_content>               
                    "children": [
                        <@fillCategoryTree childLiabilityList = liabilityAccountBalanceList condition=Liabilitycatalog.glAccountClassId/>
                    ]
                </#if>
                <#if Liabilitycatalog_has_next>
                },
                <#else>
                }
                </#if>
            </#list>
          </#if>
        </#macro>
		<#macro fillCategoryTree childLiabilityList condition>		
			<#if childLiabilityList?has_content>
            	<#assign total=0/>
            	<#list childLiabilityList as childs>
            	<#if childs.glAccountClassId?exists>
            		<#if childs.glAccountClassId == condition>
            			<#assign glAccountClass = childs.glAccountClassId/>
           					{ "data": {"title" : "${childs.accountCode} ${childs.accountName} <@ofbizCurrency isoCode=currencyUomId amount=childs.balance?if_exists/>"},
             				"attr": {"href":"<@ofbizUrl>/FindAcctgTransEntries?organizationPartyId=${organizationPartyId}&glAccountId=${childs.glAccountId}</@ofbizUrl>", "onClick":"callDocument('<@ofbizUrl>/FindAcctgTransEntries?organizationPartyId=${organizationPartyId}&glAccountId=${childs.glAccountId}</@ofbizUrl>')" }},
          			         <#assign total=total+childs.balance/>
                	</#if>
                <#else>
                </#if>
                </#list>{"data":{"title":"Total ${total}"}}
            </#if>
        </#macro>
     ];
     <#-------------------------------------------------------------------------------------create Tree-->
function createTree() {
	jQuery(function () {
    	<#if !openTree>
            jQuery.cookie('jstree_select', null);
            jQuery.cookie('jstree_open', null); 
        <#else>
            jQuery.cookie("jstree_select", "glAccountClassIdsLiability");
        </#if>
        jQuery("#liabilityTree").jstree({
        	"plugins" : [ "themes", "json_data", "cookies", "ui", "types"],
            "json_data" : {
                "data" : rawdata
            },
            "themes" : {
                "icons" : false
            },
            "cookies" : {
                "save_opened" : true
            },
        "types" : {
            "valid_children" : [ "root" ],
            "types" : {
                "CATEGORY" : {
                    "icon" : { 
                        "image" : "/images/jquery/plugins/jsTree/themes/apple/d.png",
                        "position" : "10px40px"
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
</script>
<div id="liabilityTree"></div>
<style type="text/css">
    .jstree-default a 
        {
            white-space:normal !important;
            height: auto;
        }
    .jstree-default .jstree-leaf > ins
        {
            background-position:-36px 0;
            vertical-align: top;
        }
</style> 
