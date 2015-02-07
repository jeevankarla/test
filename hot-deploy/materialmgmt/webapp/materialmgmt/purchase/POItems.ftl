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

<#if orderHeader?has_content>
    <div class="screenlet">
        <div class="screenlet-title-bar">
            <ul>
                <li class="h3">&nbsp;${uiLabelMap.OrderOrderItems}</li>
            </ul>
            <br class="clear" />
        </div>
        <div class="screenlet-body">
            <table class="order-items basic-table" cellspacing='0'>
                <tr valign="bottom" class="header-row">
                    <td width="30%">${uiLabelMap.Material}</td>
                    <td width="30%">Material Specification</td>
                    <td width="14%" align="left">${uiLabelMap.CommonStatus}</td>
                    <td width="5%" align="left">Qty</td>
                     <td width="10%" align="right">U.Price</td>
                    <#-->
                    <td width="10%" align="center">${uiLabelMap.OrderUnitList}</td>-->
                  <#--  <td width="10%" align="right">${uiLabelMap.OrderAdjustments}</td> -->
                   <td width="5%" align="right">Ed</td>
                    <td width="6%" align="right">VAT</td>
                    <td width="6%" align="right">CST</td>
                    <td width="15%" align="right">${uiLabelMap.OrderSubTotal}</td>
                    <td width="2%">&nbsp;</td>
                </tr>
                 <#assign orderExTaxTotal = (Static["java.math.BigDecimal"].ZERO)>
                <#if !orderItemList?has_content>
                    <tr>
                        <td colspan="7">
                            <font color="red">${uiLabelMap.checkhelper_sales_order_lines_lookup_failed}</font>
                        </td>
                    </tr>
                <#else>
                    <#assign itemClass = "2">
                    <#list orderItemList as orderItem>
                        <#assign orderItemContentWrapper = Static["org.ofbiz.order.order.OrderContentWrapper"].makeOrderContentWrapper(orderItem, request)>
                        <#assign orderItemShipGrpInvResList = orderReadHelper.getOrderItemShipGrpInvResList(orderItem)>
                        <#if orderHeader.orderTypeId == "SALES_ORDER"><#assign pickedQty = orderReadHelper.getItemPickedQuantityBd(orderItem)></#if>
                        <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                            <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
                            <#assign productId = orderItem.productId?if_exists>
                             <#assign productDetails = delegator.findOne("Product", {"productId" : productId}, true)>
                            <#if productId?exists && productId == "shoppingcart.CommentLine">
                                <td colspan="7" valign="top" class="label"> &gt;&gt; ${orderItem.itemDescription}</td>
                            <#else>
                                <td>
                                    <div class="order-item-description">
                                        <#if productDetails?has_content?exists>
                                            ${productDetails.get("productId")?if_exists} - ${productDetails.get("productName")?if_exists} 
                                           <#if (product.salesDiscontinuationDate)?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(product.salesDiscontinuationDate)>
                                                <br />
                                                <span style="color: red;">${uiLabelMap.OrderItemDiscontinued}: ${product.salesDiscontinuationDate}</span>
                                            </#if> 
                                        <#elseif orderItemType?exists>
                                            ${orderItemType.description} - ${orderItem.itemDescription?if_exists}
                                        <#else>
                                            ${orderItem.itemDescription?if_exists}
                                        </#if>
                                    </div>
                                    <div style="float:right;">
                                        <#if orderItemContentWrapper.get("IMAGE_URL")?has_content>
                                            <a href="<@ofbizUrl>viewimage?orderId=${orderId}&amp;orderItemSeqId=${orderItem.orderItemSeqId}&amp;orderContentTypeId=IMAGE_URL</@ofbizUrl>"
                                               target="_orderImage" class="buttontext">${uiLabelMap.OrderViewImage}</a>
                                        </#if>
                                    </div>
                                </td>
                                 <td align="left">
                                 <#if productDetails?has_content?exists>
                                  ${productDetails.longDescription?if_exists}
                                 </#if>
                                </td>
                                <td align="left"><#assign statusDesc = delegator.findOne("StatusItem", {"statusId" : orderItem.statusId}, true) />${statusDesc.description?if_exists}
                                </td>
                                <td align="left">${orderItem.quantity}
                                </td>
                                <td align="right"  nowrap="nowrap">
                                    <@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/>
                                </td>
                                <#assign exciseAmount=0>
                                <#if orderItem.bedAmount?exists > <#assign exciseAmount=exciseAmount+orderItem.bedAmount >  </#if>
                                <#if orderItem.bedcessAmount?exists> <#assign exciseAmount=exciseAmount+orderItem.bedcessAmount>  </#if>
                                <#if orderItem.bedseccessAmount?exists> <#assign exciseAmount=exciseAmount+orderItem.bedseccessAmount>  </#if>
                                <td align="right"  nowrap="nowrap">
                                   <@ofbizCurrency amount=exciseAmount isoCode=currencyUomId/>
                                </td>
                                <#-->
                                <td align="right"  nowrap="nowrap">
                                    <@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/>
                                    / <@ofbizCurrency amount=orderItem.unitListPrice isoCode=currencyUomId/>
                                </td>-->
                               <#-- <td align="right" valign="top" nowrap="nowrap">
                                   <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getPurchaseOrderItemTaxTotal(orderItem) isoCode=currencyUomId/>
                                </td> -->
                                <#assign orderItemAdjs = delegator.findOne("OrderItem", {"orderId" : orderItem.orderId,"orderItemSeqId": orderItem.orderItemSeqId}, true) />
								<td align="right" valign="top" nowrap="nowrap">
                                   <@ofbizCurrency amount=orderItemAdjs.vatAmount isoCode=currencyUomId/>
                                </td>
								<td align="right" valign="top" nowrap="nowrap">
                                   <@ofbizCurrency amount=orderItemAdjs.cstAmount isoCode=currencyUomId/>
                                </td>
                                <td align="right" valign="top" nowrap="nowrap">
                                    <#if orderItem.statusId != "ITEM_CANCELLED">
                                         <#assign  itemSubTotal=Static["org.ofbiz.order.order.OrderReadHelper"].getPurchaseOrderItemTotal(orderItem,false)>
                                         <#assign  orderExTaxTotal=orderExTaxTotal.add(itemSubTotal)>
                                        <@ofbizCurrency amount=itemSubTotal isoCode=currencyUomId/>
                                    <#else>
                                        <@ofbizCurrency amount=0.00 isoCode=currencyUomId/>
                                    </#if>
                                </td>
                            </#if>
                        </tr>
                   <#--     <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                            <#if productId?exists && productId == "shoppingcart.CommentLine">
                                <td colspan="7" valign="top" class="label"> &gt;&gt; ${orderItem.itemDescription}</td>
                            <#else>
                                <td valign="top">
                                    <#if productId?has_content>
                                        <#assign product = orderItem.getRelatedOneCache("Product")>
                                    </#if>
                                    <#if productId?exists> -->
                                        <#-- INVENTORY -->
                                   <#--     <#if (orderHeader.statusId != "ORDER_COMPLETED") && availableToPromiseMap?exists && quantityOnHandMap?exists && availableToPromiseMap.get(productId)?exists && quantityOnHandMap.get(productId)?exists>
                                            <#assign quantityToProduce = 0>
                                            <#assign atpQuantity = availableToPromiseMap.get(productId)?default(0)>
                                            <#assign qohQuantity = quantityOnHandMap.get(productId)?default(0)>
                                            <#assign mktgPkgATP = mktgPkgATPMap.get(productId)?default(0)>
                                            <#assign mktgPkgQOH = mktgPkgQOHMap.get(productId)?default(0)>
                                            <#assign requiredQuantity = requiredProductQuantityMap.get(productId)?default(0)>
                                            <#assign onOrderQuantity = onOrderProductQuantityMap.get(productId)?default(0)>
                                            <#assign inProductionQuantity = productionProductQuantityMap.get(productId)?default(0)>
                                            <#assign unplannedQuantity = requiredQuantity - qohQuantity - inProductionQuantity - onOrderQuantity - mktgPkgQOH>
                                            <#if unplannedQuantity < 0><#assign unplannedQuantity = 0></#if>
                                        </#if>
                                    </#if>
                                </td> -->
                                <#-- now show status details per line item -->
                              <#--  <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem")>
                                <td  valign="top">
                                    <#assign returns = orderItem.getRelated("ReturnItem")?if_exists>
                                    <#if returns?has_content>
                                        <#list returns as returnItem>
                                            <#assign returnHeader = returnItem.getRelatedOne("ReturnHeader")>
                                            <#if returnHeader.statusId != "RETURN_CANCELLED">
                                                <font color="red">${uiLabelMap.OrderReturned}</font>
                                                ${uiLabelMap.CommonNbr}<a href="<@ofbizUrl>returnMain?returnId=${returnItem.returnId}</@ofbizUrl>" class="buttontext">${returnItem.returnId}</a>
                                            </#if>
                                        </#list>
                                    </#if>
                                </td> -->
                                <#-- QUANTITY -->
                            <#--    <td align="right" valign="top" nowrap="nowrap">
                                </td>
                                <td align="right" valign="top" nowrap="nowrap">
                                    <@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/>
                                    / <@ofbizCurrency amount=orderItem.unitListPrice isoCode=currencyUomId/>
                                </td>
                                <td align="right" valign="top" nowrap="nowrap">
                                    <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false) isoCode=currencyUomId/>
                                </td>
                                <td align="right" valign="top" nowrap="nowrap">
                                    <#if orderItem.statusId != "ITEM_CANCELLED">
                                        <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments) isoCode=currencyUomId/>
                                    <#else>
                                        <@ofbizCurrency amount=0.00 isoCode=currencyUomId/>
                                    </#if>
                                </td>
                                <td>&nbsp;</td>
                            </#if>
                        </tr> -->
                        <#-- show info from workeffort -->
                        
                        <#-- show linked order lines -->
                                               <#-- show linked quote -->
                   <#--     <#assign linkedQuote = orderItem.getRelatedOneCache("QuoteItem")?if_exists>
                        <#if linkedQuote?has_content>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td>&nbsp;</td>
                                <td colspan="6">
                                    <span class="label">${uiLabelMap.OrderLinkedToQuote}</span>&nbsp;
                                    <a href="<@ofbizUrl>EditQuoteItem?quoteId=${linkedQuote.quoteId}&amp;quoteItemSeqId=${linkedQuote.quoteItemSeqId}</@ofbizUrl>"
                                       class="buttontext">${linkedQuote.quoteId}-${linkedQuote.quoteItemSeqId}</a>&nbsp;
                                </td>
                            </tr>
                        </#if> -->
                        <#-- now show adjustment details per line item -->
                   <#--     <#assign orderItemAdjustmentsAll = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)>
                        <#assign orderItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].fetchNonTaxAdjustments(orderItemAdjustmentsAll)>
 -->
                        
                        <#-- now show price info per line item -->
                        
                        <#-- now show survey information per line item -->
                <#--        <#assign orderItemSurveyResponses = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSurveyResponse(orderItem)>
                        <#if orderItemSurveyResponses?exists && orderItemSurveyResponses?has_content>
                            <#list orderItemSurveyResponses as survey>
                                <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                    <td align="right" colspan="2">
                                        <span class="label">${uiLabelMap.CommonSurveys}</span>&nbsp;
                                        <a href="/content/control/ViewSurveyResponses?surveyResponseId=${survey.surveyResponseId}&amp;surveyId=${survey.surveyId}&amp;externalLoginKey=${externalLoginKey}"
                                           class="buttontext">${survey.surveyId}</a>
                                    </td>
                                    <td colspan="5">&nbsp;</td>
                                </tr>
                            </#list>
                        </#if> -->
                        <#-- display the ship estimated/before/after dates -->
                     <#--   <#if orderItem.estimatedShipDate?exists>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td align="right" colspan="2">
                                    <span class="label">${uiLabelMap.OrderEstimatedShipDate}</span>&nbsp;${orderItem.estimatedShipDate?string.short}
                                </td>
                                <td colspan="5">&nbsp;</td>
                            </tr>
                        </#if>
                        <#if orderItem.estimatedDeliveryDate?exists>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td align="right" colspan="2">
                                    <span class="label">${uiLabelMap.OrderOrderQuoteEstimatedDeliveryDate}</span>&nbsp;${orderItem.estimatedDeliveryDate?string.short}
                                </td>
                                <td colspan="5">&nbsp;</td>
                            </tr>
                        </#if>
                        <#if orderItem.shipAfterDate?exists>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td align="right" colspan="2">
                                    <span class="label">${uiLabelMap.OrderShipAfterDate}</span>&nbsp;${orderItem.shipAfterDate?string.short}
                                </td>
                                <td colspan="5">&nbsp;</td>
                            </tr>
                        </#if>
                        <#if orderItem.shipBeforeDate?exists>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td align="right" colspan="2">
                                    <span class="label">${uiLabelMap.OrderShipBeforeDate}</span>&nbsp;${orderItem.shipBeforeDate?string.short}
                                </td>
                                <td colspan="5">&nbsp;</td>
                            </tr>
                        </#if> -->
                        <#-- now show ship group info per line item -->
                   <#--     <#assign orderItemShipGroupAssocs = orderItem.getRelated("OrderItemShipGroupAssoc")?if_exists>
                        <#if orderItemShipGroupAssocs?has_content>
                            <#list orderItemShipGroupAssocs as shipGroupAssoc>
                                <#assign shipGroup = shipGroupAssoc.getRelatedOne("OrderItemShipGroup")>
                                <#assign shipGroupAddress = shipGroup.getRelatedOne("PostalAddress")?if_exists>
                                <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                    <td align="right" colspan="2">
                                        <span class="label">${uiLabelMap.OrderShipGroup}</span>&nbsp;[${shipGroup.shipGroupSeqId}]
                                        ${shipGroupAddress.address1?default("${uiLabelMap.OrderNotShipped}")}
                                    </td>
                                    <td align="center">
                                        ${shipGroupAssoc.quantity?string.number}&nbsp;
                                    </td>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                            </#list>
                        </#if> -->
                        <#-- now show inventory reservation info per line item -->
                     <#--   <#if orderItemShipGrpInvResList?exists && orderItemShipGrpInvResList?has_content>
                            <#list orderItemShipGrpInvResList as orderItemShipGrpInvRes>
                                <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                    <td align="right" colspan="2">
                                        <span class="label">${uiLabelMap.CommonInventory}</span>&nbsp;
                                        <a href="/facility/control/EditInventoryItem?inventoryItemId=${orderItemShipGrpInvRes.inventoryItemId}&amp;externalLoginKey=${externalLoginKey}"
                                           class="buttontext">${orderItemShipGrpInvRes.inventoryItemId}</a>
                                        <span class="label">${uiLabelMap.OrderShipGroup}</span>&nbsp;${orderItemShipGrpInvRes.shipGroupSeqId}
                                    </td>
                                    <td align="center">
                                        ${orderItemShipGrpInvRes.quantity?string.number}&nbsp;
                                    </td>
                                    <td>
                                        <#if (orderItemShipGrpInvRes.quantityNotAvailable?has_content && orderItemShipGrpInvRes.quantityNotAvailable > 0)>
                                            <span style="color: red;">
                                                [${orderItemShipGrpInvRes.quantityNotAvailable?string.number}&nbsp;${uiLabelMap.OrderBackOrdered}]
                                            </span>
                                            <#--<a href="<@ofbizUrl>balanceInventoryItems?inventoryItemId=${orderItemShipGrpInvRes.inventoryItemId}&amp;orderId=${orderId}&amp;priorityOrderId=${orderId}&amp;priorityOrderItemSeqId=${orderItemShipGrpInvRes.orderItemSeqId}</@ofbizUrl>" class="buttontext" style="font-size: xx-small;">Raise Priority</a> -->
                                <#--        </#if>
                                        &nbsp;
                                    </td>
                                    <td colspan="3">&nbsp;</td>
                                </tr>
                            </#list> -->
                      <#--  </#if> -->
                        <#-- now show planned shipment info per line item -->
                      <#--  <#assign orderShipments = orderItem.getRelated("OrderShipment")?if_exists>
                        <#if orderShipments?has_content>
                            <#list orderShipments as orderShipment>
                                <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                    <td align="right" colspan="2">
                                        <span class="label">${uiLabelMap.OrderPlannedInShipment}</span>&nbsp;<a
                                            target="facility"
                                            href="/facility/control/ViewShipment?shipmentId=${orderShipment.shipmentId}&amp;externalLoginKey=${externalLoginKey}"
                                            class="buttontext">${orderShipment.shipmentId}</a>: ${orderShipment.shipmentItemSeqId}
                                    </td>
                                    <td align="center">
                                        ${orderShipment.quantity?string.number}&nbsp;
                                    </td>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                            </#list>
                        </#if> -->
                        <#-- now show item issuances (shipment) per line item -->
                      <#--  <#assign itemIssuances = itemIssuancesPerItem.get(orderItem.get("orderItemSeqId"))?if_exists>
                        <#if itemIssuances?has_content>
                            <#list itemIssuances as itemIssuance>
                            <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                <td align="right" colspan="2">
                                    <#if itemIssuance.shipmentId?has_content>
                                        <span class="label">${uiLabelMap.OrderIssuedToShipmentItem}</span>&nbsp;
                                        <a target="facility"
                                           href="/facility/control/ViewShipment?shipmentId=${itemIssuance.shipmentId}&amp;externalLoginKey=${externalLoginKey}"
                                           class="buttontext">${itemIssuance.shipmentId}</a>: ${itemIssuance.shipmentItemSeqId?if_exists}
                                    <#else>
                                        <span class="label">${uiLabelMap.OrderIssuedWithoutShipment}</span>
                                    </#if>
                                </td>
                                <td align="center">
                                    ${itemIssuance.quantity?default(0) - itemIssuance.cancelQuantity?default(0)}&nbsp;
                                </td>
                                <td colspan="4">&nbsp;</td>
                            </tr>
                            </#list>
                        </#if> -->
                        <#-- now show item issuances (inventory item) per line item -->
                     <#--   <#if itemIssuances?has_content>
                            <#list itemIssuances as itemIssuance>
                                <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                    <td align="right" colspan="2">
                                        <#if itemIssuance.inventoryItemId?has_content>
                                            <#assign inventoryItem = itemIssuance.getRelatedOne("InventoryItem")/>
                                            <span class="label">${uiLabelMap.CommonInventory}</span>
                                            <a href="/facility/control/EditInventoryItem?inventoryItemId=${itemIssuance.inventoryItemId}&amp;externalLoginKey=${externalLoginKey}"
                                               class="buttontext">${itemIssuance.inventoryItemId}</a>
                                            <span class="label">${uiLabelMap.OrderShipGroup}</span>&nbsp;${itemIssuance.shipGroupSeqId?if_exists}
                                            <#if (inventoryItem.serialNumber?has_content)>
                                                <br />
                                                <span class="label">${uiLabelMap.ProductSerialNumber}</span>&nbsp;${inventoryItem.serialNumber}&nbsp;
                                            </#if>
                                        </#if>
                                    </td>
                                    <td align="center">
                                        ${itemIssuance.quantity?default(0) - itemIssuance.cancelQuantity?default(0)}
                                    </td>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                            </#list>
                        </#if> -->
                        <#-- now show shipment receipts per line item -->
                     <#--   <#assign shipmentReceipts = orderItem.getRelated("ShipmentReceipt")?if_exists>
                        <#if shipmentReceipts?has_content>
                            <#list shipmentReceipts as shipmentReceipt>
                                <tr<#if itemClass == "1"> class="alternate-row"</#if>>
                                    <td align="right" colspan="2">
                                        <#if shipmentReceipt.shipmentId?has_content>
                                            <span class="label">${uiLabelMap.OrderShipmentReceived}</span>&nbsp;
                                            <a target="facility"
                                               href="/facility/control/ViewShipment?shipmentId=${shipmentReceipt.shipmentId}&amp;externalLoginKey=${externalLoginKey}"
                                               class="buttontext">${shipmentReceipt.shipmentId}</a>:${shipmentReceipt.shipmentItemSeqId?if_exists}
                                        </#if>
                                        &nbsp;${shipmentReceipt.datetimeReceived}&nbsp;
                                        <span class="label">${uiLabelMap.CommonInventory}</span>&nbsp;
                                        <a href="/facility/control/EditInventoryItem?inventoryItemId=${shipmentReceipt.inventoryItemId}&amp;externalLoginKey=${externalLoginKey}"
                                           class="buttontext">${shipmentReceipt.inventoryItemId}</a>
                                    </td>
                                    <td align="center">
                                        ${shipmentReceipt.quantityAccepted?string.number}&nbsp;/&nbsp;${shipmentReceipt.quantityRejected?default(0)?string.number}
                                    </td>
                                    <td colspan="4">&nbsp;</td>
                                </tr> -->
                         <#--   </#list>
                        </#if>
                        <#if itemClass == "2">
                            <#assign itemClass = "1">
                        <#else>
                            <#assign itemClass = "2">
                        </#if> -->
                    </#list>
                </#if>
                <tr><td colspan="9"><hr /></td></tr>
                <#list orderHeaderAdjustments as orderHeaderAdjustment>
                    <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType")>
                    <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
                    <#if adjustmentAmount != 0>	
                        <tr>
                            <td align="right" colspan="7">
                                <#if orderHeaderAdjustment.comments?has_content>${orderHeaderAdjustment.comments} - </#if>
                                <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description} - </#if>
                                <span class="label"> ${adjustmentType.get("description", locale)}</span>
                            </td>
                            <td align="right" nowrap="nowrap">
                                <@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId/>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                    </#if>
                </#list> 
                <#-- subtotal -->
               <tr>
                    <td colspan="2"></td>
                    <td colspan="7"><hr /></td>
                </tr>
                <tr>
                    <td align="right" colspan="7">
                        <span class="label">${uiLabelMap.OrderItemsSubTotal}</span>
                    </td>
                    <td align="right" nowrap="nowrap">
                    <@ofbizCurrency amount=orderExTaxTotal isoCode=currencyUomId/>
                    <#-->
                        <@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/> -->
                    </td>
                    <td>&nbsp;</td>
                </tr>
                <#-- other adjustments -->
               <tr>
                    <td align="right" colspan="7">
                        <span class="label">${uiLabelMap.OrderTotalOtherOrderAdjustments}</span>
                    </td>
                    <td align="right" nowrap="nowrap">
                        <@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId/>
                    </td>
                    <td>&nbsp;</td>
                </tr>
                <#-- shipping adjustments -->
                <tr>
                    <td align="right" colspan="7">
                        <span class="label">${uiLabelMap.OrderTotalShippingAndHandling}</span>
                    </td>
                    <td align="right" nowrap="nowrap">
                        <@ofbizCurrency amount=shippingAmount isoCode=currencyUomId/>
                    </td>
                    <td>&nbsp;</td>
                </tr>
                <#-- tax adjustments -->
                 <#assign orderItemAdjustmentsAll = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)>
                 <#assign orderItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].fetchNonTaxAdjustments(orderItemAdjustmentsAll)>
               <#assign taxAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].fetchTaxAdjustments(orderItemAdjustmentsAll)>
                <#if taxAdjustments?exists && taxAdjustments?has_content>
                <#list taxAdjustments as taxAdjustment>
                	<tr>
                    	<#assign adjustmentType = taxAdjustment.getRelatedOneCache("OrderAdjustmentType")>
                    	<td align="right" colspan="7">
                        	<span class="label">${adjustmentType.get("description",locale)} <#if taxAdjustment.sourcePercentage?has_content>(${taxAdjustment.sourcePercentage}%)</#if></span>
                    	</td>            			
            			<td align="right" nowrap="nowrap">
              				<@ofbizCurrency amount=taxAdjustment.amount isoCode=currencyUomId/>
            			</td>
                    	<td>&nbsp;</td>            			
          			</tr>            
          		</#list> 
          		</#if>   
                <#-- grand total -->
               <tr>
                    <td align="right" colspan="7">
                        <span class="label">${uiLabelMap.OrderTotalDue}</span>
                    </td>
                    <td align="right" nowrap="nowrap">
                        <@ofbizCurrency amount=grandTotal isoCode=currencyUomId/>
                    </td>
                    <td>&nbsp;</td>
                </tr> 
            </table>
        </div>
    </div>
</#if>
