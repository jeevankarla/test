<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="10in" page-width="10in"
                     margin-left="0.1in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="1.4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
		<#if shiftFinalMap?has_content>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="12pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                           CANTEEN REPORT FOR THE MONTH OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMMM yyyy")}         </fo:block>	 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                          PAGE: <fo:page-number/></fo:block>	 	 	  	 	  
	        		<fo:block text-align="left" keep-together="always"  >---------------------------------------------------------------------------------------------</fo:block>
	        	</fo:static-content>
	        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		            <fo:block font-family="Courier,monospace">
		                <fo:table >
		                	<#assign sNo=1>
		                	<fo:table-column column-width="40pt"/>
		                	<fo:table-column column-width="70pt"/>
		                	<fo:table-column column-width="200pt"/>
		                	<fo:table-column column-width="50pt"/>
		                	<fo:table-column column-width="60pt"/>
		                	<fo:table-column column-width="70pt"/>
		                	<fo:table-column column-width="40pt"/>
		                	<fo:table-column column-width="60pt"/>
		                	<fo:table-column column-width="70pt"/>
			                <fo:table-body> 
	                     		<fo:table-row >
	                     			<fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">SL</fo:block>
		                            </fo:table-cell>
		                           	<fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">EMP NO</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">NAME</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">SH-I</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">SH-II</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">SH-III</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">GEN</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">TOTALDAYS</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="right" keep-together="always" font-weight="bold" font-size="12pt">AMOUNT</fo:block>
		                            </fo:table-cell>
		                    	</fo:table-row>
		                    	<fo:table-row >
			                   		<fo:table-cell >	
	                            		<fo:block keep-together="always" font-weight="bold">---------------------------------------------------------------------------------------------</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                            <#assign shiftMap=shiftFinalMap.entrySet()>
			                   	<#list shiftMap as shiftMapValues>
			                   	<#assign totalDays=0>
			                   	<#assign amount=0>
			                   	<#assign finalAmount=0>
			                   		<fo:table-row >
		                     			<fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">${sNo}</fo:block>
			                            </fo:table-cell>
			                           	<fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">${shiftMapValues.getKey()}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell>	
                     		 				<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt" white-space-collapse="false">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, shiftMapValues.getKey(), false)}</fo:block>
			                            </fo:table-cell>
			                            <#if shift1finalMap.get(shiftMapValues.getKey())?exists>
			                            <#assign shift1Details=shift1finalMap.get(shiftMapValues.getKey()?if_exists)>
			                            <#assign shift1Days = 0>
			                           		<#if shift1Details?exists>
			                            		<#assign shift1Days = shift1Details.get("sh1noOfDays")>
			                          		  	<fo:table-cell >	
			                           				<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">${shift1Days}</fo:block>
			                           		 	</fo:table-cell>
			                           		 	<#assign totalDays=totalDays+shift1Days>
			                           		 	<#assign amount=shift1Days*17>
			                           		 	<#assign finalAmount=finalAmount+amount>
			                           		</#if>
			                           		<#else>
			                            		<fo:table-cell >	
			                           				<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">0</fo:block>
			                           			 </fo:table-cell>
			                            </#if>
			                            <#if shift2finalMap.get(shiftMapValues.getKey())?exists>
			                            <#assign shift2Details=shift2finalMap.get(shiftMapValues.getKey()?if_exists)>
			                            <#assign shift2Days = 0>
			                           		<#if shift2Details?exists>
			                            		<#assign shift2Days = shift2Details.get("sh2noOfDays")>
			                          		  	<fo:table-cell >	
			                           				<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">${shift2Days}</fo:block>
			                           		 	</fo:table-cell>
			                           		 	<#assign totalDays=totalDays+shift2Days>
			                           		 	<#assign amount=shift2Days*10>
			                           		 	<#assign finalAmount=finalAmount+amount>
			                           		</#if>
			                           		<#else>
			                            		<fo:table-cell >	
			                           				<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">0</fo:block>
			                           			 </fo:table-cell>
			                            </#if>
			                            <#if shift3finalMap.get(shiftMapValues.getKey())?exists>
			                            <#assign shift3Details=shift3finalMap.get(shiftMapValues.getKey()?if_exists)>
			                            <#assign shift3Days = 0>
			                           		<#if shift3Details?exists>
			                            		<#assign shift3Days = shift3Details.get("sh3noOfDays")>
			                          		  	<fo:table-cell >	
			                           				<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">${shift3Days}</fo:block>
			                           		 	</fo:table-cell>
			                           		 	<#assign totalDays=totalDays+shift3Days>
			                           		 	<#assign amount=shift3Days*0>
			                           		 	<#assign finalAmount=finalAmount+amount>
			                           		</#if>
			                           		<#else>
			                            		<fo:table-cell >	
			                           				<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">0</fo:block>
			                           			 </fo:table-cell>
			                            </#if>
			                            <#if genshiftfinalMap.get(shiftMapValues.getKey())?exists>
			                            <#assign genShiftDetails=genshiftfinalMap.get(shiftMapValues.getKey()?if_exists)>
			                            <#assign genShiftDays = 0>
			                           		<#if genShiftDetails?exists>
			                            		<#assign genShiftDays = genShiftDetails.get("shgennoOfDays")>
			                          		  	<fo:table-cell >	
			                           				<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">${genShiftDays}</fo:block>
			                           		 	</fo:table-cell>
			                           		 	<#assign totalDays=totalDays+genShiftDays>
			                           		 	<#assign amount=genShiftDays*10>
			                           		 	<#assign finalAmount=finalAmount+amount>
			                           		</#if>
			                           		<#else>
			                            		<fo:table-cell >	
			                           				<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">0</fo:block>
			                           			 </fo:table-cell>
			                            </#if>
			                            	<fo:table-cell>	
						                    	<fo:block text-align="right" keep-together="always" font-weight="bold" font-size="12pt">${totalDays}.00</fo:block>
						                    </fo:table-cell>
						                    <fo:table-cell>	
						                    	<fo:block text-align="right" keep-together="always" font-weight="bold" font-size="12pt">${finalAmount}.00</fo:block>
						                    </fo:table-cell>
		                    		</fo:table-row>
		                    		<#assign sNo=sNo+1>
		                    	</#list>
		                    	<fo:table-row >
			                   		<fo:table-cell >	
	                            		<fo:block keep-together="always" font-weight="bold">---------------------------------------------------------------------------------------------</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
		                    </fo:table-body>
                     	</fo:table>
                     	<fo:table>
                     		<fo:table-column column-width="650pt"/>
                     		<fo:table-body> 
	                     		<fo:table-row >
		                            <fo:table-cell >	
		                            	<fo:block keep-together="always" text-align="center">&#160; This is a System generated report and does not require any signature. </fo:block>
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
   		 		<fo:block font-size="12pt">
        			No Employee Found.......!
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>	
	</#if>
     </fo:root>
</#escape>