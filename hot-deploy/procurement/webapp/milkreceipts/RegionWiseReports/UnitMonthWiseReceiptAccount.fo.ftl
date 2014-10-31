<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".2in">
                <fo:region-body margin-top=".5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "UnitMonthWiseReceiptAccount.txt")}
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
				<#assign facility = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>
                <fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;UNIT NAME : ${facility.getString("facilityName")?if_exists}   PERIOD FROM : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}    </fo:block>
				<fo:block font-size="6pt">--------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;MONTH		    TYPE		          QTY      AVG	  AVG	      QTY       TOTAL     TOTAL	</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;		         MILK		          LTS	     FAT%	 SNF%      KGS      KG-FAT    KG-SNF</fo:block>
				<fo:block font-size="6pt">--------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">       				   
						<fo:block font-family="Courier,monospace" font-size="6pt">
						 	<fo:table>
						 		<fo:table-column column-width="50pt"/>
           						<fo:table-column column-width="30pt"/>
           						<fo:table-column column-width="64pt"/>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-column column-width="50pt"/>
           						<fo:table-column column-width="45pt"/>
           						<fo:table-column column-width="45pt"/>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-body> 
           						<#assign dayTotals = finalTotalsMap.entrySet()>
					            <#list dayTotals as dayWiseSales>
						            <#assign productDetailsMap = dayWiseSales.getValue()> 
					             	<#assign eachProdDetails = productDetailsMap.entrySet()>
					             	<#list eachProdDetails as eachProdEntry>
						             	<#assign eachProd = eachProdEntry.getValue()>
	           							<#if eachProdEntry.getKey() == "TOT">
	           								
											
											
											
										  <#else>
										  <#assign dateKey = dayWiseSales.getKey()>
	           								 <#assign productId = eachProdEntry.getKey() >
       							   			 <#assign product = delegator.findOne("Product", {"productId" : productId}, true)>
       							   			 <#assign qtyLtrs = eachProd.get("recdQtyLtrs")>
       							   			 <#assign qtyKgs = eachProd.get("recdQtyKgs")>
       							   			 <#assign kgFat = eachProd.get("recdKgFat")>
       							   			 <#assign kgSnf = eachProd.get("recdKgSnf")>
           							   		<#if qtyKgs?has_content && qtyKgs!=0>
           							   		<#if eachProdEntry.getKey() != 'sour'>	 
	           									<fo:table-row>
	           								
		           								<fo:table-cell>
		           									<fo:block keep-together="always" text-align="left"  font-size="5pt">${((dayWiseSales.getKey()).toUpperCase())?if_exists}</fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" text-align="left"  font-size="7pt">${product.brandName?if_exists}</fo:block>
		           								</fo:table-cell>
		           								
		           								<fo:table-cell>
		           									<fo:block keep-together="always" text-align="right"  font-size="7pt"><#if qtyLtrs !=0>${qtyLtrs?if_exists?string("##0.0")}<#else></#if></fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" text-align="right"  font-size="7pt"><#if qtyKgs !=0>${((kgFat*100)/(qtyKgs))?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" text-align="right"  font-size="7pt"><#if qtyLtrs !=0>${((kgSnf*100)/(qtyKgs))?if_exists?string("##0.00")}<#else>0.0</#if></fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${qtyKgs?if_exists?string("##0.00")}</fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${kgFat?if_exists?string("##0.00")}</fo:block>
		           								</fo:table-cell>
		           								<fo:table-cell>
		           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${kgSnf?if_exists?string("##0.00")}</fo:block>
		           								</fo:table-cell>
											</fo:table-row>
											</#if>
											</#if>
											<#if eachProdEntry.getKey() == 'sour'>
											
       							   			 <#assign sQtyLtrs = eachProd.get("sQtyLtrs")>
       							   			 <#assign sQtyKgs = eachProd.get("sQtyKgs")>
       							   			 <#assign sKgFat = eachProd.get("sKgFat")>
       							   			 <#assign sKgSnf = eachProd.get("sKgSnf")>
       							   			 <#if sQtyKgs?has_content && sQtyKgs!=0>
       							   			 <#if (sQtyLtrs?has_content || sQtyKgs?has_content) && (sQtyLtrs != 0 || sQtyKgs != 0)  >
												<fo:table-row>
				       								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="left"  font-size="7pt">  ${((dayWiseSales.getKey()).toUpperCase())?if_exists}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="left"  font-size="7pt">SOUR/REJECT</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${sQtyLtrs?if_exists?string("##0.0")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt"><#if sQtyKgs !=0>${((sKgFat*100)/(sQtyKgs))?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt"><#if sKgSnf !=0>${((sKgSnf*100)/(sQtyKgs))?if_exists?string("##0.00")}<#else>0.0</#if></fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${sQtyKgs?if_exists?string("##0.00")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${sKgFat?if_exists?string("##0.00")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${sKgSnf?if_exists?string("##0.00")}</fo:block>
			           								</fo:table-cell>
				       							</fo:table-row>
				       							</#if>
				       							</#if>
											</#if>
										</#if>
									</#list>
								</#list>   
								<fo:table-row>
       								<fo:table-cell>
       									<fo:block font-size="6pt">--------------------------------------------------------------------------------------------</fo:block>
       								</fo:table-cell>
       							</fo:table-row>
							</fo:table-body>
			 			</fo:table>
					</fo:block>
					<#if grandTotalsMap?has_content>
					<fo:block font-family="Courier,monospace" font-size="6pt">
						 	<fo:table>
						 		<fo:table-column column-width="50pt"/>
           						<fo:table-column column-width="30pt"/>
           						<fo:table-column column-width="64pt"/>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-column column-width="50pt"/>
           						<fo:table-column column-width="45pt"/>
           						<fo:table-column column-width="45pt"/>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-column column-width="25pt"/>
           						<fo:table-body> 
           							
           						<#assign grandTotals = grandTotalsMap.entrySet()>
           							<#list grandTotals as grandTotSales>
           							  <#assign totProdDetailsMap = grandTotSales.getValue()> 
           							  <#if grandTotSales.getKey() != "TOT" >
           									
           							  		<#if grandTotSales.getKey() == "sour">
						   				    	<#assign totQtyLtrs = totProdDetailsMap.get("sQtyLtrs")>
						   			 	    	<#assign totQtyKgs = totProdDetailsMap.get("sQtyKgs")>
   							   			    	<#assign totKgFat = totProdDetailsMap.get("sKgFat")>
   							   			    	<#assign totKgSnf = totProdDetailsMap.get("sKgSnf")>
   							   			    <#else>
   							   			    	<#assign totQtyLtrs = totProdDetailsMap.get("recdQtyLtrs")>
	       							   			<#assign totQtyKgs = totProdDetailsMap.get("recdQtyKgs")>
	       							   			<#assign totKgFat = totProdDetailsMap.get("recdKgFat")>
	       							   			<#assign totKgSnf = totProdDetailsMap.get("recdKgSnf")>
   							   			    </#if>	
   							   			    <#if totQtyKgs?has_content && totQtyKgs!=0>
	           									<fo:table-row>
	       							   			    <#assign productId = grandTotSales.getKey() >
	       							   			    <#assign product = delegator.findOne("Product", {"productId" : productId}, true)>	
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="left"  font-size="7pt"></fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="left"  font-size="7pt"><#if grandTotSales.getKey() == "sour">SOUR MILK<#else>${product.brandName?if_exists}</#if></fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${totQtyLtrs?if_exists?string("##0.0")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt"><#if totQtyKgs !=0>${((totKgFat*100)/(totQtyKgs))?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt"><#if totQtyLtrs !=0>${((totKgSnf*100)/(totQtyLtrs))?if_exists?string("##0.00")}<#else>0.0</#if></fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${totQtyKgs?if_exists?string("##0.00")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${totKgFat?if_exists?string("##0.00")}</fo:block>
			           								</fo:table-cell>
			           								<fo:table-cell>
			           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${totKgSnf?if_exists?string("##0.00")}</fo:block>
			           								</fo:table-cell>
												</fo:table-row>
												</#if>
											</#if>
										</#list>
										<fo:table-row>
	           								<fo:table-cell>
	           									<fo:block font-size="6pt">--------------------------------------------------------------------------------------------</fo:block>
	           								</fo:table-cell>
										</fo:table-row>
										<#if grandTotalsMap?exists>	
												<#assign totProdDetailsMap = grandTotalsMap.get("TOT")>
												<#assign sourQtyLtrs = 0>
												<#assign sourQtyKgs = 0>
												<#assign sourKgFat = 0>
												<#assign sourKgSnf = 0>
												<#if grandTotalsMap.get('sour')?exists>
													<#assign sourQtyLtrs = grandTotalsMap.get('sour').get('sQtyLtrs')>
													<#assign sourQtyKgs = grandTotalsMap.get('sour').get('sQtyKgs')>
													<#assign sourKgFat = grandTotalsMap.get('sour').get('sKgFat')>
													<#assign sourKgSnf = grandTotalsMap.get('sour').get('sKgSnf')>
													</#if>
													<#assign grandQtyLtrs = (totProdDetailsMap.get("recdQtyLtrs")+(sourQtyLtrs))>
		   							   			    <#assign grandQtyKgs = (totProdDetailsMap.get("recdQtyKgs")+(sourQtyKgs))>
		   							   			    <#assign grandKgFat = (totProdDetailsMap.get("recdKgFat")+(sourKgFat))>
		   							   			    <#assign grandKgSnf = (totProdDetailsMap.get("recdKgSnf")+(sourKgSnf))>
				           							<fo:table-row>
				           								<fo:table-cell>
				           									<fo:block keep-together="always" text-align="left"  font-size="7pt">GRAND TOT:</fo:block>
				           								</fo:table-cell>
				           								<fo:table-cell>
				           									<fo:block keep-together="always" text-align="left"  font-size="7pt"></fo:block>
				           								</fo:table-cell>
				           								<fo:table-cell>
				           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${grandQtyLtrs?if_exists?string("##0.0")}</fo:block>
				           								</fo:table-cell>
				           								<fo:table-cell>
				           									<fo:block keep-together="always" text-align="right"  font-size="7pt"><#if grandQtyKgs !=0>${((grandKgFat*100)/(grandQtyKgs))?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
				           								</fo:table-cell>
				           								<fo:table-cell>
				           									<fo:block keep-together="always" text-align="right"  font-size="7pt"><#if grandQtyKgs !=0>${((grandKgSnf*100)/(grandQtyKgs))?if_exists?string("##0.00")}<#else>0.0</#if></fo:block>
				           								</fo:table-cell>
				           								<fo:table-cell>
				           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${grandQtyKgs?if_exists?string("##0.00")}</fo:block>
				           								</fo:table-cell>
				           								<fo:table-cell>
				           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${grandKgFat?if_exists?string("##0.00")}</fo:block>
				           								</fo:table-cell>
				           								<fo:table-cell>
				           									<fo:block keep-together="always" text-align="right"  font-size="7pt">${grandKgSnf?if_exists?string("##0.00")}</fo:block>
				           								</fo:table-cell>
													</fo:table-row>
												</#if>
												<fo:table-row>
				       								<fo:table-cell>
				       									<fo:block font-size="6pt">--------------------------------------------------------------------------------------------</fo:block>
				       								</fo:table-cell>
				       							</fo:table-row>
											
											</fo:table-body>
							 			</fo:table>
									</fo:block>
								</#if>
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