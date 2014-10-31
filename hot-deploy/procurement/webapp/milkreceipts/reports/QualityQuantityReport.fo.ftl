<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".2in">
                <fo:region-body margin-top=".6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "QualityQuantityReport.txt")}
		<#if errorMessage?has_content>
			<fo:page-sequence master-reference="main">
   				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      				<fo:block font-size="14pt">
              			${errorMessage}.
   	  				</fo:block>
   				</fo:flow>
			</fo:page-sequence>        
		<#else>         
    		<#if milkDetailslist?has_content>
    			<fo:page-sequence master-reference="main">
					<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="5pt">
			 			<fo:block font-size="5pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block>
			 			<#assign unitDetails = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>
                		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">.     UNIT NAME :${unitDetails.get("facilityName")?if_exists}       PERIOD FROM : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate,"dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate,"dd/MM/yyyy")}  </fo:block>
						<fo:block font-size="5pt">---------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
						<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">D  :     TAN      TYPE    :        DESPATCHES              ACKNOWLEDGE      :     DIFFERENCE       : DESPATCH       ACKNOWL    :  EXCES   :</fo:block>
						<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">A  :     KER      OF      : -------------------------:-----------------------:---------------------:-------------:-------------:          :</fo:block>
						<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">Y  :     NO.      MILK    :      FAT        SNF     :    FAT        SNF     :    FAT      SNF      : QTY  LTS    :  QTY  LTS   :  SHORT   :</fo:block>
						<fo:block font-size="5pt">---------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					</fo:static-content>
					<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 
						<fo:block font-family="Courier,monospace" font-size="5pt">
						 	<fo:table>
						 		<fo:table-column column-width="5pt"/>
           						<fo:table-column column-width="35pt"/>
       							<fo:table-column column-width="40pt"/>
       							<fo:table-column column-width="31pt"/>
           						<fo:table-column column-width="33pt"/>
           						<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="36pt"/>
       							<fo:table-column column-width="38pt"/>
           						<fo:table-column column-width="30pt"/>
           						<fo:table-column column-width="46pt"/>
           						<fo:table-column column-width="43pt"/> 
       							<fo:table-column column-width="32pt"/>
           						<fo:table-column column-width="20pt"/>
           						<fo:table-column column-width="19pt"/>
           						<fo:table-body>  
                   					<#assign nextIndex=0>
								    <#assign previous = "none">
								    <#assign nextTranId = "none">
								    <#assign totMlkQty=0>
							     	<#assign totMlkRecQty=0>
								    <#assign totMlkQkg=0>
								    <#assign totMilkReceivedQty=0>
								    <#assign totalQty=0>
								    <#assign lineNumber = 0>
								    <#list milkDetailslist as milkDetail>
							    		<#assign currentIndex = milkDetailslist.indexOf(milkDetail)>
							    		<#assign current = milkDetail.get("milkTransferId")>
							    		<#assign nextIndex = currentIndex+1>
							    		<#if ( nextIndex < listSize ) >
							    			<#assign nextElement = milkDetailslist[nextIndex] >
							    			<#assign nextElementQty = nextElement.get("receivedQuantityLtrs")>
							    			<#if nextElementQty!=0>
							    				<#assign nextTranId = nextElement.get("milkTransferId")>
							    			<#else>
							    				<#assign nextIndex= nextIndex+1>
							    				<#if (nextIndex < listSize)>
							    					<#assign nextElement = milkDetailslist[nextIndex] >
							    					<#assign nextElementQty = nextElement.get("receivedQuantityLtrs")>
							    					<#if nextElementQty!=0>
											    		<#assign nextTranId = nextElement.get("milkTransferId")>
											   	 	<#else>
											    		<#assign nextIndex= nextIndex+1>
											    		<#if (nextIndex < listSize)>
											    			<#assign nextElement = milkDetailslist[nextIndex] >
											    			<#assign nextTranId = nextElement.get("milkTransferId")>
											    		<#else>
											    			<#assign nextTranId = "none">
											    		</#if>		
											    	</#if>		
							    				<#else>
							    					<#assign nextTranId = "none">
							    				</#if>		
							    			</#if>	
							    		<#else>
							    			<#assign nextTranId = "none">
							    		</#if>
								    	<#assign date = milkDetail.get("sendDate")>
										<#assign date1 = milkDetail.get("receiveDate")>
										<#assign monthTotals = finalQtyMap[milkDetail.get('milkTransferId')]>
										<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : milkDetail.get("facilityId")}, true)>	
								  		<#if (milkDetail.get("receivedQuantityLtrs"))!=0>
								  		<#assign lineNumber = lineNumber + 1>
								  			<#assign facilityAttribute = delegator.findOne("FacilityAttribute", {"facilityId" :facilityDetails.get("parentFacilityId"),"attrName":"enableQuantityKgs"}, true)>	
                   							<fo:table-row>
           										<fo:table-cell>
           											<fo:block keep-together="always" text-align="left"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(date?if_exists, "dd")} </fo:block>
           										</fo:table-cell>
           										<#assign shedDetails = delegator.findOne("Facility", {"facilityId" : facilityDetails.parentFacilityId}, true)>
           										<fo:table-cell>
           										<fo:block keep-together="always" text-align="right" text-indent="5pt">${milkDetail.get("vehicleId")?if_exists} </fo:block>
           										</fo:table-cell>
           										<#assign product = delegator.findOne("Product", {"productId" : milkDetail.get("sendProductId")}, true)>
           										<fo:table-cell>
           											<fo:block keep-together="always" text-align="left" text-indent="10pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim(product.get("productName"),8)?if_exists}</fo:block>
           										</fo:table-cell>
           										<#assign qty=0>
           										<#assign recqty=0>
           										<#assign receivedQty=0>
           										<#assign excess=0>
           										<#assign Difffat=0>
           										<#assign Diffsnf=0>
           										<#assign qty=milkDetail.get("quantityLtrs")>
           										<#if (facilityAttribute.get("attrValue"))=="Y">
           											<#assign qty=milkDetail.get("quantity")>           								
           										</#if>
           										<#assign recqty=milkDetail.get("receivedQuantityLtrs")>
           										<#if (facilityAttribute.get("attrValue"))=="Y">
       											 	<#assign recqty=milkDetail.get("receivedQuantity")>
           										</#if>
           										<#assign facilityAttribute=0> 
           										<#assign excess=(recqty)-(qty)>
           										<fo:table-cell>
           											<fo:block keep-together="always" text-align="right">${milkDetail.get("fat")?if_exists?string("##0.00")} </fo:block>
           										</fo:table-cell>
           										<fo:table-cell>
           											<fo:block keep-together="always" text-align="right"> ${milkDetail.get("snf")?if_exists?string("##0.00")}</fo:block>
           										</fo:table-cell>
           										<fo:table-cell>
           											<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedFat")?if_exists?string("##0.00")} </fo:block>
           										</fo:table-cell>
           										<fo:table-cell>
           											<fo:block keep-together="always" text-align="right"> ${milkDetail.get("receivedSnf")?if_exists?string("##0.00")}</fo:block>
           										</fo:table-cell>
           										<fo:table-cell>
           											<#assign Difffat=(milkDetail.get("fat"))-(milkDetail.get("receivedFat"))>
           											<fo:block keep-together="always" text-align="right"> ${Difffat?if_exists?string("##0.00")}</fo:block>
           										</fo:table-cell>
           										<fo:table-cell>
           											<#assign Diffsnf=(milkDetail.get("snf"))-(milkDetail.get("receivedSnf"))>
           											<fo:block keep-together="always" text-align="right"> ${Diffsnf?if_exists?string("##0.00")}</fo:block>
           										</fo:table-cell>
           										<fo:table-cell>
           											<#if  previous != current>
										 				<#assign previous=current >
										 			</#if>
										 			<#if nextTranId != current>
										 				<#assign qty=(qty+extraqty)>
           												<fo:block keep-together="always" text-align="right">${qty?if_exists?string("##0.00")} </fo:block>
           												<#assign totMlkQty=totMlkQty+(qty)>
           											<#else>
           												<#assign extraqty=0>
   														<fo:block keep-together="always" text-align="right">&#160;</fo:block>
   														<#assign extraqty=extraqty+qty>
   										 			</#if>
           										</fo:table-cell>
           										<fo:table-cell>
           											<#if  previous != current>
										 				<#assign previous=current >
										 			</#if>
										 			<#if nextTranId != current>
										 				<#assign recqty=(recqty+extrarecqty)>
           												<fo:block keep-together="always" text-align="right">${recqty?if_exists?string("##0.00")} </fo:block>
           												<#assign totMlkRecQty=totMlkRecQty+(recqty)>
           											<#else>
           												<#assign extrarecqty=0>
   														<fo:block keep-together="always" text-align="right">&#160;</fo:block>
   														<#assign extrarecqty=extrarecqty+recqty>
   										 			</#if>
           										</fo:table-cell>
           										<fo:table-cell>
           											<#if  previous != current>
										 				<#assign previous=current >
										 			</#if>
										 			<#if nextTranId != current>
										 				<#assign excess=(excess+extraexcess)>
           												<fo:block keep-together="always" text-align="right"> ${excess?if_exists?string("##0.00")}</fo:block>
           												<#assign totalQty=totalQty+excess>
           											<#else>
           												<#assign extraexcess=0>
   														<fo:block keep-together="always" text-align="right">&#160;</fo:block>
   														<#assign extraexcess=extraexcess+excess>
   										 			</#if>
           										</fo:table-cell>
           									</fo:table-row>
           									<fo:table-row>
		                    				<fo:table-cell >
		                    					<#if (lineNumber >= 50)>
		                    							<#assign lineNumber = 0 >
		                    							<fo:block page-break-after="always"></fo:block>
		                    					</#if>	
			                        		</fo:table-cell>
                    					</fo:table-row>
                   						</#if>
                   					</#list>
                   					<fo:table-row>
                   						<fo:table-cell>
                   							<fo:block font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                   						</fo:table-cell>
                   					</fo:table-row>
                   					<fo:table-row>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">&#160;</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">&#160;</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">&#160;</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">&#160;</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">&#160;</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">&#160;</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">&#160;</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">&#160;</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">&#160;</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${totMlkQty?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${totMlkRecQty?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${totalQty?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>totFat
           							</fo:table-row>
                   				<fo:table-row>
                   					<fo:table-cell>
                   						<fo:block font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                   					</fo:table-cell>
                   				</fo:table-row>
                   				
                   			</fo:table-body>
        				</fo:table>
        			</fo:block>
				</fo:flow>		
   			</fo:page-sequence>	
		<#else>
			<fo:page-sequence master-reference="main">
    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
       		 		<fo:block font-size="14pt">
            			${uiLabelMap.NoOrdersFound}.
       		 		</fo:block>
    			</fo:flow>
			</fo:page-sequence>
		</#if>
	</#if>
</fo:root>
</#escape>
