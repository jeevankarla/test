<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".2in">
                <fo:region-body margin-top=".5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "DayWiseAbstract.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>      
	<#if finalTotalsMap?has_content>  
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="7pt">
                <fo:block text-align="left" white-space-collapse="false" keep-together="always">.        ABSTRACT FOR DAY-WISE RECEIPTS PERIOD FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}    </fo:block>
				<fo:block font-size="6pt">-------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">DATED		  TYPE OF MILK		 QTY.LTS		   QTY.KGS		   KG FAT		    KGSNF</fo:block>
				<fo:block font-size="6pt">-------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">       				   
						<fo:block font-family="Courier,monospace" font-size="7pt">
						 	<fo:table>
						 		<fo:table-column column-width="50pt"/>
           						<fo:table-column column-width="46pt"/>
           						<fo:table-column column-width="45pt"/>
           						<fo:table-column column-width="60pt"/>
           						<fo:table-column column-width="55pt"/>
           						<fo:table-column column-width="55pt"/>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-body>
           						<#assign noOfDays = 8>
           						<#assign dayCount = 1>	 
           						<#assign dayTotals = finalTotalsMap.entrySet()>
					            <#list dayTotals as dayWiseSales>
						            <#assign productDetailsMap = dayWiseSales.getValue()> 
					             	<#assign eachProdDetails = productDetailsMap.entrySet()>
					             	<#list eachProdDetails as eachProdEntry>
						             	<#assign eachProd = eachProdEntry.getValue()>
						             	<#if (dayWiseSales.getKey() != "TOT")>
		           							<#if (eachProdEntry.getKey() == "TOT")>
		           								<fo:table-row>
			           								<fo:table-cell>
			           									<fo:block font-size="7pt">------------------------------------------------------------------------------</fo:block>
			           								</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<#assign dateKey = dayWiseSales.getKey()>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="left"  font-size="7pt">${dateKey.substring(8)?if_exists}/${dateKey.substring(5,7)?if_exists}/${dateKey.substring(0,4)?if_exists}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="left"  font-size="7pt"><#if dateKey!="TOT">DAY TOTAL:<#else>GRAND TOTAL: </#if></fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdQtyLtrs")?if_exists?string("##0.0")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdQtyKgs")?if_exists?string("##0.0")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdKgFat")?if_exists?string("##0.00")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
			           								</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
			           								<fo:table-cell>
			           									<fo:block font-size="7pt">------------------------------------------------------------------------------</fo:block>
			           								</fo:table-cell>
												</fo:table-row>
												<#assign dayCount= dayCount+1>
		           							 <#else>
		           							 <#if eachProd.get("recdQtyLtrs")!=0>
		           							 <#if (dayCount > noOfDays)> 
							        				<#assign dayCount = 1>		          				
							           				<fo:table-row>
							        					<fo:table-cell>	          				
							           						<fo:block font-family="Courier,monospace" font-size="10pt" break-before="page"/>     
							           					</fo:table-cell>
													</fo:table-row>           				
							           			</#if>
		           							 
		           							 
		           								<fo:table-row>
			           								<#assign dateKey = dayWiseSales.getKey()>
			           								 <#assign productId = eachProdEntry.getKey() >
	           							   			 <#assign product = delegator.findOne("Product", {"productId" : productId}, true)>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="left"  font-size="7pt"><#if dateKey=="TOT">${product.productId} </#if>  ${dateKey.substring(8)?if_exists}/${dateKey.substring(5,7)?if_exists}/${dateKey.substring(0,4)?if_exists}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="left"  font-size="7pt">${product.brandName?if_exists}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdQtyLtrs")?if_exists?string("##0.0")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdQtyKgs")?if_exists?string("##0.0")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdKgFat")?if_exists?string("##0.00")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
			           								</fo:table-cell>
												</fo:table-row>
												</#if>
		           							</#if>
	           							</#if>
									</#list>
								</#list> 
									<#assign grandTotProdMap = finalTotalsMap.get("TOT")> 
					             	<#assign totProdDetails = grandTotProdMap.entrySet()>
					             	<#list totProdDetails as totProdEntry>
						             	<#assign eachProd = totProdEntry.getValue()>
		           							<#if (totProdEntry.getKey() == "TOT")>
		           								<fo:table-row>
			           								<fo:table-cell>
			           									<fo:block font-size="7pt">------------------------------------------------------------------------------</fo:block>
			           								</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="left"  font-size="7pt">GRAND TOTAL:</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="left"  font-size="7pt"></fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdQtyLtrs")?if_exists?string("##0.0")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdQtyKgs")?if_exists?string("##0.0")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdKgFat")?if_exists?string("##0.00")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
			           								</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
			           								<fo:table-cell>
			           									<fo:block font-size="7pt">------------------------------------------------------------------------------</fo:block>
			           								</fo:table-cell>
												</fo:table-row>
		           							 <#else>
		           							  <#if eachProd.get("recdQtyLtrs")!=0>
		           								<fo:table-row>
			           								<#assign dateKey = "TOT">
			           								 <#assign productId = totProdEntry.getKey() >
	           							   			 <#assign product = delegator.findOne("Product", {"productId" : productId}, true)>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="left"  font-size="7pt"><#if dateKey=="TOT">${product.productId} </#if>   ${dateKey?if_exists}</fo:block>
			           								</fo:table-cell>															
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="left"  font-size="7pt">${product.brandName?if_exists}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdQtyLtrs")?if_exists?string("##0.0")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdQtyKgs")?if_exists?string("##0.0")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdKgFat")?if_exists?string("##0.00")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${eachProd.get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
			           								</fo:table-cell>
												</fo:table-row>
												</#if>
		           							</#if>
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