<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".2in">
                <fo:region-body margin-top=".9in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "MilkReceipt.txt")}
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
    	<#assign recptNo =1> 
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="5pt">
			 	<fo:block font-size="5pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block>
                <fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">.                                                                 MILK PRODUCTS FACTORY, HYDERABAD.   </fo:block>
                <fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">.                                                            MILK RECEIPTS FROM THE UINTS 0N ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")}  </fo:block>
				<fo:block font-size="5pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">&#160;                                                     &#160;&#160;&#160;:              A S    P E R    D E S P A T C H                                   :  A S    P E R  A C K N O W L E D G E M E N T          :                  </fo:block>
				<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">&#160;                                                     &#160;&#160;&#160;:  ------------------------------------------------------------------------------:--------------------------------------------------------:-----------------------------------------------</fo:block>
				<fo:block text-align="left"  keep-together="always" white-space-collapse="false" font-size="5pt">&#160; RC     DATE      MCC    MCC/DAIRY      &#160;&#160;&#160;RECT	  TANKE  : D	   T    C   TYPE    		 				                                         T   C  S :   T                                         T    C  C  :</fo:block>
				<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">.  NO    		        CODE    NAME           &#160;&#160;&#160;NO      NO.  : A    I    E	  OF   QUANTITY   QUANTITY     FAT   SNF    CLR   ACI-    E   O  O :   I   QUANTITY   FAT  SNF    CLR    ACI-    E    O  A  :</fo:block>
				<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">&#160;                                                     &#160;&#160;&#160;: T     M    L   MILK   LTS.      KGS          %    %             DITY   M   B  D :   M     LTS.     %     %            DITY    M    B  P  :    KGS     KG FAT    KG SNF    MLK   DIFF.QTY</fo:block>
				<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">&#160;                                                     &#160;&#160;&#160;: E     E    L                                                           P     A :    E                                         P        :                                  TYP  (Desp-Ack)</fo:block>
				<fo:block font-size="5pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 
						<fo:block font-family="Courier,monospace" font-size="5pt">
						 	<fo:table>
						 		<fo:table-column column-width="20pt"/>
           						<fo:table-column column-width="38pt"/>
       							<fo:table-column column-width="0pt"/>
       							<fo:table-column column-width="20pt"/>
           						<fo:table-column column-width="68pt"/>
           						<fo:table-column column-width="0pt"/>
           						<fo:table-column column-width="26pt"/>
       							<fo:table-column column-width="13pt"/>
           						<fo:table-column column-width="18pt"/>
           						<fo:table-column column-width="15pt"/>
           						<fo:table-column column-width="16pt"/> 
       							<fo:table-column column-width="32pt"/>
       							<fo:table-column column-width="31pt"/>
           						<fo:table-column column-width="20pt"/>
           						<fo:table-column column-width="19pt"/>
           						<fo:table-column column-width="23pt"/>
           						<fo:table-column column-width="17pt"/>
           						<fo:table-column column-width="18pt"/>
           						<fo:table-column column-width="11pt"/>
           						<fo:table-column column-width="10pt"/>
           						<fo:table-column column-width="20pt"/>
           						<fo:table-column column-width="31pt"/>
           						<fo:table-column column-width="22pt"/>
           						<fo:table-column column-width="18pt"/>
           						<fo:table-column column-width="20pt"/>
           						<fo:table-column column-width="18pt"/>
           						<fo:table-column column-width="18pt"/>   
           						<fo:table-column column-width="11pt"/>
           						<fo:table-column column-width="5pt"/>
           						<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="30pt"/>
           						<fo:table-column column-width="30pt"/>
           						<fo:table-column column-width="12pt"/>
           						<fo:table-column column-width="30pt"/>
           						<fo:table-body>  
           						<#assign rowCount = 40>
           							<fo:table-row>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">&#160;</fo:block>
           								</fo:table-cell>
           								</fo:table-row>
           								<fo:table-row>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left"></fo:block>
           								</fo:table-cell>
                   					</fo:table-row>
                   					<#assign sno=0>
                   					<#assign nextIndex=0>
								    <#assign previous = "none">
								    <#assign nextTranId = "none">
								    <#assign totMlkQty=0>
							     	<#assign totMlkRecQty=0>
								   
								    <#assign totMlkQkg=0>
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
								  	<#assign facilityAttribute = delegator.findOne("FacilityAttribute", {"facilityId" :facilityDetails.get("parentFacilityId"),"attrName":"enableQuantityKgs"}, true)>	
								  	<#assign sno=sno+1>
								  	<#if (sno>48)>
								  		<#if current!= previous>
           									<#if previous!="none">
										  		<fo:table-row>
									     			<fo:table-cell>	          				
									           			<fo:block font-family="Courier,monospace" font-size="10pt" break-before="page"/>     
									           		</fo:table-cell>
												</fo:table-row>
											</#if>
										</#if>	
										<#assign sno=0>
								  	</#if>
                   					<fo:table-row>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">${milkDetail.get('milkTransferId')?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "dd/MM/yyyy")} </fo:block>
           								</fo:table-cell>
           								<#assign shedDetails = delegator.findOne("Facility", {"facilityId" : facilityDetails.parentFacilityId}, true)>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" >${facilityDetails.mccCode?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("facilityName")?if_exists)),20)}</fo:block>
           								</fo:table-cell>
           									<#if current!= previous>
           										<#if previous!="none">
           											<#assign recptNo= recptNo+1>
           										</#if>
           									</#if>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">${recptNo}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("vehicleId")?if_exists} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "d")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><#if milkDetail.get("sendTime")?exists>${milkDetail.get("sendTime")?if_exists}</#if></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><#if milkDetail.get("cellType")?exists>${milkDetail.get("cellType")?if_exists}</#if></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendProductId")?if_exists}${milkDetail.get("milkCondition")?if_exists}</fo:block>
           								</fo:table-cell>
           								<#assign qty=0>
           								<#assign qty=milkDetail.get("quantityLtrs")>
           								<#if (facilityAttribute.get("attrValue"))=="Y">
           									<#assign qty=milkDetail.get("quantity")>           								
           								</#if>
           								 <#assign totMlkQty=totMlkQty+(qty)> 
           								<#assign facilityAttribute=0> 
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${qty?if_exists?string("##0.0")} </fo:block>
           								</fo:table-cell>
           								 <#assign totMlkQkg=totMlkQkg+milkDetail.get("quantity")>
           								<fo:table-cell>
           								 <fo:block keep-together="always" text-align="right">${milkDetail.get("quantity")?if_exists?string("##0.0")}</fo:block>
           								 </fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("fat")?if_exists?string("##0.00")} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"> ${milkDetail.get("snf")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign sendLrStr= '0.00'>
           									<#if (milkDetail.get("sendLR"))?has_content >
           										<#assign sendLrStr= milkDetail.get("sendLR")>
           									</#if>	
           									<fo:block keep-together="always" text-align="right">${sendLrStr?string("##0.0")} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign sendAcidityStr= '0.00'>
           									<#if (milkDetail.get("sendAcidity"))?has_content>
           										<#assign sendAcidityStr= milkDetail.get("sendAcidity")>
           									</#if>
           									<fo:block keep-together="always" text-align="right">${sendAcidityStr} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign sendTemparatureStr= '0.0'>
           									<#if (milkDetail.get("sendTemparature"))?has_content >
           										<#assign sendTemparatureStr= milkDetail.get("sendTemparature")>
           									</#if>
           									<fo:block keep-together="always" text-align="right">${sendTemparatureStr?string('#0.0')} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendCob")?if_exists} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><#if milkDetail.get("sendSoda")?exists>${milkDetail.get("sendSoda")?if_exists}</#if></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><#if milkDetail.get("ackTime")?exists>${milkDetail.get("ackTime")?if_exists}</#if></fo:block>
           								</fo:table-cell>
           								 <#assign totMlkRecQty=totMlkRecQty+milkDetail.get("receivedQuantityLtrs")> 
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"> ${milkDetail.get("receivedQuantityLtrs")?if_exists?string("##0.0")} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedFat")?if_exists?string("##0.00")} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"> ${milkDetail.get("receivedSnf")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign receivedLRStr= '0.00'>
           									<#if (milkDetail.get("sendTemparature"))?has_content>
           										<#assign receivedLRStr= milkDetail.get("receivedLR")>
           									</#if>
           									<fo:block keep-together="always" text-align="right">${receivedLRStr?string("##0.0")} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign receivedAcidityStr= '0.00'>
           									<#if (milkDetail.get("receivedAcidity"))?has_content>
           										<#assign receivedAcidityStr= milkDetail.get("receivedAcidity")>
           									</#if>
           									
           									<fo:block keep-together="always" text-align="right">${receivedAcidityStr} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign receivedTemparatureStr= '0.00'>
           									<#if (milkDetail.get("receivedTemparature"))?has_content >
           										<#assign receivedTemparatureStr= milkDetail.get("receivedTemparature")>
           									</#if>
           									<fo:block keep-together="always" text-align="right">${receivedTemparatureStr?string('#0.0')} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedCob")?if_exists} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("capacity")?if_exists} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedQuantity")?if_exists?string("##0.0")} </fo:block>
           								</fo:table-cell>totFat
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedKgFat")?if_exists?string("##0.00")} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedKgSnf")?if_exists?string("##0.00")} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><#if milkDetail.get("milkCondition")?exists>${milkDetail.get("milkCondition")}</#if></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
										 <#if  previous != current>
										 	<#assign previous=current >
										 </#if>
										 <#if nextTranId != current>
											<fo:block keep-together="always" text-align="right">${(monthTotals.get("recd")-monthTotals.get("send"))?if_exists?string("##0.0")} </fo:block>
										 
										 <#else>
   											<fo:block keep-together="always" text-align="right">&#160;</fo:block>
   										 </#if>
           								</fo:table-cell>
                   					</fo:table-row>
                   					</#if>
                   					</#list>
                   					<fo:table-row>
                   						<fo:table-cell>
                   							<fo:block font-size="5pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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
           									<fo:block></fo:block>
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
           									<fo:block keep-together="always" text-align="right">${totMlkQty?if_exists?string("##0.0")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           								 <fo:block keep-together="always" text-align="right">${totMlkQkg?if_exists?string("##0.0")}</fo:block>
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
           									<fo:block keep-together="always" text-align="right">${totMlkRecQty?if_exists?string("##0.0")}</fo:block>
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
           									<fo:block keep-together="always" text-align="right">${totalQty?if_exists?string("##0.0")}</fo:block>
           								</fo:table-cell>totFat
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${totKgFat?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${totKgSnf?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">&#160;</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
   											<fo:block keep-together="always" text-align="right">&#160; </fo:block>
           								</fo:table-cell>
                   					</fo:table-row>
                   					<fo:table-row>
                   						<fo:table-cell>
                   							<fo:block font-size="5pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                   						</fo:table-cell>
                   					</fo:table-row>
                   				</fo:table-body>
        					</fo:table>
        				</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="10pt">&#160;                                                                         </fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="10pt">&#160;                                                                         </fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="10pt">&#160;                                                                         </fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="10pt">&#160;                                                                         </fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="10pt">&#160;                          ENTERED BY                            CHECKED BY                           CORRECTED </fo:block>
                <fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="10pt">&#160;                             (MIS)                             (PRODUCTION)                             (MIS)    </fo:block>    				
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
