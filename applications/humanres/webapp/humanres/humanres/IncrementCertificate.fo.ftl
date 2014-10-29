<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="11in" page-width="14in"
                     margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="3.2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
		<#if EmployeeFinalMap?has_content>
			<#assign noofLines=1>
			<#assign sNo=1>
			<#assign employeeDetails=EmployeeFinalMap.entrySet()>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before">        
	        		<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
	        		<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                            PERIODICAL INCREMENT CERTIFICATE                     DATE   : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>
	        		<fo:block keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">PAGE NO : <fo:page-number/> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;    </fo:block>	
          			<fo:block keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;CERTIFIED THAT EMPLOYEES NAMED BELOW ARE ALLOWED THE SANCTIONED PERIODICAL INCREMENTS</fo:block> 
	        		<fo:block keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;FROM THE DATE CITED IN COLUMN NO.8 FOR APPROVED SERVICE</fo:block>
	        		<fo:block keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="11pt">&#160; </fo:block>
	        		<fo:block keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="11pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; 1. Having been incumbent of the appointment specified for not less than one year from the date of column 05</fo:block>
	        		<fo:block keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="11pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; after deducting period of suspension and on absence on Leave without pay.</fo:block>
	        		<fo:block keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="11pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; 2. Being entitled to the increments, as and when in explanatory memo attached. </fo:block>
          			<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
          			<fo:block font-family="Courier,monospace">
	                	<fo:table border-style="solid">
		                	<fo:table-column column-width="30pt"/>
		                    <fo:table-column column-width="60pt"/>
		                    <fo:table-column column-width="100pt"/>                
		                    <fo:table-column column-width="120pt"/>
		                    <fo:table-column column-width="260pt"/>
		                    <fo:table-column column-width="70pt"/>
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="80pt"/>                
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="80pt"/>
	                		<fo:table-body> 
	                     		<fo:table-row >
		                            <fo:table-cell border-style="solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">SNo </fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style="solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">Emp Code</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style="solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">Name of Incumbent</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style="solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">Designation</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style="solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">SCALE OF PAY</fo:block>
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">Maximum rate of increment</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style="solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">Per pay</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style="solid">	
		                            	<fo:block text-align="left" font-weight="bold" font-size="11pt">Dt.Of Last Increment of appt of cost</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style="solid">	
		                            	<fo:block text-align="left" font-weight="bold" font-size="11pt">Present Pay</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style="solid">	
		                            	<fo:block text-align="left" font-weight="bold" font-size="11pt">Dt.Of Present Increment</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style="solid">	
		                            	<fo:block text-align="left" font-weight="bold" font-size="11pt">Pay after adding Present Increment</fo:block>
		                            </fo:table-cell>
		                       	</fo:table-row>
		                  	</fo:table-body>
                     	</fo:table>
                 	</fo:block>
          		</fo:static-content>
          		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	                <fo:block font-family="Courier,monospace">
	                	<fo:table border-style="solid">
		                	<fo:table-column column-width="30pt"/>
		                    <fo:table-column column-width="60pt"/>
		                    <fo:table-column column-width="100pt"/>                
		                    <fo:table-column column-width="120pt"/>
		                    <fo:table-column column-width="260pt"/>
		                    <fo:table-column column-width="70pt"/>
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="80pt"/>                
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="80pt"/>
	                		<fo:table-body> 
		                       	<#list employeeDetails as employeeValues>
		                       		<fo:table-row >
		                       			<fo:table-cell border-style="solid">	
			                            	<fo:block text-align="center" font-size="11pt">${sNo?if_exists}</fo:block>
			                            	<#assign sNo=sNo+1>
			                            </fo:table-cell>
			                            <fo:table-cell border-style="solid">	
			                            	<fo:block text-align="center" font-size="11pt">${employeeValues.getKey()?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-style="solid">	
			                            	<fo:block text-align="left" font-size="11pt">${employeeValues.getValue().get("partyName")?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-style="solid">	
			                            	<fo:block text-align="left" font-size="11pt">${employeeValues.getValue().get("Designation")?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-style="solid">	
			                            	<fo:block text-align="left" font-size="11pt">${employeeValues.getValue().get("presentPayScale")?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-style="solid">	
			                            	<fo:block text-align="right" font-size="11pt">${employeeValues.getValue().get("PersonalPay")?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-style="solid">	
			                            	<fo:block text-align="center" font-size="11pt">${employeeValues.getValue().get("dateOfLastIncre")?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-style="solid">	
			                            	<fo:block text-align="right" font-size="11pt">${employeeValues.getValue().get("LastPay")?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-style="solid">	
			                            	<fo:block text-align="center" font-size="11pt">${employeeValues.getValue().get("dateOfPresentIncre")?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-style="solid">	
			                            	<fo:block text-align="right" font-size="11pt">${employeeValues.getValue().get("presentPay")?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <#assign noofLines=noofLines+1>
		                       		</fo:table-row>
		                       		<#if (noofLines >= 16)>
			                     		<#assign noofLines=1>
				                     	<fo:table-row>
			                            	<fo:table-cell >	
			                            		<fo:block page-break-after="always"></fo:block>
			                            	</fo:table-cell>
			                            </fo:table-row>
			                        </#if>
		                       	</#list>
                           </fo:table-body>
                     	</fo:table>
                 	</fo:block>
                 	<fo:block font-family="Courier,monospace">
	                	<fo:table>
		                	<fo:table-column column-width="1030pt"/>
	                		<fo:table-body> 
	                			<fo:table-row>
	                            	<fo:table-cell >	
	                            		<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                			<fo:table-row>
	                            	<fo:table-cell >	
	                            		<fo:block linefeed-treatment="preserve" font-size="11pt">&#160;&#160;1. Certified that the increment claimed in respect of the above said employees have been verified with reference to the</fo:block>
	                            		<fo:block linefeed-treatment="preserve" font-size="11pt">&#160;&#160;"SERVICE REGISTER" and found correct.</fo:block>
	                            		<fo:block linefeed-treatment="preserve" font-size="11pt">&#160;&#160;2. Certified that the employees were not on LWA during the incremental period.</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                            	<fo:table-cell >	
	                            		<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                            	<fo:table-cell >	
	                            		<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                            	<fo:table-cell >	
	                            		<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                            	<fo:table-cell >	
	                            		<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
                 				<fo:table-row>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="left" font-size="11pt">Signature of Admin Officer &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Signature of Director</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                      	</fo:table-body>
                     	</fo:table>
                 	</fo:block>
				</fo:flow>
  			</fo:page-sequence>
  		<#else>
	  		<fo:page-sequence master-reference="main">
		    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
		       		 <fo:block font-size="14pt">
		            	${uiLabelMap.NoEmployeeFound}.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
  		</#if>
     </fo:root>
</#escape>