<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="10in" page-width="12in" margin-left="0.1in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="2.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "DayWiseEditLateHoursReport.pdf")}
        <#if attendanceDetailList?has_content>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="12pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block text-align="center" keep-together="always" white-space-collapse="false">&#160;   DAY WISE EDITED LATE HOURS REPORT FOR THE MONTH OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateEnd, "MMM-yyyy")}</fo:block>	  
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                              DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>	 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                              PAGE: <fo:page-number/></fo:block>	 	 	  	 	  
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;</fo:block>
	        		<fo:block text-align="left" keep-together="always">--------------------------------------------------------------------------------------------------------------------</fo:block>
	        		<fo:block text-align="left" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">&#160;SNo     Emp		     Date	   Original	  Edited	   Edited	         Edit       Edit       Remarks  </fo:block>	 
	        		<fo:block text-align="left" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">&#160;       Code	                Mins      Mins	      By            Date       Time                </fo:block>	 
	        		<fo:block text-align="left" keep-together="always">--------------------------------------------------------------------------------------------------------------------</fo:block>
          		</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	            <fo:block font-family="Courier,monospace">
	                <fo:table>
	                	<#assign sno=0>
	                	<#assign noofLines=1>
	                	<fo:table-column column-width="30pt"/>
	                    <fo:table-column column-width="80pt"/>
	                    <fo:table-column column-width="50pt"/>
	                    <fo:table-column column-width="60pt"/>
	                    <fo:table-column column-width="70pt"/>
	                    <fo:table-column column-width="120pt"/>
	                    <fo:table-column column-width="100pt"/>
	                    <fo:table-column column-width="60pt"/>
	                    <fo:table-column column-width="180pt"/>
                     	<fo:table-body>
                     	<#list attendanceDetailList as empAttendence>
                     	<#if empAttendence.get("lateMin")!=0>
                     		<#assign sno=sno+1>
				         			<fo:table-row>
				         				<fo:table-cell>	
				                    		<fo:block text-align="center" keep-together="always" font-size="12pt">${sno}</fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>	
				                    		<fo:block text-align="center" keep-together="always" font-size="12pt">${empAttendence.get("partyId")}</fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>	
				                    		<fo:block text-align="left" keep-together="always" font-size="12pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(empAttendence.get("date"), "dd/MM/yy")?if_exists}</fo:block>
				                    	</fo:table-cell>
				                    	<fo:table-cell>	
				                    		<fo:block text-align="right" keep-together="always" font-size="12pt"><#if empAttendence.get("lateMin")?has_content>${empAttendence.get("lateMin")?if_exists}<#else>0</#if></fo:block>
				                    	</fo:table-cell>
				                    	<fo:table-cell>	
				                    		<fo:block text-align="right" keep-together="always" font-size="12pt"><#if empAttendence.get("overrideLateMin")?has_content>${empAttendence.get("overrideLateMin")?if_exists}<#else>0</#if></fo:block>
				                    	</fo:table-cell>
				                    	<fo:table-cell>	
				                    		<fo:block text-align="right" keep-together="always" font-size="12pt"><#if empAttendence.get("overridenBy")?has_content>${empAttendence.get("overridenBy")?if_exists}<#else></#if></fo:block>
				                    	</fo:table-cell>
				                    	<fo:table-cell>	
											<fo:block text-align="right" keep-together="always" font-size="12pt"><#if empAttendence.get("lastUpdatedStamp")?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(empAttendence.get("lastUpdatedStamp") ,"dd/MM/yy")?if_exists}<#else></#if></fo:block>
				                    	</fo:table-cell>
				                    	<fo:table-cell>	
											<fo:block text-align="right" keep-together="always" font-size="12pt"><#if empAttendence.get("lastUpdatedStamp")?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(empAttendence.get("lastUpdatedStamp") ,"HH:mm")?if_exists}<#else></#if></fo:block>
				                    	</fo:table-cell>
				                    	<fo:table-cell>	
				                    		<fo:block text-align="center" keep-together="always" font-size="12pt"><#if empAttendence.get("overrideReason")?has_content>${empAttendence.get("overrideReason")?if_exists}<#else></#if></fo:block>
				                    	</fo:table-cell>
			                    		<#assign noofLines=noofLines+1>
			               			</fo:table-row>
			               		</#if>
			               		<#if (noofLines == 35)>
	                            	<fo:table-row>
	                            		<fo:table-cell>
	   										<fo:block page-break-after="always" font-weight="bold" font-size="12pt" text-align="center"></fo:block>
	   									</fo:table-cell>
									</fo:table-row>
	                           		<#assign noofLines =1>
								</#if>
			                 </#list>
			               		<fo:table-row>
			               			<fo:table-cell >	
			                			<fo:block keep-together="always" font-weight="bold">--------------------------------------------------------------------------------------------------------------</fo:block>
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
        			No Employee Found.......!
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>	
	</#if>
     </fo:root>
</#escape>
