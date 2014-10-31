<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".2in">
                <fo:region-body margin-top=".5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "MilkRecptErrorList.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>         
    
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="5pt">
                <fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">DATED AS ON : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")}  </fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">MILK RECEIPTS VALIDATION ERROR LIST </fo:block>
				<fo:block font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">&#160;                                                       :     A S    P E R    D E S P A T C H       :  A S   P E R   A C K N O W L E D G E M E N T  :      REMARKS</fo:block>
				<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">&#160;                                                       : ------------------------------------------:-----------------------------------------------:------------------------</fo:block>
				<fo:block text-align="left"  white-space-collapse="false" font-size="5pt">&#160;  NO    	DATE      GCNO    GNAME           GTKNO    GTM:     GQTY1     GFAT1     GSNF1     GCLR1   :     GQTY2       GFAT2     GSNF2     GCLR2     :</fo:block>
				<fo:block font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 
						<fo:block font-family="Courier,monospace" font-size="5pt">
						 	<fo:table>
						 		<fo:table-column column-width="20pt"/>
           						<fo:table-column column-width="35pt"/>
       							<fo:table-column column-width="0pt"/>
       							<fo:table-column column-width="15pt"/><!--nAME-->
           						<fo:table-column column-width="25pt"/>
           						<fo:table-column column-width="50pt"/>
           						<fo:table-column column-width="26pt"/>
       							<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="23pt"/>
           						<fo:table-column column-width="30pt"/>
           						<fo:table-column column-width="30pt"/> <!--LR-->
       							<fo:table-column column-width="45pt"/>
       							<fo:table-column column-width="35pt"/>
           						<fo:table-column column-width="30pt"/>
           						<fo:table-column column-width="40pt"/>
           						
           						<fo:table-column column-width="80pt"/>
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
								  <#if fatErrorList?has_content>  
								    <#list fatErrorList as milkDetail>
							    	<#assign current = milkDetail.get("milkTransferId")>
								    <#assign date = milkDetail.get("sendDate")>
									<#assign date1 = milkDetail.get("receiveDate")>									
									<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : milkDetail.get("facilityId")}, true)>	
								  	<#if (milkDetail.get("receivedQuantityLtrs"))!=0>
                   					<fo:table-row>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">${milkDetail.get("milkTransferId")?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "dd/MM/yyyy")} </fo:block>
           								</fo:table-cell>
           								<#assign shedDetails = delegator.findOne("Facility", {"facilityId" : facilityDetails.parentFacilityId}, true)>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="6pt">${facilityDetails.mccCode?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("facilityName")?if_exists)),15)}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("vehicleId")?if_exists} </fo:block>
           								</fo:table-cell>           								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendProductId")?if_exists}${milkDetail.get("milkCondition")?if_exists}</fo:block>
           								</fo:table-cell>           								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><!--${milkDetail.get("quantityLtrs")?if_exists?string("##0.0")}--></fo:block>
           								</fo:table-cell>            								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("fat")?if_exists?string("##0.00")} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><!-- ${milkDetail.get("snf")?if_exists?string("##0.00")}--></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign sendLrStr= '0.00'>
           									<#if (milkDetail.get("sendLR"))?has_content >
           										<#assign sendLrStr= milkDetail.get("sendLR")>
           									</#if>	
           									<fo:block keep-together="always" text-align="right"><!--${sendLrStr?string("##0.0")}--></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><!-- ${milkDetail.get("receivedQuantityLtrs")?if_exists?string("##0.0")}--> </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedFat")?if_exists?string("##0.00")} </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><!-- ${milkDetail.get("receivedSnf")?if_exists?string("##0.00")}--></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign receivedLRStr= '0.00'>
           									<#if (milkDetail.get("sendTemparature"))?has_content>
           										<#assign receivedLRStr= milkDetail.get("receivedLR")>
           									</#if>
           									<fo:block keep-together="always" text-align="right"><!--${receivedLRStr?string("##0.0")}--> </fo:block>
           								</fo:table-cell> 
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="15pt"> FAT CHECK</fo:block>
           								</fo:table-cell>      								          								
                   					</fo:table-row>
                   					</#if>
                   					</#list>
                   					</#if>
                   					<#if snfErrorList?has_content>  
								    <#list snfErrorList as milkDetail>
							    	<#assign current = milkDetail.get("milkTransferId")>
								    <#assign date = milkDetail.get("sendDate")>
									<#assign date1 = milkDetail.get("receiveDate")>									
									<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : milkDetail.get("facilityId")}, true)>	
								  	<#if (milkDetail.get("receivedQuantityLtrs"))!=0>
                   					<fo:table-row>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">${milkDetail.get("milkTransferId")?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "dd/MM/yyyy")} </fo:block>
           								</fo:table-cell>
           								<#assign shedDetails = delegator.findOne("Facility", {"facilityId" : facilityDetails.parentFacilityId}, true)>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="6pt">${facilityDetails.mccCode?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("facilityName")?if_exists)),15)}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("vehicleId")?if_exists} </fo:block>
           								</fo:table-cell>           								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendProductId")?if_exists}${milkDetail.get("milkCondition")?if_exists}</fo:block>
           								</fo:table-cell>           								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><!--${milkDetail.get("quantityLtrs")?if_exists?string("##0.0")}--></fo:block>
           								</fo:table-cell>            								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><!--${milkDetail.get("fat")?if_exists?string("##0.00")}--> </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("snf")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign sendLrStr= '0.00'>
           									<#if (milkDetail.get("sendLR"))?has_content >
           										<#assign sendLrStr= milkDetail.get("sendLR")>
           									</#if>	
           									<fo:block keep-together="always" text-align="right"><!--${sendLrStr?string("##0.0")}--></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><!-- ${milkDetail.get("receivedQuantityLtrs")?if_exists?string("##0.0")}--> </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><!--${milkDetail.get("receivedFat")?if_exists?string("##0.00")}--> </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedSnf")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign receivedLRStr= '0.00'>
           									<#if (milkDetail.get("sendTemparature"))?has_content>
           										<#assign receivedLRStr= milkDetail.get("receivedLR")>
           									</#if>
           									<fo:block keep-together="always" text-align="right"><!--${receivedLRStr?string("##0.0")}--> </fo:block>
           								</fo:table-cell> 
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="15pt"> SNF CHECK</fo:block>
           								</fo:table-cell>      								          								
                   					</fo:table-row>
                   					</#if>
                   					</#list>
                   					</#if>
                   					<#if clrErrorList?has_content>  
								    <#list clrErrorList as milkDetail>
							    	<#assign current = milkDetail.get("milkTransferId")>
								    <#assign date = milkDetail.get("sendDate")>
									<#assign date1 = milkDetail.get("receiveDate")>									
									<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : milkDetail.get("facilityId")}, true)>	
								  	<#if (milkDetail.get("receivedQuantityLtrs"))!=0>
                   					<fo:table-row>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">${milkDetail.get("milkTransferId")?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "dd/MM/yyyy")} </fo:block>
           								</fo:table-cell>
           								<#assign shedDetails = delegator.findOne("Facility", {"facilityId" : facilityDetails.parentFacilityId}, true)>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="6pt">${facilityDetails.mccCode?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("facilityName")?if_exists)),15)}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("vehicleId")?if_exists} </fo:block>
           								</fo:table-cell>           								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendProductId")?if_exists}${milkDetail.get("milkCondition")?if_exists}</fo:block>
           								</fo:table-cell>           								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><!--${milkDetail.get("quantityLtrs")?if_exists?string("##0.0")}--></fo:block>
           								</fo:table-cell>            								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><!--${milkDetail.get("fat")?if_exists?string("##0.00")}--> </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><!--${milkDetail.get("snf")?if_exists?string("##0.00")}--></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign sendLrStr= '0.00'>
           									<#if (milkDetail.get("sendLR"))?has_content >
           										<#assign sendLrStr= milkDetail.get("sendLR")>
           									</#if>	
           									<fo:block keep-together="always" text-align="right">${sendLrStr?string("##0.0")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><!-- ${milkDetail.get("receivedQuantityLtrs")?if_exists?string("##0.0")}--> </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><!--${milkDetail.get("receivedFat")?if_exists?string("##0.00")}--> </fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"><!--${milkDetail.get("receivedSnf")?if_exists?string("##0.00")}--></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign receivedLRStr= '0.00'>
           									<#if (milkDetail.get("sendTemparature"))?has_content>
           										<#assign receivedLRStr= milkDetail.get("receivedLR")>
           									</#if>
           									<fo:block keep-together="always" text-align="right">${receivedLRStr?string("##0.0")}</fo:block>
           								</fo:table-cell> 
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="15pt"> CLR CHECK</fo:block>
           								</fo:table-cell>      								          								
                   					</fo:table-row>
                   					</#if>
                   					</#list>
                   					</#if>
                   				<#if qtyErrorList?has_content>  
								    <#list qtyErrorList as milkDetail>
									<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : milkDetail.get("facilityId")}, true)>
                   					<fo:table-row>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">${milkDetail.get("milkTranferId")?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "dd/MM/yyyy")} </fo:block>
           								</fo:table-cell>
           								<#assign shedDetails = delegator.findOne("Facility", {"facilityId" : facilityDetails.parentFacilityId}, true)>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="6pt">${facilityDetails.mccCode?if_exists}</fo:block>
           								</fo:table-cell> 
           								<fo:table-cell>
           									<fo:block></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("facilityName")?if_exists)),15)}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("vehicleId")?if_exists} </fo:block>
           								</fo:table-cell> 
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("productId")?if_exists}${milkDetail.get("milkCondition")?if_exists}</fo:block>
           								</fo:table-cell> 
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("send")?if_exists?string("0.0")}</fo:block>
           								</fo:table-cell> 
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"></fo:block>
           								</fo:table-cell>            								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("recd")?if_exists?string("##0.0")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right"></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           								
           									<fo:block keep-together="always" text-align="right"></fo:block>
           								</fo:table-cell> 
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="15pt">QUANTITY CHECK</fo:block>
           								</fo:table-cell>								          								
                   					</fo:table-row>
                   				
                   					</#list>
                   					</#if>
                   					<#if duplicateEntriesList?has_content>  
								    <#list duplicateEntriesList as milkDetail>
							    	<#assign current = milkDetail.get("milkTransferId")>
								    <#assign date = milkDetail.get("sendDate")>
									<#assign date1 = milkDetail.get("receiveDate")>									
									<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : milkDetail.get("facilityId")}, true)>	
								  	<#if (milkDetail.get("receivedQuantityLtrs"))!=0>
                   					<fo:table-row>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">${milkDetail.get("milkTransferId")?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "dd/MM/yyyy")} </fo:block>
           								</fo:table-cell>
           								<#assign shedDetails = delegator.findOne("Facility", {"facilityId" : facilityDetails.parentFacilityId}, true)>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="6pt">${facilityDetails.mccCode?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("facilityName")?if_exists)),15)}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("vehicleId")?if_exists} </fo:block>
           								</fo:table-cell>           								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendProductId")?if_exists}${milkDetail.get("milkCondition")?if_exists}</fo:block>
           								</fo:table-cell>           								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("quantityLtrs")?if_exists?string("##0.0")}</fo:block>
           								</fo:table-cell>            								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("fat")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("snf")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign sendLrStr= '0.00'>
           									<#if (milkDetail.get("sendLR"))?has_content >
           										<#assign sendLrStr= milkDetail.get("sendLR")>
           									</#if>	
           									<fo:block keep-together="always" text-align="right">${sendLrStr?string("##0.0")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedQuantityLtrs")?if_exists?string("##0.0")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedFat")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedSnf")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign receivedLRStr= '0.00'>
           									<#if (milkDetail.get("sendTemparature"))?has_content>
           										<#assign receivedLRStr= milkDetail.get("receivedLR")>
           									</#if>
           									<fo:block keep-together="always" text-align="right">${receivedLRStr?string("##0.0")}</fo:block>
           								</fo:table-cell> 
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="15pt"> DUPLICATE ENTRY</fo:block>
           								</fo:table-cell>      								          								
                   					</fo:table-row>
                   					</#if>
                   					</#list>
                   					</#if>
                   					<#if sourProdErrorList?has_content>  
								    <#list sourProdErrorList as milkDetail>
							    	<#assign current = milkDetail.get("milkTransferId")>
								    <#assign date = milkDetail.get("sendDate")>
									<#assign date1 = milkDetail.get("receiveDate")>									
									<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : milkDetail.get("facilityId")}, true)>	
								  	<#if (milkDetail.get("receivedQuantityLtrs"))!=0>
                   					<fo:table-row>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">${milkDetail.get("milkTransferId")?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "dd/MM/yyyy")} </fo:block>
           								</fo:table-cell>
           								<#assign shedDetails = delegator.findOne("Facility", {"facilityId" : facilityDetails.parentFacilityId}, true)>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="6pt">${facilityDetails.mccCode?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("facilityName")?if_exists)),15)}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("vehicleId")?if_exists} </fo:block>
           								</fo:table-cell>           								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendProductId")?if_exists}${milkDetail.get("milkCondition")?if_exists}</fo:block>
           								</fo:table-cell>           								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("quantityLtrs")?if_exists?string("##0.0")}</fo:block>
           								</fo:table-cell>            								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("fat")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("snf")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign sendLrStr= '0.00'>
           									<#if (milkDetail.get("sendLR"))?has_content >
           										<#assign sendLrStr= milkDetail.get("sendLR")>
           									</#if>	
           									<fo:block keep-together="always" text-align="right">${sendLrStr?string("##0.0")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedQuantityLtrs")?if_exists?string("##0.0")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedFat")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedSnf")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign receivedLRStr= '0.00'>
           									<#if (milkDetail.get("sendTemparature"))?has_content>
           										<#assign receivedLRStr= milkDetail.get("receivedLR")>
           									</#if>
           									<fo:block keep-together="always" text-align="right">${receivedLRStr?string("##0.0")}</fo:block>
           								</fo:table-cell> 
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="15pt"> SOUR PRODUCT CHECK</fo:block>
           								</fo:table-cell>      								          								
                   					</fo:table-row>
                   					</#if>
                   					</#list>
                   					</#if>
                   					<#if skimmedProdErrorList?has_content>  
								    <#list skimmedProdErrorList as milkDetail>
							    	<#assign current = milkDetail.get("milkTransferId")>
								    <#assign date = milkDetail.get("sendDate")>
									<#assign date1 = milkDetail.get("receiveDate")>									
									<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : milkDetail.get("facilityId")}, true)>	
								  	<#if (milkDetail.get("receivedQuantityLtrs"))!=0>
                   					<fo:table-row>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left">${milkDetail.get("milkTransferId")?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "dd/MM/yyyy")} </fo:block>
           								</fo:table-cell>
           								<#assign shedDetails = delegator.findOne("Facility", {"facilityId" : facilityDetails.parentFacilityId}, true)>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="6pt">${facilityDetails.mccCode?if_exists}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block></fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("facilityName")?if_exists)),15)}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("vehicleId")?if_exists} </fo:block>
           								</fo:table-cell>           								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendProductId")?if_exists}${milkDetail.get("milkCondition")?if_exists}</fo:block>
           								</fo:table-cell>           								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("quantityLtrs")?if_exists?string("##0.0")}</fo:block>
           								</fo:table-cell>            								
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("fat")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("snf")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign sendLrStr= '0.00'>
           									<#if (milkDetail.get("sendLR"))?has_content >
           										<#assign sendLrStr= milkDetail.get("sendLR")>
           									</#if>	
           									<fo:block keep-together="always" text-align="right">${sendLrStr?string("##0.0")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedQuantityLtrs")?if_exists?string("##0.0")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedFat")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="right">${milkDetail.get("receivedSnf")?if_exists?string("##0.00")}</fo:block>
           								</fo:table-cell>
           								<fo:table-cell>
           									<#assign receivedLRStr= '0.00'>
           									<#if (milkDetail.get("sendTemparature"))?has_content>
           										<#assign receivedLRStr= milkDetail.get("receivedLR")>
           									</#if>
           									<fo:block keep-together="always" text-align="right">${receivedLRStr?string("##0.0")}</fo:block>
           								</fo:table-cell> 
           								<fo:table-cell>
           									<fo:block keep-together="always" text-align="left" text-indent="15pt"> SKIMMED PRODUCT CHECK</fo:block>
           								</fo:table-cell>      								          								
                   					</fo:table-row>
                   					</#if>
                   					</#list>
                   					</#if>
                   				</fo:table-body>
        					</fo:table>
        				</fo:block>        				
    			</fo:flow>		
   			</fo:page-sequence>	
	
			
		
	</#if>
</fo:root>
</#escape>
