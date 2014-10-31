<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".1in" margin-top=".3in">
                <fo:region-body margin-top=".9in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "MilkReceiptsFairList.txt")}
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
				<fo:block  font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">&#160;                                                     &#160;&#160;&#160;:              A S    P E R    D E S P A T C H                                   :  A S    P E R  A C K N O W L E D G E M E N T          :                  </fo:block>
				<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">&#160;                                                     &#160;&#160;&#160;:  ------------------------------------------------------------------------------:--------------------------------------------------------:----------------------------------------------------</fo:block>
				<fo:block text-align="left"  keep-together="always" white-space-collapse="false" font-size="5pt"> 	RC     DATE      MCC    MCC/DAIRY      &#160;&#160;&#160;RECT	  TANKE : D	   T    C   TYPE    		 				                                           T   C  S : T                                             T   C  C  :</fo:block>
				<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">  NO    		        CODE    NAME           &#160;&#160;&#160;NO      NO..  : A   I    E	  OF   QUANTITY   QUANTITY     FAT   SNF      CLR   ACI-    E   O  O :  I    QUANTITY    FAT   SNF    CLR    ACI-    E   O  A  :</fo:block>
				<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">&#160;                                                    &#160;&#160;: T    M    L   MILK   LTS.      KGS          %    %               DITY   M   B  D :  M     LTS.       %     %             DITY    M   B  P  :    KGS     KG FAT         KG SNF    MLK   DIFF.QTY</fo:block>
				<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">&#160;                                                    &#160;&#160;: E    E    L                                                             P     A :  E                                             P       :                                       TYP  (Desp-Ack)</fo:block>
				<fo:block  font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 
						<fo:block font-family="Courier,monospace" font-size="5pt">
						 	<fo:table>
						 		<fo:table-column column-width="10pt"/>
           						<fo:table-column column-width="35pt"/>
       							<fo:table-column column-width="17pt"/>
       							<fo:table-column column-width="10pt"/>
           						<fo:table-column column-width="50pt"/>
           						<fo:table-column column-width="10pt"/>
           						<fo:table-column column-width="26pt"/>
       							<fo:table-column column-width="15pt"/>
           						<fo:table-column column-width="18pt"/>
           						<fo:table-column column-width="15pt"/>
           						<fo:table-column column-width="16pt"/> 
       							<fo:table-column column-width="35pt"/>
       							<fo:table-column column-width="38pt"/>
           						<fo:table-column column-width="20pt"/>
           						<fo:table-column column-width="19pt"/>
           						<fo:table-column column-width="23pt"/>
           						<fo:table-column column-width="17pt"/>
           						<fo:table-column column-width="18pt"/>
           						<fo:table-column column-width="11pt"/>
           						<fo:table-column column-width="10pt"/>
           						<fo:table-column column-width="18pt"/>
           						<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="20pt"/>
           						<fo:table-column column-width="18pt"/>
           						<fo:table-column column-width="20pt"/>
           						<fo:table-column column-width="18pt"/>
           						<fo:table-column column-width="18pt"/>   
           						<fo:table-column column-width="11pt"/>
           						<fo:table-column column-width="6pt"/>
           						<fo:table-column column-width="40pt"/>
           						<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="38pt"/>
           						<fo:table-column column-width="15pt"/>
           						<fo:table-column column-width="32pt"/>
           						<fo:table-body>  
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
								    <#assign previous = "none">
								    <#assign totMlkQty=0>
							     	<#assign totMlkRecQty=0>
								    <#assign totMlkQkg=0>
								    <#list milkDetailslist as milkDetail>
							    	<#assign current = milkDetail.get("milkTransferId")>
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
           									<fo:block keep-together="always" text-align="right">${milkDetail.get('milkTransferId')?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "dd/MM/yyyy")} </fo:block>
           								</fo:table-cell>
           								<#assign shedDetails = delegator.findOne("Facility", {"facilityId" : facilityDetails.parentFacilityId}, true)>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right" >${facilityDetails.mccCode?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("facilityName")?if_exists)),15)}</fo:block>
           								</fo:table-cell>
           									<#if current!= previous>
           										<#if previous!="none">
           											<#assign recptNo= recptNo+1>
           										</#if>
           									</#if>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${recptNo}</fo:block>
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
           									<#assign receivedLRStr= '0.0'>
           									<#if (milkDetail.get("receivedLR"))?has_content>
           										<#assign receivedLRStr= milkDetail.get("receivedLR")>
           									</#if>
           									<fo:block keep-together="always" text-align="right">${receivedLRStr?if_exists?string("##0.0")} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign receivedAcidityStr= '0.00'>
           									<#if (milkDetail.get("receivedAcidity"))?has_content>
           										<#assign receivedAcidityStr= milkDetail.get("receivedAcidity")>
           									</#if>
           									
           									<fo:block keep-together="always" text-align="right">${receivedAcidityStr}</fo:block>
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
           								</fo:table-cell>
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
										 <#if previous!= current>
											<fo:block keep-together="always" text-align="right">${(monthTotals.get("recd")-monthTotals.get("send"))?if_exists?string("##0.0")} </fo:block>
										 <#assign previous=current >
										 <#else>
   											<fo:block keep-together="always" text-align="right">&#160; </fo:block>
   										 </#if>
           								</fo:table-cell>
                   					</fo:table-row>
                   					</#if>
                   					</#list>
                   					<fo:table-row>
                   						<fo:table-cell>
                   							<fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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
           								</fo:table-cell>
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
                   							<fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                   						</fo:table-cell>
                   					</fo:table-row>
                   				</fo:table-body>
        					</fo:table>
        				</fo:block>
    			</fo:flow>		
   			</fo:page-sequence>	
			<fo:page-sequence master-reference="main">
				<fo:static-content flow-name="xsl-region-before">
					<fo:block font-size="5pt">VST_ASCII-018 </fo:block>
					<fo:block text-align="left" font-size="5pt" white-space-collapse="false" keep-together="always">&#160;            ABSTRACT FOR UNIT-WISE MILK RECEIPTS    PERIOD FROM  : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMM d, yyyy")}        Page:<fo:page-number/>              </fo:block>                      
					<fo:block white-space-collapse="false" text-align="left" font-size="8pt">----------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">CODE    UNOIN/SHED        TYPE OF     QUANTITY            QUANTITY           TOTAL              TOTAL            AVERAGE         NO.OF   AVERAGE</fo:block>
					<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">&#160;              NAME                   MILK                  (LTS) 	                (KGS)              KG FAT	 	 	    	  KG SNF	        FAT%      SNF%   DAYS	       QTY(LTS)</fo:block>
					<fo:block white-space-collapse="false" text-align="left" font-size="8pt">----------------------------------------------------------------------------------------------------------------------</fo:block> 
				</fo:static-content>
					<fo:flow flow-name="xsl-region-body" font-family="Courier,monospae"> 
						<fo:block font-family="Courier,monospae" font-size="5pt">
						<fo:table>
							<fo:table-column column-width="14pt"/>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="37pt"/>
							<fo:table-column column-width="36pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="17.1pt"/>
							<fo:table-column column-width="10pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-body>
							<#assign totReceivedQty=0>
							<#assign totQtyKgs=0>
							<#assign totFedKgFat=0>
						    <#assign totFedKgSnf=0>
						    <#assign totAvgltrs=0>
							<#if finalUnitMap?has_content>					    
								<#assign milkDetail = finalUnitMap.entrySet()>
				                <#list milkDetail as milkData>
				               		<#assign productMilkData = milkData.getValue().entrySet()>
				                	<#list productMilkData as product>
				                		<#if product.getKey()!="TOT">
				                			<#if product.getValue().get('recdQtyLtrs')!=0>
				                				<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : milkData.getKey()}, true)>
				                				<#assign shedDetails = delegator.findOne("Facility", {"facilityId" :facilityDetails.parentFacilityId}, true)>
												<fo:table-row>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="left" font-weight="bold">&#160;${facilityDetails.mccCode?if_exists}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="left" font-weight="bold"> ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("facilityName")?if_exists)),15)}</fo:block>
													</fo:table-cell>
													<#assign products = delegator.findOne("Product", {"productId" : product.getKey()}, true)>
													<#assign productName= products.brandName>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="left" keep-together="always" font-weight="bold">${productName.replace(" ","-")?if_exists}</fo:block>
													</fo:table-cell>
				                                    <#assign totReceivedQty=totReceivedQty+(product.getValue().get("recdQtyLtrs"))> 
				                                    <#assign rcdQtyLts=product.getValue().get("recdQtyLtrs")>
				                                    <#assign rcdLtrs=0>
				                                    <#if rcdQtyLts?has_content>
				                                    	<#assign rcdLtrs=rcdQtyLts>
				                                    </#if>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${rcdLtrs?string("##0.0")}</fo:block>
													</fo:table-cell>
													<#assign totQtyKgs=totQtyKgs+(product.getValue().get("recdQtyKgs"))> 
													<#assign rcdQuantity=product.getValue().get("recdQtyKgs")>
				                                    <#assign rcdQtyKgs=0>
				                                    <#if rcdQuantity?has_content>
				                                    	<#assign rcdQtyKgs=rcdQuantity>
				                                    </#if>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${rcdQtyKgs?string("##0.0")}</fo:block>
													</fo:table-cell>
													<#assign totFedKgFat=totFedKgFat+(product.getValue().get("recdKgFat"))> 
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${product.getValue().get("recdKgFat")?if_exists?string("##0.00")}</fo:block>
													</fo:table-cell>
													<#assign totFedKgSnf=totFedKgSnf+(product.getValue().get("recdKgSnf"))>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${product.getValue().get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${product.getValue().get("receivedFat")?if_exists?string("##0.0")}0</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${product.getValue().get("receivedSnf")?if_exists?string("##0.00")}</fo:block>
													</fo:table-cell>
													<#assign unitQty=productWiseDaysMap.get(milkData.getKey())>
													<#assign totalDays=(unitQty.get(product.getKey()).get("noofdays"))>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${totalDays?if_exists}</fo:block>
													</fo:table-cell>
													<#assign avgQty=(unitQty.get(product.getKey()).get("avgQtyLtrs"))>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${(avgQty)?if_exists?string("##0")}</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</#if>
										</#if>
									</#list>
							    </#list>
							</#if>
						</fo:table-body>
					</fo:table>
			     	</fo:block>
					<fo:block font-size="8pt" white-space-collapse="false" text-align="left">---------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block font-family="Courier,monospae" font-size="5pt">
						<fo:table>
							<fo:table-column column-width="14pt"/>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="37pt"/>
							<fo:table-column column-width="36pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="17.1pt"/>
							<fo:table-column column-width="10pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-body>
								<#assign fedProductQty = 0>
								<#if unitProductTotalMap?has_content>
									<#assign grandTotals = unitProductTotalMap.entrySet()>
					                <#list grandTotals as grandTot>
					                	<#if grandTot.getValue().get("recdQtyLtrs")!=0>
											<fo:table-row>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="left" font-weight="bold">${grandTot.getKey()}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="left" font-weight="bold">TOTAL:</fo:block>
												</fo:table-cell>
												<#assign products = delegator.findOne("Product", {"productId" : grandTot.getKey()}, true)>
												<#assign productName= products.brandName>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="left" keep-together="always" font-weight="bold">${productName.replace(" ","-")?if_exists}</fo:block>
													</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandTot.getValue().get("recdQtyLtrs")?if_exists?string("##0.0")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandTot.getValue().get("recdQtyKgs")?if_exists?string("##0.0")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandTot.getValue().get("recdKgFat")?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandTot.getValue().get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
												</fo:table-cell>
												<#assign grandFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandTot.getValue().get("recdKgFat"),grandTot.getValue().get("recdQtyKgs")))>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandFat?string("##0.0")}0</fo:block>
												</fo:table-cell>
												<#assign grandSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandTot.getValue().get("recdKgSnf"),grandTot.getValue().get("recdQtyKgs")))>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandSnf?string("##0.00")}</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold"></fo:block>
												</fo:table-cell>
												<#assign productGrandQty= productAvgMap.get(grandTot.getKey()).get("avgQtyLtrs")>
												<#assign fedProductQty = fedProductQty+productGrandQty>
												<fo:table-cell>
													<fo:block font-size="5pt" text-align="right" font-weight="bold">${productGrandQty?if_exists?string("#0")}</fo:block>
												</fo:table-cell>
								     		</fo:table-row>
								    	 </#if>
									 </#list>
								 </#if>
							</fo:table-body>
					    </fo:table>
				    </fo:block>
				    <fo:block font-size="8pt" white-space-collapse="false" text-align="left">&#160;  ---------------------------------------------------------------------------------------------------------------</fo:block>
 					<fo:block font-family="Courier,monospace" font-size="5pt">
						<fo:table>
							<fo:table-column column-width="64pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="37pt"/>
							<fo:table-column column-width="36pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-column column-width="21pt"/>
							<fo:table-column column-width="17pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-body>
							    <fo:table-row>
									<fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold" text-align="left">FEDERATION TOTAL:</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold" text-align="left"></fo:block>
							        </fo:table-cell>
							        <#assign totFedReceivedQty=0>
							        <#if totReceivedQty?has_content>
							        	<#assign totFedReceivedQty=totReceivedQty>
							        </#if>
									<fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${totFedReceivedQty?string("##0.0")}</fo:block>
							        </fo:table-cell>
							        <#assign totFedQtyKgs=0>
							        <#if totQtyKgs?has_content>
							        	<#assign totFedQtyKgs=totQtyKgs>
							        </#if>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${totFedQtyKgs?string("##0.0")}</fo:block>
							        </fo:table-cell>
							        <#assign fedTotKgFat=0>
							        <#if totFedKgFat?has_content>
							        	<#assign fedTotKgFat=totFedKgFat>
							        </#if>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${fedTotKgFat?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <#assign fedTotKgSnf=0>
							        <#if totFedKgSnf?has_content>
							        	<#assign fedTotKgSnf=totFedKgSnf>
							        </#if>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${fedTotKgSnf?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <#assign fedFat=0>
							        <#assign fat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(totFedKgFat,totQtyKgs))> 
								        <#if fat?has_content>
								        	<#assign fedFat=fat>
								        </#if>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${fedFat?string("##0.0")}0</fo:block>
							        </fo:table-cell>
							        <#assign fedSnf=0>
								    <#assign snf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(totFedKgSnf,totQtyKgs))>
								     	<#if snf?has_content>
								        	<#assign fedSnf=snf>
								        </#if>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${fedSnf?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right"></fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${fedProductQty?if_exists?string("#0")}</fo:block>
							        </fo:table-cell>
							    </fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<#if finalPrivateUnitMap?has_content>
					<fo:block font-size="8pt" white-space-collapse="false" text-align="left">---------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block font-family="Courier,monospace" font-size="5pt">
						<fo:table>
							<fo:table-column column-width="14pt"/>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="37pt"/>
							<fo:table-column column-width="36pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="17.1pt"/>
							<fo:table-column column-width="10pt"/>
							<fo:table-column column-width="30pt"/>
							<#assign privatetotQty =0>
							<#assign privatetotQtyKgs =0>
							<#assign privatetotKgFat =0>
							<#assign privatetotKgSnf =0>
							<#assign finalPrivateTotals = finalPrivateUnitMap.entrySet()>
			                <#list finalPrivateTotals as finalPrivateTotal>
			                	<#assign privateTotal = finalPrivateTotal.getValue().entrySet()>
		                		<#list privateTotal as privateTot>
		                			<#if privateTot.getKey()!="TOT">
		                				<#if privateTot.getValue().get('recdQtyLtrs')!=0>
		                				<#assign facilityPrivateDetails = delegator.findOne("Facility", {"facilityId" : finalPrivateTotal.getKey()}, true)>
		                				<#assign shedPrivateDetails = delegator.findOne("Facility", {"facilityId" :facilityPrivateDetails.parentFacilityId}, true)>
				    						<fo:table-body> 
												<fo:table-row>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="left" font-weight="bold">${facilityPrivateDetails.mccCode?if_exists}</fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="left" font-weight="bold"> ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityPrivateDetails.get("facilityName")?if_exists)),15)}</fo:block>
													</fo:table-cell>
													<#assign products = delegator.findOne("Product", {"productId" : privateTot.getKey()}, true)>
													<#assign productName= products.brandName>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="left" keep-together="always" font-weight="bold">${productName.replace(" ","-")?if_exists}</fo:block>
													</fo:table-cell>
													<#assign privateQty = (privateTot.getValue().get("recdQtyLtrs"))>
													 <#assign privatetotQty=privatetotQty+privateQty> 
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold"><#if (privateTot.getValue().get("recdQtyLtrs"))!=0>${privateTot.getValue().get("recdQtyLtrs")?if_exists?string("##0.0")}<#else>0.00</#if></fo:block>
													</fo:table-cell>
													<#assign privatetotQtyKgs=privatetotQtyKgs+(privateTot.getValue().get("recdQtyKgs"))> 
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold"><#if (privateTot.getValue().get("recdQtyKgs"))!=0>${privateTot.getValue().get("recdQtyKgs")?if_exists?string("##0.0")}<#else>0.00</#if></fo:block>
													</fo:table-cell>
													<#assign privatetotKgFat=privatetotKgFat+(privateTot.getValue().get("recdKgFat"))> 
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold"><#if (privateTot.getValue().get("recdKgFat"))!=0>${privateTot.getValue().get("recdKgFat")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
													</fo:table-cell>
													<#assign privatetotKgSnf=privatetotKgSnf+(privateTot.getValue().get("recdKgSnf"))>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold"><#if (privateTot.getValue().get("recdKgSnf"))!=0>${privateTot.getValue().get("recdKgSnf")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold"><#if (privateTot.getValue().get("receivedFat"))!=0>${privateTot.getValue().get("receivedFat")?string("##0.0")}0<#else>0.00</#if></fo:block>
													</fo:table-cell>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold"><#if (privateTot.getValue().get("receivedSnf"))!=0>${privateTot.getValue().get("receivedSnf")?string("##0.00")}<#else>0.00</#if></fo:block>
													</fo:table-cell>
													<#assign privateUnitQty=privateProductWiseDaysMap.get(finalPrivateTotal.getKey())>
													<#assign privateTotalDays=(privateUnitQty.get(privateTot.getKey()).get("noofdays"))>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${privateTotalDays?if_exists}</fo:block>
													</fo:table-cell>
													<#assign privateAvgQty=(privateUnitQty.get(privateTot.getKey()).get("avgQtyLtrs"))>
													<fo:table-cell>
														<fo:block font-size="5pt" text-align="right" font-weight="bold">${(privateAvgQty)?if_exists?string("#0")}</fo:block>
													</fo:table-cell>
						     					</fo:table-row>	
					    					</fo:table-body>
										</#if>
									</#if>
								</#list>
							</#list>
					    </fo:table>
				    </fo:block>
				    </#if>
			        <#if privateProductTotalMap?has_content>
			        <fo:block font-size="8pt" white-space-collapse="false" text-align="left">---------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block font-family="Courier,monospace" font-size="5pt">
						<fo:table>
							<fo:table-column column-width="14pt"/>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="37pt"/>
							<fo:table-column column-width="36pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="17.1pt"/>
							<fo:table-column column-width="10pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-body>
								<#assign privateProductQty =0> 
								<#assign grandPrivateTotals = privateProductTotalMap.entrySet()>
				                <#list grandPrivateTotals as grandPrivateTot>
									<#if grandPrivateTot.getValue().get("recdQtyLtrs")!=0>
										<fo:table-row>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="left" font-weight="bold">${grandPrivateTot.getKey()}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="left" font-weight="bold">TOTAL:</fo:block>
											</fo:table-cell>
											<#assign products = delegator.findOne("Product", {"productId" : grandPrivateTot.getKey()}, true)>
											<#assign productName= products.brandName>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="left" keep-together="always" font-weight="bold">${productName.replace(" ","-")?if_exists}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandPrivateTot.getValue().get("recdQtyLtrs")?if_exists?string("##0.0")}</fo:block>
									        </fo:table-cell>
									        <fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandPrivateTot.getValue().get("recdQtyKgs")?if_exists?string("##0.0")}</fo:block>
									        </fo:table-cell>
									        <fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandPrivateTot.getValue().get("recdKgFat")?if_exists?string("##0.00")}</fo:block>
									        </fo:table-cell>
									        <fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandPrivateTot.getValue().get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
									        </fo:table-cell>
									        <#assign grandFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandPrivateTot.getValue().get("recdKgFat"),grandPrivateTot.getValue().get("recdQtyKgs")))>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandFat?if_exists?string("##0.0")}0</fo:block>
											</fo:table-cell>
											<#assign grandSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandPrivateTot.getValue().get("recdKgSnf"),grandPrivateTot.getValue().get("recdQtyKgs")))>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandSnf?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold"></fo:block>
											</fo:table-cell>
											<#assign productPrivateGrandQty= productPrivateAvgMap.get(grandPrivateTot.getKey()).get("avgQtyLtrs")>
											<#assign privateProductQty = privateProductQty+productPrivateGrandQty>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${productPrivateGrandQty?if_exists?string("#0")}</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</#if>
								</#list>
							</fo:table-body>
						</fo:table>
				 	</fo:block>
				 	<fo:block font-size="8pt" white-space-collapse="false" text-align="left">&#160;  ---------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block font-family="Courier,monospace" font-size="5pt">
					<fo:table>
						<fo:table-column column-width="64pt"/>
						<fo:table-column column-width="15pt"/>
						<fo:table-column column-width="40pt"/>
						<fo:table-column column-width="37pt"/>
						<fo:table-column column-width="36pt"/>
						<fo:table-column column-width="35pt"/>
						<fo:table-column column-width="21pt"/>
						<fo:table-column column-width="17pt"/>
						<fo:table-column column-width="20pt"/>
						<fo:table-column column-width="20pt"/>
						<fo:table-body>
							<#assign privatetotFat =0>
							<#assign privatetotSnf =0>
							<#if privatetotQty!=0>
							 	<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold" text-align="left">UNION/OTHER TOTAL:</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold" text-align="left"></fo:block>
							        </fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${privatetotQty?if_exists?string("##0.0")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${privatetotQtyKgs?if_exists?string("##0.0")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${privatetotKgFat?if_exists?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${privatetotKgSnf?if_exists?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <#assign privatetotFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(privatetotKgFat,privatetotQtyKgs))>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${privatetotFat?if_exists?string("##0.0")}0</fo:block>
							        </fo:table-cell>
							        <#assign privatetotSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(privatetotKgSnf,privatetotQtyKgs))>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${privatetotSnf?if_exists?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right"></fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right">${privateProductQty?if_exists?string("#0")}</fo:block>
							        </fo:table-cell>
							    </fo:table-row>
							</#if>
						</fo:table-body>
					</fo:table>
					</fo:block>
					<fo:block font-size="8pt" white-space-collapse="false" text-align="left">---------------------------------------------------------------------------------------------------------------------</fo:block>
					</#if>
					<fo:block font-family="Courier,monospace" font-size="5pt">
						<fo:table>
							<fo:table-column column-width="14pt"/>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="37pt"/>
							<fo:table-column column-width="36pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="17.1pt"/>
							<fo:table-column column-width="10pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-body> 
								<#assign productFinalGrandQty=0>
								<#assign grandQty=0>
				                <#assign grandKgs=0>
				                <#assign grandKgFat=0>
				                <#assign grandKgSnf=0>
				                <#assign grandFat=0>
				                <#assign grandSnf=0>
								<#assign finTotals = finalTotalMap.entrySet()>
			                	<#list finTotals as finTot>
			                		<#if finTot.getValue().get("recdQtyLtrs")!=0>
										<fo:table-row>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="left" font-weight="bold">${finTot.getKey()}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="left" font-weight="bold">TOTAL:</fo:block>
											</fo:table-cell>
											<#assign products = delegator.findOne("Product", {"productId" : finTot.getKey()}, true)>
											<#assign productName= products.brandName>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="left" keep-together="always" font-weight="bold">${productName.replace(" ","-")?if_exists}</fo:block>
											</fo:table-cell>
											<#assign grandQty=grandQty+finTot.getValue().get("recdQtyLtrs")>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${finTot.getValue().get("recdQtyLtrs")?if_exists?string("##0.0")}</fo:block>
											</fo:table-cell>
											<#assign grandKgs=grandKgs+finTot.getValue().get("recdQtyKgs")>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${finTot.getValue().get("recdQtyKgs")?if_exists?string("##0.0")}</fo:block>
											</fo:table-cell>
											<#assign grandKgFat=grandKgFat+finTot.getValue().get("recdKgFat")>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${finTot.getValue().get("recdKgFat")?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<#assign grandKgSnf=grandKgSnf+finTot.getValue().get("recdKgSnf")>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${finTot.getValue().get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											 <#assign grandFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(finTot.getValue().get("recdKgFat"),finTot.getValue().get("recdQtyKgs")))>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandFat?if_exists?string("##0.0")}0</fo:block>
											</fo:table-cell>
											 <#assign grandSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(finTot.getValue().get("recdKgSnf"),finTot.getValue().get("recdQtyKgs")))>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${grandSnf?if_exists?string("##0.00")}</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold"></fo:block>
											</fo:table-cell>
											<#assign productFinGrandQty= grandTotalAvgMap.get(finTot.getKey()).get("avgQtyLtrs")>
											<#assign productFinalGrandQty = productFinalGrandQty+productFinGrandQty>
											<fo:table-cell>
												<fo:block font-size="5pt" text-align="right" font-weight="bold">${productFinGrandQty?if_exists?string("#0")}</fo:block>
											</fo:table-cell>
										</fo:table-row>
							 		</#if>
								</#list>
						    </fo:table-body>
					    </fo:table>
					</fo:block>
					<fo:block font-size="8pt" white-space-collapse="false" text-align="left">&#160;  ---------------------------------------------------------------------------------------------------------------</fo:block>
			 		<fo:block font-family="Courier,monospace" font-size="5pt">
						<fo:table>
							<fo:table-column column-width="64pt"/>
							<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="37pt"/>
							<fo:table-column column-width="36pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-column column-width="21pt"/>
							<fo:table-column column-width="17pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-column column-width="20pt"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold" text-align="left">GRAND TOTAL:</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" font-weight="bold" text-align="left"></fo:block>
							        </fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold">${grandQty?if_exists?string("##0.0")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold">${grandKgs?if_exists?string("##0.0")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold">${grandKgFat?if_exists?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold">${grandKgSnf?if_exists?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <#assign grandTotFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandKgFat,grandKgs))>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold">${grandTotFat?if_exists?string("##0.0")}0</fo:block>
							        </fo:table-cell>
							        <#assign grandTotSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(grandKgSnf,grandKgs))>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold">${grandTotSnf?if_exists?string("##0.00")}</fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold"></fo:block>
							        </fo:table-cell>
							        <fo:table-cell>
										<fo:block font-size="5pt" white-space-collapse="false" text-align="right" font-weight="bold">${productFinalGrandQty?if_exists?string("#0")}</fo:block>
							        </fo:table-cell>
							    </fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>	
					<fo:block font-size="8pt" white-space-collapse="false" text-align="left">---------------------------------------------------------------------------------------------------------------------</fo:block>
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
