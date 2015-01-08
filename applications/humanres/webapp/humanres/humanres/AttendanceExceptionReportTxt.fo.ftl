<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
                     margin-left="0.1in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="0.8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
		<#assign noofLines=1>
		<#assign organizationDetails = delegator.findOne("PartyGroup", {"partyId" : parameters.partyId}, true)>
        <#assign oragnizationId = organizationDetails.get("comments")>
		<fo:page-sequence master-reference="main">
    		<fo:static-content font-size="14pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
				<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">ANDHRA PRADESH DAIRY DEVELOPMENT CO-OP. FEDERATION LIMITED</fo:block>
        		<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">ATTENDANCE NOT GIVEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateStart, "MMMM-yyyy").toUpperCase()}       SHED CODE : ${oragnizationId?if_exists}</fo:block>	
        		<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;      </fo:block> 
        		<fo:block text-align="left" keep-together="always" font-size="8pt">---------------------------------------------------------------------------------------------</fo:block>
      		</fo:static-content>
      		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
            	<fo:block font-family="Courier,monospace">
                	<fo:table >
	                	<fo:table-column column-width="45pt"/>
	                    <fo:table-column column-width="45pt"/>
	                    <fo:table-column column-width="45pt"/>
	                    <fo:table-column column-width="50pt"/>
	                    <fo:table-column column-width="50pt"/>
	                    <fo:table-column column-width="110pt"/>
	                    <fo:table-column column-width="80pt"/>
                		<fo:table-body> 
                     		<fo:table-row >
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" font-size="5pt">SHED</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" font-size="5pt">UNIT</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" font-size="5pt">COST</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" font-size="5pt">GISNO</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" font-size="5pt">EMPNO</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" font-size="5pt">NAME</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" font-size="5pt">DESIGNATION</fo:block>
	                            </fo:table-cell>
	                       	</fo:table-row>
	                       	<fo:table-row >
		                   		<fo:table-cell >	
                            		<fo:block keep-together="always">---------------------------------------------------------------------------------------------</fo:block>
                            	</fo:table-cell>
                            </fo:table-row>
                            <#if attendanceExceptionMap?has_content>
	                            <#assign attendanceExceptionDetails = attendanceExceptionMap.entrySet()> 
	                            <#list attendanceExceptionDetails as attendanceDetails>
	            					<#assign emplPositionAndFulfilment=delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : attendanceDetails.getKey()})/>
	            					<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPositionAndFulfilment[0].emplPositionTypeId?if_exists}, true)>
	            					<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
	            					<fo:table-row >
			                            <fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-size="5pt"><#if oragnizationId?has_content>${oragnizationId?if_exists}<#else>&#160;</#if></fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-size="5pt">${attendanceDetails.getValue().get("unit")?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-size="5pt">${attendanceDetails.getValue().get("costCode")?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-size="5pt">${attendanceDetails.getValue().get("GISNo")?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-size="5pt">${Static["org.ofbiz.party.party.PartyServices"].getPartyInternal(delegator, attendanceDetails.getKey())}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-size="5pt">${(attendanceDetails.getValue().get("partyName")).toUpperCase()?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-size="5pt"><#if designationName?has_content>${designationName?if_exists}<#else><#if designation?has_content>${designation.description?if_exists}</#if></#if></fo:block>
			                            </fo:table-cell>
			                       	</fo:table-row>
	                            </#list>
                            <#else>
					       		<fo:table-row >
				            		<fo:table-cell >
				            			<fo:block font-size="4pt" text-align="left">No Records Found</fo:block>
				            		</fo:table-cell >
				            	</fo:table-row >
				           	</#if>
				           	<fo:table-row >
		                   		<fo:table-cell >	
                            		<fo:block keep-together="always">---------------------------------------------------------------------------------------------</fo:block>
                            	</fo:table-cell>
                            </fo:table-row>
                       </fo:table-body>
                 	</fo:table>
             	</fo:block>
			</fo:flow>
		</fo:page-sequence>
     </fo:root>
</#escape>