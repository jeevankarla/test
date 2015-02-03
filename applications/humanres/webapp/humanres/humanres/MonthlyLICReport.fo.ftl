<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
                     margin-left="0.6in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="0.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "LICPolicyDetailsEmployeeWise.pdf")}
		
		<#if finalMap?has_content>
			<fo:page-sequence master-reference="main">
        		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	        		<fo:block  keep-together="always" text-align="right" font-family="Helvetica" white-space-collapse="false" font-size="10pt" line-height = "18pt">Branch Address :.......................................................</fo:block>
	        		<fo:block  keep-together="always" text-align="right" font-family="Helvetica" white-space-collapse="false" font-size="10pt" line-height = "18pt">........................................................................................</fo:block>
	        		<fo:block  keep-together="always" text-align="right" font-family="Helvetica" white-space-collapse="false" font-size="10pt" line-height = "18pt">........................................................................................</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="10pt">______________________________________________________________________________________________________________________</fo:block>
		            <fo:block  keep-together="always" text-align="right" font-family="Helvetica" white-space-collapse="false" font-size="10pt">&#160;</fo:block>
		            
		            <fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false" font-size="10pt">Employer's Name&#160;<fo:inline text-decoration="underline" font-weight = "bold"> &#160;KARNATAKA MILK FEDERATION (UNIT MOTHER DAIRY)&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:inline> P.A.No.<fo:inline text-decoration="underline" font-weight = "bold">&#160;&#160;&#160;628461&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:inline></fo:block>
		            <fo:block  keep-together="always" text-align="right" font-family="Helvetica" white-space-collapse="false" font-size="10pt">&#160;</fo:block>
		            <fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false" font-size="10pt">Premium Reconciliation Statement under Salary Saving Scheme Policies for the month of.<fo:inline text-decoration="underline" font-weight = "bold">&#160;&#160;&#160;${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMMM-yyyy").toUpperCase()}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:inline> </fo:block>
		            <fo:block  keep-together="always" text-align="right" font-family="Helvetica" white-space-collapse="false" font-size="10pt">&#160;</fo:block>
		            <fo:block font-family="Helvetica">
	                	<fo:table border-style = "solid">
	                		<#assign totalAdditionAmount = 0>
	                		<#assign totalDeletionAmount = 0>
		                    <fo:table-column column-width="650pt"/>
	                     	<fo:table-body> 
                     			<fo:table-row >
	                           		<fo:table-cell >
	                           			<fo:block>
		                           			<fo:table>
		                           				<fo:table-column column-width="400pt"/>
		                           				<fo:table-column column-width="150pt"/>
		                           				<fo:table-column column-width="100pt"/>
		                           				<fo:table-body> 
						                     		<fo:table-row >
							                           	<fo:table-cell >
							                           		<fo:block text-align="left" keep-together="always" font-size="10pt"  font-weight="bold">&#160;</fo:block>
		                           						</fo:table-cell>
		                           						<fo:table-cell border-right-style="solid">
							                           		<fo:block text-align="right" keep-together="always" font-size="11pt"  font-weight="bold">Total Premium as per the Demand&#160;&#160;</fo:block>
		                           							<fo:block text-align="right" keep-together="always" font-size="10pt"  font-weight="bold">(Previous month Premium Balance)&#160;&#160;</fo:block>
		                           						</fo:table-cell>
		                           						<fo:table-cell >
							                           		<fo:block text-align="left" font-size="10pt">&#160;</fo:block>
							                           		<fo:block text-align="left" font-size="10pt">&#160; ${totalPreviousBalnace?if_exists}</fo:block>
		                           						</fo:table-cell>
								                   	</fo:table-row>
								                   	<fo:table-row >
							                           	<fo:table-cell >
							                           		<fo:block text-align="left" keep-together="always" font-size="11pt"  font-weight="bold">LESS policies deleted from the List</fo:block>
		                           						</fo:table-cell>
		                           						<fo:table-cell >
							                           		<fo:block text-align="right" keep-together="always" font-size="10pt" border-right-style="solid">&#160;</fo:block>
		                           						</fo:table-cell>
		                           						<fo:table-cell >
							                           		<fo:block text-align="left" font-size="10pt">&#160;</fo:block>
		                           						</fo:table-cell>
								                   	</fo:table-row>
								                   	
								              	</fo:table-body>
		                           			</fo:table>
		                           		</fo:block>
		                           		<fo:block>
		                           			<fo:table>
		                           				<fo:table-column column-width="100pt"/>
		                           				<fo:table-column column-width="360pt"/>
		                           				<fo:table-column column-width="90pt"/>
		                           				<fo:table-column column-width="100pt"/>
		                           				<fo:table-body> 
						                     		<fo:table-row border-top-style="solid" >
							                           	<fo:table-cell  border-right-style="solid">
							                           		<fo:block text-align="center" keep-together="always" font-size="11pt" font-weight = "bold">Policy Nos.</fo:block>
		                           							<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;</fo:block>
		                           							<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;</fo:block>
		                           						</fo:table-cell>
		                           						<fo:table-cell  border-right-style="solid">
							                           		<fo:block text-align="center" keep-together="always" font-size="10pt" font-weight = "bold">Reasons for deletion(if it is due to transfer to a</fo:block>
		                           							<fo:block text-align="center" keep-together="always" font-size="10pt" font-weight = "bold">different place, the new place of working with the name</fo:block>
		                           							<fo:block text-align="center" keep-together="always" font-size="10pt" font-weight = "bold">of the DISTRICT and TALUK should be mentioned)</fo:block>
		                           						</fo:table-cell>
		                           						<fo:table-cell border-right-style="solid">
							                           		<fo:block text-align="center" keep-together="always" font-size="10pt" font-weight = "bold">Premium</fo:block>
							                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;</fo:block>
							                           		<fo:block text-align="left" keep-together="always" font-size="10pt" font-weight = "bold">&#160;Rs&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Ps</fo:block>
		                           						</fo:table-cell>
		                           						<fo:table-cell >
							                           		<fo:block text-align="left" font-size="10pt">&#160;</fo:block>
		                           						</fo:table-cell>
								                   	</fo:table-row>
								              	</fo:table-body>
		                           			</fo:table>
		                           		</fo:block>
		                           		<fo:block>
		                           			<fo:table>
		                           				<fo:table-column column-width="100pt"/>
		                           				<fo:table-column column-width="100pt"/>
		                           				<fo:table-column column-width="200pt"/>
		                           				<fo:table-column column-width="60pt"/>
		                           				<fo:table-column column-width="90pt"/>
		                           				<fo:table-column column-width="100pt"/>
		                           				<fo:table-body> 
		                           					<#assign policyDetails = finalMap.entrySet()>
		                           					<#list policyDetails as policyEmployeeDetails>
		                           						<#if policyEmployeeDetails.getKey() == "policyDelitions">
		                           							<#assign policyDeletionsDetails = policyEmployeeDetails.getValue().entrySet()>
		                           							<#list policyDeletionsDetails as policyDeletionsList>
									                     		<fo:table-row border-top-style="solid" height = "20pt">
										                           	<fo:table-cell  border-right-style="solid">
										                           		<fo:block text-align="left" keep-together="always" font-size="11pt">&#160;${policyDeletionsList.getValue().get("policyNo")}</fo:block>
					                           						</fo:table-cell>
					                           						<fo:table-cell  border-right-style="solid">
										                           		<fo:block text-align="center" keep-together="always" font-size="10pt">${policyDeletionsList.getValue().get("partyId")?if_exists}</fo:block>
					                           						</fo:table-cell>
					                           						<fo:table-cell border-right-style="solid">
										                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;${policyDeletionsList.getValue().get("employeeFirstName")?if_exists}&#160;${policyDeletionsList.getValue().get("employeeMiddleName")?if_exists}&#160;${policyDeletionsList.getValue().get("employeeLastName")?if_exists}</fo:block>
					                           						</fo:table-cell>
					                           						<fo:table-cell border-right-style="solid">
										                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;</fo:block>
					                           						</fo:table-cell>
					                           						<fo:table-cell border-right-style="solid">
										                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;${policyDeletionsList.getValue().get("premiumAmount")?if_exists}&#160;</fo:block>
					                           							<#assign totalDeletionAmount = totalDeletionAmount + policyDeletionsList.getValue().get("premiumAmount")>
					                           						</fo:table-cell> 
					                           						<fo:table-cell border-right-style="solid">
										                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;</fo:block>
					                           						</fo:table-cell>
											                   	</fo:table-row>
											               	</#list>
										              	</#if>
									              	</#list>
									              	<fo:table-row border-top-style="solid" height = "20pt">
									              		<fo:table-cell  border-right-style="solid">
							                           		<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
		                           						</fo:table-cell>
		                           						<fo:table-cell  border-right-style="solid">
							                           		<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
		                           						</fo:table-cell>
		                           						<fo:table-cell>
							                           		<fo:block text-align="center" keep-together="always" font-size="10pt">Total Deletion</fo:block>
		                           						</fo:table-cell>
		                           						<fo:table-cell  border-right-style="solid">
							                           		<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
		                           						</fo:table-cell>
							                           	<fo:table-cell  border-right-style="solid">
							                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;${totalDeletionAmount?if_exists}</fo:block>
		                           						</fo:table-cell>
		                           						<fo:table-cell  border-right-style="solid">
							                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;${(totalPreviousBalnace - totalDeletionAmount)?if_exists}</fo:block>
		                           						</fo:table-cell>
		                           					</fo:table-row>
								              	</fo:table-body>
		                           			</fo:table>
		                           		</fo:block>
		                            </fo:table-cell>
			                   	</fo:table-row>
			              	</fo:table-body>
	                 	</fo:table>
			    	</fo:block>
			    	<fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false" font-size="10pt">&#160;</fo:block>
			    	<fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false" font-size="11pt" font-weight = "bold">Policies added to the List</fo:block>
		    	 	<fo:block>
			    	 	<fo:table border-style = "solid">
		                    <fo:table-column column-width="90pt"/>
               				<fo:table-column column-width="160pt"/>
               				<fo:table-column column-width="210pt"/>
               				<fo:table-column column-width="90pt"/>
               				<fo:table-column column-width="100pt"/>
	                     	<fo:table-body> 
	                     		<fo:table-row >
		                           	<fo:table-cell border-right-style="solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="11pt" font-weight = "bold">Policy Nos</fo:block>
               						</fo:table-cell>
               						<fo:table-cell border-right-style="solid">
               							<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" font-weight = "bold">Names</fo:block>
               						</fo:table-cell>
               						<fo:table-cell border-right-style="solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" font-weight = "bold">Reasons for Addition(if it is due</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" font-weight = "bold">to transfer from any other place</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" font-weight = "bold">the previous place of working with</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" font-weight = "bold">the name of the DISTRICT and TALUK</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" font-weight = "bold">should be mentioned.</fo:block>
               						</fo:table-cell>
               						<fo:table-cell border-right-style="solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" font-weight = "bold">Premium</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt" font-weight = "bold">&#160;Rs&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Ps</fo:block>
               						</fo:table-cell>
               						<fo:table-cell border-right-style="solid">
               							<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt">&#160;</fo:block>
		                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;Rs&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Ps</fo:block>
               						</fo:table-cell>
			                   	</fo:table-row>
			                   	
               					<#list policyDetails as policyEmployeeDetails>
               						<#if policyEmployeeDetails.getKey() == "policyAdditions">
               							<#assign policyAdditionDetails = policyEmployeeDetails.getValue().entrySet()>
               							<#list policyAdditionDetails as policyAdditionsList>
	               							<fo:table-row border-top-style="solid" height = "20pt">
	               								<fo:table-cell  border-right-style="solid">
					                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;${policyAdditionsList.getValue().get("policyNo")}</fo:block>
                           						</fo:table-cell>
                           						<fo:table-cell border-right-style="solid">
					                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;${policyAdditionsList.getValue().get("employeeFirstName")?if_exists}&#160;${policyAdditionsList.getValue().get("employeeMiddleName")?if_exists}&#160;${policyAdditionsList.getValue().get("employeeLastName")?if_exists}</fo:block>
                           						</fo:table-cell>
                           						<fo:table-cell border-right-style="solid">
					                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;</fo:block>
                           						</fo:table-cell>
                           						<fo:table-cell border-right-style="solid">
					                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;${policyAdditionsList.getValue().get("premiumAmount")?if_exists}&#160;</fo:block>
                           							<#assign totalAdditionAmount = totalAdditionAmount + policyAdditionsList.getValue().get("premiumAmount")>
                           						</fo:table-cell> 
                           						<fo:table-cell border-right-style="solid">
					                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;</fo:block>
                           						</fo:table-cell>
                           					</fo:table-row>
                           				</#list>
	                           						
               					<#--		  <#assign policyAdditionDetails = policyEmployeeDetails.getValue().entrySet()>
               							<#list policyAdditionDetails as policyAdditionsList>
				                     		<fo:table-row border-top-style="solid" height = "20pt">
					                           	<fo:table-cell  border-right-style="solid">
					                           		<fo:block text-align="center" keep-together="always" font-size="10pt">${policyAdditionsList.getKey()}</fo:block>
                           						</fo:table-cell>
                           						
                           						<fo:table-cell border-right-style="solid">
					                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;</fo:block>
                           						</fo:table-cell>
                           						<fo:table-cell border-right-style="solid">
					                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;${policyAdditionsList.getValue().get("premiumAmount")?if_exists}</fo:block>
                           						</fo:table-cell> 
                           						<fo:table-cell border-right-style="solid">
					                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;</fo:block>
                           						</fo:table-cell>
						                   	</fo:table-row>
						               	</#list>  -->
						               		
					              	</#if> 
				              	</#list>
				              	<fo:table-row border-top-style="solid" height = "20pt">
				              		<fo:table-cell  border-right-style="solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
               						</fo:table-cell>
               						<fo:table-cell  border-right-style="solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
               						</fo:table-cell>
               						<fo:table-cell border-right-style="solid">
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt">Total Addition</fo:block>
               						</fo:table-cell>
		                           	<fo:table-cell  border-right-style="solid">
		                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;${totalAdditionAmount?if_exists}</fo:block>
               						</fo:table-cell>
               						<fo:table-cell  border-right-style="solid">
		                           		<fo:block text-align="left" keep-together="always" font-size="10pt">&#160;${((totalPreviousBalnace - totalDeletionAmount)+totalAdditionAmount)?if_exists}</fo:block>
               						</fo:table-cell>
               					</fo:table-row>
			              	</fo:table-body>
	                 	</fo:table>
		    		</fo:block>
		    		<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
		    		
		    		<fo:block>
		    			<fo:table border-style="solid" >
		    				<fo:table-column column-width="90pt"/>
               				<fo:table-column column-width="160pt"/>
               				<fo:table-column column-width="210pt"/>
               				<fo:table-column column-width="90pt"/>
               				<fo:table-column column-width="100pt"/>
		    				<fo:table-body>
		    					<fo:table-row height = "20pt">
		    						<fo:table-cell>
		                           		<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
               						</fo:table-cell>
               						<fo:table-cell>
		                           		<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
               						</fo:table-cell>
               						<fo:table-cell>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt">Total Amount</fo:block>
               						</fo:table-cell>
				              		<fo:table-cell>
		                           		<fo:block text-align="center" keep-together="always" font-size="11pt">&#160;</fo:block>
               						</fo:table-cell>
               						<fo:table-cell>
		                           		<fo:block text-align="center" keep-together="always" font-size="10pt">${((totalPreviousBalnace - totalDeletionAmount)+totalAdditionAmount)?if_exists}</fo:block>
               						</fo:table-cell>
               					</fo:table-row>
		    				</fo:table-body>
		    			</fo:table>
		    		</fo:block>
		    		<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
		    		<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
		    		<fo:block text-align="left" keep-together="always" font-size="10pt">Encl.Cheque No...................................................... Date...................................</fo:block>
		    		<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
		    		<fo:block>
		    			<fo:table>
		    				<fo:table-column column-width="325pt"/>
               				<fo:table-column column-width="325pt"/>
               				<fo:table-body>
		    					<fo:table-row height = "20pt">
		    						<fo:table-cell>
		                           		<fo:block text-align="left" keep-together="always" font-size="11pt">Drawn on ..............................................................................................</fo:block>
               						</fo:table-cell>
               						<fo:table-cell>
		                           		<fo:block text-align="right" keep-together="always" font-size="11pt">Signature of the Employer</fo:block>
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
        			No Employee Found.......!
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>	
	</#if>
     </fo:root>
</#escape>
