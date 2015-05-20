<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
                     margin-left="0.4in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="0.6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "EmployeeMisPunchData.pdf")}
		
		<#if employeeWiseMap?has_content>
			<#assign SNo=1>
			<#assign noOfLines=1>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-family="Helvetica"  flow-name="xsl-region-before" font-weight="bold">        
	        		
          		</fo:static-content>
          		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
          			<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="12pt" line-height = "18pt">&#160; THE EMPLOYEES PROVIDENT FUND SCHEME, 1952</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold" line-height = "18pt">&#160; FORM 6A     </fo:block>
					<fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="11pt" line-height = "22pt">&#160;Statement of contribution for the period from ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MM-yyyy")}  to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MM-yyyy")}</fo:block> 
	        		<fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="11pt" line-height = "18pt">&#160; Name and Address of the Establishment : KMF, UNIT MOTHER DAIRY    </fo:block>
	        		<fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="11pt" line-height = "18pt">&#160; Statutory rate of Contribution  : 12 % </fo:block>
	        		<fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="11pt" line-height = "18pt">&#160; Code No of the Establishment  : KN/BG/BNG/11785   </fo:block>  
					<fo:block  keep-together="always" text-align="right" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="11pt" line-height = "18pt">&#160; Employee contribution  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;  Employer contribution   &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;  </fo:block>
					<fo:block>
                     	<fo:table >
                     		<#assign total = 0>
                     		<#assign totalEPF = 0>
                     		<#assign totalFpf = 0>
	                		<fo:table-column column-width="50pt"/>
		                	<fo:table-column column-width="50pt"/>
		                	<fo:table-column column-width="120pt"/>
		                	<fo:table-column column-width="58pt"/>
		                	<fo:table-column column-width="58pt"/>
		                	<fo:table-column column-width="58pt"/>
		                	<fo:table-column column-width="58pt"/>
		                	<fo:table-column column-width="58pt"/>
		                	<fo:table-column column-width="58pt"/>
		                	<fo:table-column column-width="58pt"/>
	                		<fo:table-body> 
                     			<fo:table-row >
	                     			<fo:table-cell >	
		                            	<fo:block text-align="left"  font-weight="bold" font-size="11pt" line-height = "35pt">SLNO</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" font-weight="bold" font-size="11pt" line-height = "35pt">A/C NO</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="center"  font-weight="bold" font-size="11pt" line-height = "35pt">NAME</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="right" font-weight="bold" font-size="11pt" line-height = "35pt">WAGES</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="right"  font-weight="bold" font-size="11pt" line-height = "35pt">E.P.F</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="right"  font-weight="bold" font-size="11pt" line-height = "35pt">V.P.F</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >
		                            	<fo:block text-align="right" font-weight="bold" font-size="11pt" line-height = "35pt">Total</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >
		                            	<fo:block text-align="right" font-weight="bold" font-size="11pt" line-height = "35pt">E.P.F</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >
		                            	<fo:block text-align="right" font-weight="bold" font-size="11pt" line-height = "35pt">F.P.F</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >
		                            	<fo:block text-align="right" font-weight="bold" font-size="11pt" line-height = "35pt">Total</fo:block>
		                            </fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row >
	                     			<fo:table-cell >	
		                            	<fo:block text-align="left"  font-weight="bold" font-size="10pt" line-height = "20pt">1</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" font-weight="bold" font-size="10pt" line-height = "20pt">2</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "20pt">3</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell>	
		                            	<fo:block text-align="right" font-weight="bold" font-size="10pt" line-height = "20pt">4</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell>	
		                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "20pt">5(a)</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell>	
		                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "20pt">5(b)</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >
		                            	<fo:block text-align="right" font-weight="bold" font-size="10pt" line-height = "20pt">5(c)</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >
		                            	<fo:block text-align="right" font-weight="bold" font-size="10pt" line-height = "20pt">6(a)</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >
		                            	<fo:block text-align="right" font-weight="bold" font-size="10pt" line-height = "20pt">6(b)</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >
		                            	<fo:block text-align="right" font-weight="bold" font-size="10pt" line-height = "20pt">6(c)</fo:block>
		                            </fo:table-cell>
			                   	</fo:table-row>
			                   	<#assign employeeWiseDetails = employeeWiseMap.entrySet()>
	            				<#list employeeWiseDetails as employeeVal>
	            					<fo:table-row >
		                     			<fo:table-cell>	
			                            	<fo:block text-align="left"  font-size="11pt" line-height = "20pt">${SNo?if_exists}</fo:block>
			                            	<#assign noOfLines = noOfLines + 1>
			                            	<#assign SNo = SNo + 1>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="left" font-size="11pt" line-height = "20pt">${employeeVal.getValue().get("pfAccNo")?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="left"  font-size="11pt" line-height = "20pt">${(Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(employeeVal.getValue().get("employeeName")?if_exists)),16))} </fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="right" font-size="11pt" line-height = "20pt"><#if employeeVal.getValue().get("monthTotWages")?has_content>${employeeVal.getValue().get("monthTotWages")?if_exists}<#else>0</#if></fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="right"  font-size="11pt" line-height = "20pt"><#if employeeVal.getValue().get("employeeEpf")?has_content>${employeeVal.getValue().get("employeeEpf")?if_exists}<#else>0</#if></fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >
			                            	<fo:block text-align="right" font-size="11pt" line-height = "20pt"><#if employeeVal.getValue().get("employeeVpf")?has_content>${employeeVal.getValue().get("employeeVpf")?if_exists}<#else>0</#if></fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >
			                            	<fo:block text-align="right" font-size="11pt" line-height = "20pt"><#if employeeVal.getValue().get("employeeTot")?has_content>${employeeVal.getValue().get("employeeTot")?if_exists}<#else>0</#if></fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >
			                            	<fo:block text-align="right" font-size="11pt" line-height = "20pt"><#if employeeVal.getValue().get("employerEpf")?has_content>${employeeVal.getValue().get("employerEpf")?if_exists}<#else>0</#if></fo:block>
			                            	<#assign totalEPF = totalEPF + employeeVal.getValue().get("employerEpf")>
			                            </fo:table-cell>
			                            <fo:table-cell >
			                            	<fo:block text-align="right" font-size="11pt" line-height = "20pt"><#if employeeVal.getValue().get("employerFpf")?has_content>${employeeVal.getValue().get("employerFpf")?if_exists}<#else>0</#if></fo:block>
			                            	<#assign totalFpf = totalFpf + employeeVal.getValue().get("employerFpf")>
			                            </fo:table-cell>
			                            <fo:table-cell >
			                            	<fo:block text-align="right" font-size="11pt" line-height = "20pt"><#if employeeVal.getValue().get("employerTot")?has_content>${employeeVal.getValue().get("employerTot")?if_exists}<#else>0</#if></fo:block>
			                            	<#assign total = total + employeeVal.getValue().get("employerTot")>
			                            </fo:table-cell>
				                   	</fo:table-row>
				                   	<#if noOfLines == 31>
				                 		<#assign noOfLines=1>
				                 		<fo:table-row >
				                 			<fo:table-cell >
				                 				<fo:block page-break-after="always"></fo:block>
				                 			</fo:table-cell>
				                   		</fo:table-row>
				                 	</#if>
	            				</#list>
	            				<#if finalTotalsMap?has_content>
	            					<#assign grandTotVal = finalTotalsMap.entrySet()>
	            					<#list grandTotVal as grnadTotals>
	            						<fo:table-row >
			                     			<fo:table-cell>	
				                            	<fo:block text-align="left"  font-size="11pt" line-height = "20pt" font-weight="bold">TOTAL :  &#160;</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="left" font-size="11pt" line-height = "20pt" font-weight="bold">&#160;</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="left"  font-size="11pt" line-height = "20pt" font-weight="bold">&#160;</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="right" font-size="11pt" line-height = "20pt" font-weight="bold">&#160;</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="right" font-size="11pt" line-height = "20pt" font-weight="bold"><#if grnadTotals.getValue().get("totEmployeeEpf")?has_content>${grnadTotals.getValue().get("totEmployeeEpf")?if_exists}<#else>0</#if></fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="right" font-size="11pt" line-height = "20pt" font-weight="bold"><#if grnadTotals.getValue().get("totEmployeeVpf")?has_content>${grnadTotals.getValue().get("totEmployeeVpf")?if_exists}<#else>0</#if></fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="right" font-size="11pt" line-height = "20pt" font-weight="bold"><#if grnadTotals.getValue().get("employeeTotal")?has_content>${grnadTotals.getValue().get("employeeTotal")?if_exists}<#else>0</#if></fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="right" font-size="11pt" line-height = "20pt" font-weight="bold">${totalEPF?if_exists}</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="right" font-size="11pt" line-height = "20pt" font-weight="bold">${totalFpf?if_exists}</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="right" font-size="11pt" line-height = "20pt" font-weight="bold">${total?if_exists}</fo:block>
				                            </fo:table-cell>
					                   	</fo:table-row>
	            					</#list>
	            				</#if>
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