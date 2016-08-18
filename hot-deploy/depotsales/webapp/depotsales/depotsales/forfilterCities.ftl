<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the 
http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<script type="text/javascript">




 function fetchCitesForState(state) {
		state = state;
		
		var dataJson = {"state": state};
		jQuery.ajax({
                url: 'fetchCitesForState',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
					
						var orderList = result["orderList"];
						
						var tableElement = "";
						tableElement +="<option value=''></option>";
						 $.each(orderList, function(key, item){
		       	  				    tableElement +="<option value='"+item['geoId']+"'>"+item['geoName']+"</option>";
		       	  				 });
		       	  				 if(tableElement.length > 0)
		       	  			     $('#district').empty().append(tableElement);
		       	  			     else
		       	  			     $('#district').empty();	
						
               		}
               	}							
		});
	}
	
	


function getCites(){
var state = $("#satate option:selected").val();

fetchCitesForState(state);


}



function selectBranch(){
var branchId = $("#branchId option:selected").val();

 if(branchId.length == 0)
 alert("Please Select Branch..!!");


}

</script>
