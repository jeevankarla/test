
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

<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="text/javascript">

	var finAccountTypeMap = ${StringUtil.wrapString(finAccountTypeJSON)!'{}'};
	
	 
	 
function setFinAccountType(finAccountType){
var finAccountIdList;
var finAccIdsList =[];
var finAccType=finAccountType.value;
finAccList=finAccountTypeMap[finAccType];
	 	 if(finAccList != undefined && finAccList != ""){
				 	   	$.each(finAccList, function(key, item){
			 				 finAccIdsList.push('<option value="'+item.value+'">'+item.text+'</option>');
			 				 
					});
			finAccountIdList = finAccIdsList;
					
	 	   }
	 	   
		$('#contraFinAccountId').html(finAccountIdList.join(''));

}
</script>