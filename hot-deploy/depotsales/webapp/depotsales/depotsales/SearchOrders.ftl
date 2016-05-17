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
	<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
	<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />

<style type="text/css">



input[type=text] {
    width: 100%;
    padding: 12px 20px;
    margin: 8px 0;
    box-sizing: border-box;
    border: 3px solid #ccc;
    -webkit-transition: 0.5s;
    transition: 0.5s;
    outline: none;
}

input[type=text]:focus {
    border: 3px solid #555;
}
form .button {
border-radius: 12px;
}
</style>
<script type="text/javascript">


</script>

 <form action="" class="search">
	<!-- We'll have a button that'll make the search input appear, a submit button and the input -->
	
	<!-- Alos, a label for it so that we can style it however we want it-->
	<table id="searchTable" >
	
	<tr>
	<!-- trigger button and input -->
	<td><b>IndentId : </b></td>
	<td><input type="search" name="orderId" id="orderId" placeholder="Search"></td>
	<td><input  value="Find" type="submit" class="button"><input type="hidden" name="salesChannelEnumId" value="BRANCH_CHANNEL">
	<label for="submit" class="submit"></label></td>
	<td><input type="hidden" name="statusId" id="statusId" value="ORDER_APPROVED">
	</tr>
	</table>
</form>

      