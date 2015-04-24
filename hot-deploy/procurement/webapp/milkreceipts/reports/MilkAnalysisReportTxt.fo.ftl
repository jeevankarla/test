<#escape x as x?xml>
  <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:layout-master-set>
      <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
        margin-top="0.5in" margin-bottom="0.3in" margin-left=".5in" margin-right="1in">
          <fo:region-body margin-top="0.7in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "MilkAnalysisReport.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else> 
    <#if ShiftWiseMap?has_content>
     <#assign ShiftWiseList=ShiftWiseMap.entrySet()>
    	<#assign recptNo =1> 
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="5pt">
			 	<fo:block white-space-collapse="false" font-weight="bold" text-align="left" keep-together="always" font-size = "11pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${uiLabelMap.KMFDairyHeader}</fo:block>
     			<fo:block white-space-collapse="false" font-weight="bold" text-align="left" keep-together="always" font-size = "11pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${uiLabelMap.KMFDairySubHeader}</fo:block>
     			<fo:block white-space-collapse="false" font-weight="bold" text-align="left" keep-together="always" font-size = "11pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;----------------------------------------------------------------</fo:block>
     			<fo:block text-align="left" white-space-collapse="false" font-size="6pt" keep-together="always">&#160;					                                      DESPATCH MILK ANALYSIS REPORT                               </fo:block>     		
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 
				<fo:block white-space-collapse="false" font-weight="bold" text-align="left" keep-together="always" font-size = "10pt">----------------------------------------------------------------------------------------------------------------------</fo:block>
        		<fo:block>
        		   <fo:table >
   					 <fo:table-column column-width="80pt"/>
   					 <fo:table-column column-width="15pt"/>
   					 <fo:table-column column-width="31pt"/>
   					 <fo:table-column column-width="33pt"/>
   					 <fo:table-column column-width="23pt"/>
   					 <fo:table-column column-width="11pt"/>
   					 <fo:table-column column-width="20pt"/>
   					 <fo:table-column column-width="25pt"/>
   					 <fo:table-column column-width="28pt"/>
   					 <fo:table-column column-width="30pt"/>
   					 <fo:table-column column-width="40pt"/>
   					 <fo:table-column column-width="60pt"/>
   					     <fo:table-body>
	   					     <fo:table-row>
	       					     <fo:table-cell>
	       					        <fo:block  text-align="left" font-size = "6pt" font-weight="bold" white-space-collapse="false">DATE/SHIFT</fo:block>
	       					     </fo:table-cell>
	       					     <fo:table-cell>
	       					        <fo:block  text-align="left" font-size = "6pt" font-weight="bold" white-space-collapse="false">TANKER NO.</fo:block>
	       					     </fo:table-cell>
	       					     <fo:table-cell>
	       					        <fo:block keep-together="always" text-align="right" font-size = "6pt" font-weight="bold">TEMP.</fo:block>
	       					     </fo:table-cell>
	       					     <fo:table-cell>
	       					        <fo:block keep-together="always" text-align="right" font-size = "6pt" font-weight="bold">ACIDITY</fo:block>
	       					     </fo:table-cell>
	       					     <fo:table-cell>
	       					        <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">COB</fo:block>
	       					     </fo:table-cell>
	       					     <fo:table-cell>
	       					        <fo:block keep-together="always" text-align="right" font-size = "6pt" font-weight="bold">CLR</fo:block>
	       					     </fo:table-cell>
	       					     <fo:table-cell>
	       					        <fo:block keep-together="always" text-align="right" font-size = "6pt" font-weight="bold">FAT</fo:block>
	       					     </fo:table-cell>
	       					     <fo:table-cell>
	       					        <fo:block keep-together="always" text-align="right" font-size = "6pt" font-weight="bold">SNF</fo:block>
	       					     </fo:table-cell>
	       					     <fo:table-cell>
	       					        <fo:block keep-together="always" text-align="right" font-size = "6pt" font-weight="bold">P.TEST</fo:block>
	       					     </fo:table-cell>
	       					     <fo:table-cell>
	       					        <fo:block keep-together="always" text-align="right" font-size = "6pt" font-weight="bold">MBR</fo:block>
	       					     </fo:table-cell>
	       					     <fo:table-cell>
	       					        <fo:block white-space-collapse="false" text-align="right" font-size = "6pt" font-weight="bold">LOADING TIME</fo:block>
	       					     </fo:table-cell>
	       					     <fo:table-cell>
	       					        <fo:block keep-together="always" text-align="right" font-size = "6pt" font-weight="bold">REMARKS</fo:block>
	       					     </fo:table-cell>
	   					     </fo:table-row>
       			         </fo:table-body>
       		         </fo:table>		    
       	        </fo:block> 
       			<fo:block white-space-collapse="false" font-weight="bold" text-align="left" keep-together="always" font-size = "10pt">----------------------------------------------------------------------------------------------------------------------</fo:block> 
       			<fo:block font-family="Courier,monospace" font-size="5pt">
       			   <fo:table >
   					 <fo:table-column column-width="80pt"/>
   					 <fo:table-column column-width="15pt"/>
   					 <fo:table-column column-width="31pt"/>
   					 <fo:table-column column-width="33pt"/>
   					 <fo:table-column column-width="20pt"/>
   					 <fo:table-column column-width="15pt"/>
   					 <fo:table-column column-width="25pt"/>
   					 <fo:table-column column-width="25pt"/>
   					 <fo:table-column column-width="20pt"/>
   					 <fo:table-column column-width="35pt"/>
   					 <fo:table-column column-width="40pt"/>
   					 <fo:table-column column-width="60pt"/>
       					 <fo:table-body>
       					  <#list ShiftWiseList as shift>
                             <#assign shiftType = shift.getKey()>   
                             <#assign milkTransferDetailsList = shift.getValue()>
       					     <#list milkTransferDetailsList as milkDetail> 
                             <fo:table-row>
   								<fo:table-cell>
   									<fo:block keep-together="always" text-align="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(milkDetail.get("sendDate")?if_exists, "dd/MM/yyyy HH:mm")}/<#if shiftType==1>${shiftType}st<#else><#if shiftType==2>${shiftType}nd<#else><#if shiftType==3>${shiftType}rd</#if></#if></#if></fo:block>
   								</fo:table-cell>
                                <fo:table-cell>
   									<fo:block keep-together="always" text-align="left">${milkDetail.get("containerId")?if_exists}</fo:block>
   								</fo:table-cell>
                                <fo:table-cell>
   									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendTemparature")?if_exists?string('#0.0')}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendAcidity")?if_exists}</fo:block>
   								</fo:table-cell>
                                <fo:table-cell>
   									<fo:block keep-together="always" text-align="center">${milkDetail.get("sendCob")?if_exists}</fo:block>
   								</fo:table-cell> 
   								<fo:table-cell>
   									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendLR")?if_exists?string("##0.0")}</fo:block>
   								</fo:table-cell> 
                                <fo:table-cell>
   									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendKgFat")?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>   
   								<fo:table-cell>
   									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendKgSnf")?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendPH")?if_exists}</fo:block>
   								</fo:table-cell>   
   								<fo:table-cell>
   									<fo:block keep-together="always" text-align="right">${milkDetail.get("sendMBRT")?if_exists}</fo:block>
   								</fo:table-cell>        
   							 </fo:table-row> 
   						</#list>
                       </#list>
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