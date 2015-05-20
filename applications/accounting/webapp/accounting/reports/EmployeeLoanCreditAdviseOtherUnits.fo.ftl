<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="10in" page-width="10in"
                     margin-left="0.5in" margin-right="0.5in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="1.4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
		<#if loanTypeEmplMap?has_content>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-family="Arial"  flow-name="xsl-region-before" font-weight="bold">        
	        		<fo:block  keep-together="always" text-align="center" font-family="Arial" white-space-collapse="false" font-weight="bold" font-size="12pt">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Arial" white-space-collapse="false" font-weight="bold" font-size="12pt">${uiLabelMap.KMFDairySubHeader}</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Arial" white-space-collapse="false" font-weight="bold" font-size="12pt">&#160;      </fo:block>
	        		<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-size="13pt">Employee Loan credit advise other units/unions for the month of ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMMM yyyy")}         </fo:block>	 
	        		<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="12pt"> PAGE: <fo:page-number/>&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>	 	 	  	 	  
	        		<fo:block text-align="left" keep-together="always"  >----------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	        	</fo:static-content>
	        	<fo:flow flow-name="xsl-region-body" font-family="Arial">
	        		<#assign loanTypeDetails = loanTypeEmplMap.entrySet()>
	        		<#list loanTypeDetails as loanType>
		            	<fo:block font-family="Arial">
		                	<fo:table >
		                		<#assign sNo=1>
			                	<fo:table-column column-width="150pt"/>
			                	<fo:table-column column-width="180pt"/>
			                	<fo:table-column column-width="180pt"/>
			                	<fo:table-body> 
			                		<fo:table-row >
		                     			<fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt" line-height = "23pt">Loan Type : ${loanType.getKey()?if_exists}</fo:block>
			                            </fo:table-cell>
			                    	</fo:table-row>
		                     		<fo:table-row >
		                     			<fo:table-cell >	
			                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="12pt" line-height = "23pt">EmployeeId</fo:block>
			                            </fo:table-cell>
			                           	<fo:table-cell >	
			                            	<fo:block text-align="right" keep-together="always" font-weight="bold" font-size="12pt" line-height = "23pt">Recovery Amount</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="right" keep-together="always" font-weight="bold" font-size="12pt" line-height = "23pt">prevIntNumber</fo:block>
			                            </fo:table-cell>
			                    	</fo:table-row>
			                    	<#assign emplLoanDetails = loanType.getValue().entrySet()>
			                    	<#list emplLoanDetails as emplLoan>
			                    		<fo:table-row >
			                     			<fo:table-cell >	
				                            	<fo:block text-align="center" keep-together="always" font-size="12pt" line-height = "18pt">${emplLoan.getKey()?if_exists}</fo:block>
				                            </fo:table-cell>
				                           	<fo:table-cell >	
				                            	<fo:block text-align="right" keep-together="always" font-size="12pt" line-height = "18pt">${emplLoan.getValue().get("recoveryAmount")?if_exists}</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="right" keep-together="always" font-size="12pt" line-height = "18pt">${emplLoan.getValue().get("principalInstNum")?if_exists}</fo:block>
				                            </fo:table-cell>
				                    	</fo:table-row>
			                    	</#list>
			                    	<fo:table-row >
			                     		<fo:table-cell >
			                    			<fo:block page-break-after="always"></fo:block>
			                    		</fo:table-cell>
				                    </fo:table-row>
	                           </fo:table-body>
	                     	</fo:table>
	             		</fo:block>
	             	</#list>
           		</fo:flow>
  			</fo:page-sequence>
  		<#else>    	
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Arial">
   		 		<fo:block font-size="12pt">
        			No Employee Found.......!
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>	
	</#if>
     </fo:root>
</#escape>