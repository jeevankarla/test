<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="10in" page-width="12in" margin-left="0.1in" margin-right="0.2in"  margin-top="0.1in" margin-bottom="0.2in" >
                <fo:region-body margin-top="2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "ConsolidatedEditLateHoursReport.pdf")}
        <#if consolidatedFinalMap?has_content>
        <#assign consolidatedDetails=consolidatedFinalMap.entrySet()>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="12pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold"> 
        			<fo:block text-align="right" keep-together="always" white-space-collapse="false">&#160;                                                                              DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>	 
	        		<fo:block text-align="right" keep-together="always" white-space-collapse="false">&#160;                                                          PAGE: <fo:page-number/></fo:block>      
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block text-align="center" keep-together="always" white-space-collapse="false">EDITED PAYABLE DAYS REPORT FOR THE MONTH OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateEnd, "MMM-yyyy")}</fo:block>	  
	        		<fo:block text-align="left" keep-together="always">---------------------------------------------------------------------------------------------------------</fo:block>
	        		<fo:block text-align="left" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">&#160;SNo     	Employee			Original	 		 Edited	   		Edited	         	Edit         	Edit       	Remarks  </fo:block>	 
	        		<fo:block text-align="left" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">&#160;       	     								Days      	 Days	      	 By            		Date         	Time                </fo:block>	 
	        		<fo:block text-align="left" keep-together="always">---------------------------------------------------------------------------------------------------------</fo:block>
          		</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	            <fo:block font-family="Courier,monospace">
	                <fo:table>
	                	<#assign sno=0>
	                	<#assign noofLines=1>
	                	<fo:table-column column-width="50pt"/>
	                    <fo:table-column column-width="90pt"/>
	                    <fo:table-column column-width="50pt"/>
	                    <fo:table-column column-width="80pt"/>
	                    <fo:table-column column-width="110pt"/>
	                    <fo:table-column column-width="110pt"/>
	                    <fo:table-column column-width="100pt"/>
	                    <fo:table-column column-width="180pt"/>
                     	<fo:table-body>
                     	<#list consolidatedDetails as consolidatedDet>
                     	<#assign newConsList = consolidatedDet.getValue()>
                     	<#list newConsList as newConsolidatedEntry>
                 	           <#assign sno=sno+1>
			         			<fo:table-row>
			         				<fo:table-cell>	
			                    		<fo:block text-align="center" keep-together="always" font-size="12pt">${sno}</fo:block>
			                   		</fo:table-cell>
			                   		<fo:table-cell>	
			                    		<fo:block text-align="center" keep-together="always" font-size="12pt">${consolidatedDet.getKey()?if_exists}</fo:block>
			                   		</fo:table-cell>
		                    		<#assign originalDays = 0>
		                    		<#assign originalDays = newConsolidatedEntry.get("oldValueText")?if_exists> 
		                    		<#if (originalDays)?has_content> 
		                    			<#assign oriPayDays = originalDays.substring(0,originalDays.indexOf(".")+3)>
		                    			<fo:table-cell>	
		                    				<fo:block text-align="right" keep-together="always" font-size="12pt"><#if oriPayDays?has_content>${oriPayDays?if_exists}<#else>0.00</#if></fo:block>
		                    			</fo:table-cell> 
		                    			<#assign oriPayDays = 0>
		                    		<#else>
		                    			<fo:table-cell>	
		                    				<fo:block text-align="right" keep-together="always" font-size="12pt">0</fo:block>
		                    			</fo:table-cell> 
		                    		</#if> 
		                    		<#assign editedPayDays = 0>
		                    		<#assign editedPayDays = newConsolidatedEntry.get("newValueText")?if_exists> 
		                    		<#if (editedPayDays)?has_content> 
		                    			<fo:table-cell>	
		                    				<fo:block text-align="right" keep-together="always" font-size="12pt"><#if editedPayDays?has_content>${editedPayDays?if_exists}<#else>0.00</#if></fo:block>
		                    			</fo:table-cell> 
		                    			<#assign editedPayDays = 0>
		                    		<#else>
		                    			<fo:table-cell>	
		                    				<fo:block text-align="right" keep-together="always" font-size="12pt">0</fo:block>
		                    			</fo:table-cell> 
		                    		</#if> 
		                    		<fo:table-cell>	
		                    			<fo:block text-align="right" keep-together="always" font-size="12pt"><#if newConsolidatedEntry.get("changedByInfo")?has_content>${newConsolidatedEntry.get("changedByInfo")?if_exists}<#else>0</#if></fo:block>
		                    		</fo:table-cell>
		                    		<fo:table-cell>	
										<fo:block text-align="right" keep-together="always" font-size="12pt"><#if newConsolidatedEntry.get("changedDate")?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(newConsolidatedEntry.get("changedDate") ,"dd/MM/yy")?if_exists}<#else></#if></fo:block>
		                    		</fo:table-cell>
		                    		<fo:table-cell>	
										<fo:block text-align="right" keep-together="always" font-size="12pt"><#if newConsolidatedEntry.get("changedDate")?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(newConsolidatedEntry.get("changedDate") ,"HH:mm")?if_exists}<#else></#if></fo:block>
		                    		</fo:table-cell>
		                    		<fo:table-cell>	
										<fo:block text-align="center" keep-together="always" font-size="12pt"></fo:block>
		                    		</fo:table-cell>
		                    		<#assign noofLines=noofLines+1>
		               			</fo:table-row>
			               		<#if (noofLines == 35)>
	                            	<fo:table-row>
	                            		<fo:table-cell>
	   										<fo:block page-break-after="always" font-weight="bold" font-size="12pt" text-align="center"></fo:block>
	   									</fo:table-cell>
									</fo:table-row>
	                           		<#assign noofLines =1>
								</#if>
								</#list>
		                 </#list>
			               		<fo:table-row>
			               			<fo:table-cell >	
			                			<fo:block keep-together="always" font-weight="bold">--------------------------------------------------------------------------------------------------------</fo:block>
			                		</fo:table-cell>
			               		</fo:table-row>
                     	</fo:table-body>
                      </fo:table>
                      <fo:table>
                     	<fo:table-column column-width="650pt"/>
                     		<fo:table-body>
	                     		<fo:table-row >
		                            <fo:table-cell>	
		                            	<fo:block keep-together="always" text-align="center">&#160; This is a system generated report and does not require any signature. </fo:block>
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
        			No Orders Found.......!
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>	
	</#if>
     </fo:root>
</#escape>
