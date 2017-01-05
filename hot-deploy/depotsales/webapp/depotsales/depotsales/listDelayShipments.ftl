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
<script type="text/javascript">
 
    function getDCReport(orderId){
		var formId = "#" + "dcForm";
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
		jQuery(formId).append(jQuery(param1));
        jQuery(formId).submit();
    }

</script>

<#if dataList?has_content>
  
  <form name="listOrders" id="listOrders"  method="post" >
   
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td align="center">OrderId</td>
          <td align="center">order Date</td>
          <td align="center">Order Qty</td>
          <td align="center">Supplier Name</td>
          <td align="center">1st Shipment Date</td>
          <td align="center">ship Qty</td>
          <td align="center">Shipment Delay</td>
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
      <#list dataList as eachOrder>
      		<tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            	<td><#if eachOrder.orderId=="Total"><font color="Blue"> <b></#if>${eachOrder.orderId?if_exists} <#if eachOrder.orderId=="Total"></font></b></#if></td>
              	<td><#if eachOrder.orderId=="Total"> <font color="Blue"> <b></#if>${eachOrder.orderDate?if_exists}<#if eachOrder.orderId=="Total"></font></b></#if></td>
              	<td><#if eachOrder.orderId=="Total"><font color="Blue"> <b></#if>${eachOrder.ordQty?if_exists}<#if eachOrder.orderId=="Total"></b></font></#if></td>
              	<td><#if eachOrder.orderId=="Total"><font color="Blue"> <b></#if>${eachOrder.supplierName?if_exists}<#if eachOrder.orderId=="Total"></font></b></#if></td>
              	<td><#if eachOrder.orderId=="Total"><font color="Blue"> <b></#if>${eachOrder.shipDate?if_exists}<#if eachOrder.orderId=="Total"></b></font></#if></td>
              	<td align="right"><#if eachOrder.orderId=="Total"><font color="Blue"> <b></#if>${eachOrder.shipedQty?if_exists}<#if eachOrder.orderId=="Total"></b></font></#if></td>
              	<td align="right"><#if eachOrder.orderId=="Total"><font color="Blue"> <b></#if>${eachOrder.diffDays?if_exists} Days<#if eachOrder.orderId=="Total"></b></font></#if></td>
            </tr>
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Orders Found</h3>
</#if>
