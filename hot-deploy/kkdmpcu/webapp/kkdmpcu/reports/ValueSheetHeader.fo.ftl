<#macro trucksheetHeader  estimatedDeliveryDate boothId dctx facilityTypeId>
<fo:page-sequence master-reference="main" force-page-count="no-force">						
			<fo:static-content flow-name="xsl-region-before">				
				<#if facilityTypeId == "BOOTH">
					<#assign boothDetails=Static["org.ofbiz.network.NetworkServices"].getBoothDetails( dctx,Static["org.ofbiz.base.util.UtilMisc"].toMap("boothId",boothId))>
					<#assign booths=boothDetails.get("boothDetails")/>	
				</#if>	
				<fo:block text-align="left" white-space-collapse="false">.                              KRISHNAVENI KKDMPCU LTD: VIJAYAWADA</fo:block>          		
              	  <fo:block>
              	 		<fo:table width="100%" space-after="0.0in" height="50pt">
              	 			 <fo:table-column column-width="60pt"/>
              	 			 <fo:table-column column-width="250pt"/>
              	 			 <fo:table-column column-width="60pt"/>
              	 			 <fo:table-body>
              	 			 	<fo:table-row>
              	 			 		<fo:table-cell>
              	 			 			<fo:block> ${uiLabelMap.CommonPage}:<fo:page-number/> </fo:block>		               
            						</fo:table-cell>
            					</fo:table-row>
              	 			 	<fo:table-row>
              	 			 		<fo:table-cell>
              	 			 			<fo:block keep-together="always" white-space-collapse="false">ZONE :${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(booths.get("zoneName").toUpperCase())),19)}</fo:block>		               
            						</fo:table-cell>
            					</fo:table-row>          	 			 	
              	 			 	<fo:table-row width="100%">
              	 			 		<fo:table-cell width="20%">
              	 			 			<fo:block white-space-collapse="false" keep-together="always">ROUTE :${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(booths.get("routeId"))),5)}           VALUE SHEET PRODUCT WISE           DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}  </fo:block>		               
            						</fo:table-cell>
            					</fo:table-row>
              	 			 </fo:table-body>
              	 		</fo:table>
              	  </fo:block>
              	 </fo:static-content> 
			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">	
			
	</#macro>			