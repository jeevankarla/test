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
  <h2> LISTING OF DELIVERIES DELAYED BY MORE THAN 7 DAYS BY SUPPLIERS </h2>
  <form name="listOrders" id="listOrders"  method="post" >
    <table class="basic-table hover-bar" cellspacing="0">
      <tbody>
        <tr class="alternate-row" style="color:black; font-stlye:bold; font-size:12px; background-color:skyblue;">
          <td align="center"><b>OrderId&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></td>
          <td align="center"><b>Order Date</b></td>
          <td align="right"><b>Order Qty(Kgs)</b></td>
          <td align="left"><b>&nbsp;&nbsp;&nbsp;&nbsp;Supplier Name</b></td>
          <td align="center"><b>Delivery  Date</b></td>
          <td align="right"><b>No of Shipments</b></td>
          <td align="right"><b>Delivered Qty(Kgs)</b></td>
          <td align="right"><b>Delivery Delayed</b></td>
        </tr>
     
     
      <#assign alt_row = false>
      <#assign records=0>
      <#list dataList as eachOrder>
      		<tr valign="middle"<#if alt_row> class="alternate-row"</#if> style="font-size:12px; color:black;">
      		     <#if eachOrder.orderId!="Total">
      		     	<#assign records=records+1>
      		     </#if>
            	<td><#if eachOrder.orderId=="Total"> <font color="Blue"> <b></#if>${eachOrder.orderId?if_exists} <#if eachOrder.orderId=="Total"></font></b></#if></td>
              	<td align="center"><#if eachOrder.orderId=="Total"> <font color="Blue"> <b></#if>${eachOrder.orderDate?if_exists}<#if eachOrder.orderId=="Total"></font></b></#if></td>
              	<td align="right"><#if eachOrder.orderId=="Total"><font color="Blue"> <b></#if>${eachOrder.ordQty?if_exists}<#if eachOrder.orderId=="Total"></b></font></#if></td>
              	<td><#if eachOrder.orderId=="Total"><font color="Blue"> <b></#if>&nbsp;&nbsp;&nbsp;&nbsp;${eachOrder.supplierName?if_exists}<#if eachOrder.orderId=="Total"></font></b></#if></td>
              	<td align="center"><#if eachOrder.orderId=="Total"><font color="Blue"> <b></#if>${eachOrder.shipDate?if_exists}<#if eachOrder.orderId=="Total"></b></font></#if></td>
              	<td align="right"><#if eachOrder.orderId=="Total"><font color="Blue"> <b></#if>${eachOrder.noOfShipments?if_exists}<#if eachOrder.orderId=="Total"></b></font></#if></td>
              	<td align="right"><#if eachOrder.orderId=="Total"><font color="Blue"> <b></#if>${eachOrder.shipedQty?if_exists}<#if eachOrder.orderId=="Total"></b></font></#if></td>
              	
             <#-- >	<#if eachOrder.orderId=="Total">
              		<#assign diffDays=eachOrder.diffDays/records>
              		<#assign records=0>
              	<#else>
              		<#assign diffDays=eachOrder.diffDays>
              	</#if> -->
              	<td align="right">${eachOrder.diffDays?if_exists} <#if eachOrder.diffDays?has_content>Days </#if> </td>
            </tr>
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Orders Found</h3>
</#if>
