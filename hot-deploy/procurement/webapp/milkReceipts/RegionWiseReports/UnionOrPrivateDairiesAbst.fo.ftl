<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".2in">
                <fo:region-body margin-top=".7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "UnionOrPrivate.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>      
	<#if privateDairiesMap?has_content>  
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="7pt">
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;               THE ANDHRA PRADESH DAIRY DEVELOPMENT COOPERATIVE FEDERATION LIMITED.</fo:block>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                                         LALAPET : HYDERABAD      </fo:block>
                <fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;         MILK RECEIVED DETAILS FROM THE UNIONS/OTHERS UNITS AT M.P.F, HYDERABA PERIOD FROM  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}    </fo:block>
				<fo:block>--------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">CODE	NAME OF THE DAIRY 	  MILK TYPE		  QTY.LTS	     QTY.KGS	    KG FAT      KGSNF    TOT.SOLIDS   AVG.FAT  AVG.SNF   MILK-AMOUNT      OPCOST   GROSS.AMOUNT AVG.RATE</fo:block>
				<fo:block>--------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">       				   
						<fo:block font-family="Courier,monospace" font-size="7pt">
						 	<fo:table >
						 		<fo:table-column column-width="20pt"/>
           						<fo:table-column column-width="100pt"/>
           						<fo:table-column column-width="100pt"/>
           						<fo:table-column column-width="100pt"/>  
           						<fo:table-column column-width="100pt"/>  
           						<fo:table-column column-width="100pt"/>  
           						<fo:table-column column-width="100pt"/>           						
           						<fo:table-body> 
           						<#assign recdTotQtyLtrs=0>
       							<#assign recdTotQtyKgs=0>
       							<#assign recdTotKgFat=0>
       							<#assign recdTotKgSnf=0>
       							<#assign mlkTotAmt=0>
       							<#assign grossTotAmt=0>
           						<#assign privateDairiesList = privateDairiesMap.entrySet()>
           							<#list privateDairiesList as privateDairies>
           								<#assign productWiseDetailsList = privateDairies.getValue().entrySet()>
           								<#assign facility = delegator.findOne("Facility", {"facilityId" : privateDairies.getKey()}, true)>
						           		<fo:table-row>						           			
						           			<fo:table-cell>
						           				<fo:block >
						           					<fo:table >
						           						<fo:table-column column-width="20pt"/>
           												<fo:table-column column-width="100pt"/>
						           						<fo:table-column column-width="25pt"/>
						           						<fo:table-column column-width="53pt"/>
						           						<fo:table-column column-width="53pt"/>
						           						<fo:table-column column-width="45pt"/>
						           						<fo:table-column column-width="50pt"/>
						           						<fo:table-column column-width="55pt"/>
						           						<fo:table-column column-width="40pt"/>
						           						<fo:table-column column-width="30pt"/>
						           						<fo:table-column column-width="70pt"/>	
						           						<fo:table-column column-width="50pt"/>	
						           						<fo:table-column column-width="60pt"/>	
						           						<fo:table-column column-width="40pt"/>	
						           						<fo:table-column column-width="50pt"/>	
						           						<fo:table-column column-width="50pt"/>	
						           						<fo:table-column column-width="50pt"/>						           						
						           						<fo:table-body>						           							
						           							<#list productWiseDetailsList as productList>
						           								<#if productList.getValue().get("recdQtyKgs") !=0>
								           							<fo:table-row>
								           								<#assign product = delegator.findOne("Product", {"productId" : productList.getKey()}, true)>
								           								<#assign recdTotQtyLtrs=recdTotQtyLtrs+productList.getValue().get("recdQtyLtrs")>
									           							<#assign recdTotQtyKgs=recdTotQtyKgs+productList.getValue().get("recdQtyKgs")>
									           							<#assign recdTotKgFat=recdTotKgFat+productList.getValue().get("recdKgFat")>
									           							<#assign recdTotKgSnf=recdTotKgSnf+productList.getValue().get("recdKgSnf")>	
									           							<fo:table-cell>
													           				<fo:block>${facility.mccCode?if_exists}</fo:block>
													           			</fo:table-cell>
													           			<fo:table-cell>
													           				<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName")?if_exists)),17)}</fo:block>
													           			</fo:table-cell>								           							
								           								<fo:table-cell>
								           									<fo:block keep-together="always">${product.brandName?if_exists}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${productList.getValue().get("recdQtyLtrs")?if_exists?string("##0.0")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${productList.getValue().get("recdQtyKgs")?if_exists?string("##0.0")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${productList.getValue().get("recdKgFat")?if_exists?string("##0.00")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${productList.getValue().get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${productList.getValue().get("totSolids")?if_exists?string("##0.000")}</fo:block>
								           								</fo:table-cell>
								           								<#assign recdFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(productList.getValue().get("recdKgFat"),productList.getValue().get("recdQtyKgs")))>
						           										<#assign recdSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(productList.getValue().get("recdKgSnf"),productList.getValue().get("recdQtyKgs")))>
						           								
								           								<fo:table-cell>
								           									<fo:block text-align="right">${recdFat?if_exists?string("##0.0")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${recdSnf?if_exists?string("##0.00")}</fo:block>
								           								</fo:table-cell>
								           								<#--<#assign slabRate= Static["in.vasista.vbiz.procurement.PriceServices"].getProcurementProductPrice(dctx,Static["org.ofbiz.base.util.UtilMisc"].toMap("userLogin",userLogin,"facilityId",facility.facilityId,"priceDate",productList.getValue().get("receiveDate"),"productId",product.productId,"fatPercent",productList.getValue().get("recdFat"),"snfPercent",productList.getValue().get("recdSnf")))>                								                     								
																		<#assign productRate = (slabRate.get("defaultRate"))>
																		<#assign billQty= (slabRate.get("billQuantity"))?if_exists>-->
																		<#assign milkAmount=0>		
																			
																		<#assign milkAmount = (productList.getValue().get("milkValue"))>	
																		<#assign mlkTotAmt=mlkTotAmt+milkAmount>
																		<#assign opCost=productList.getValue().get("opCost")>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${productList.getValue().get("milkValue")?if_exists?string("##0.00")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right"><#if opCost?has_content>${opCost?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
								           								</fo:table-cell>
								           								<#assign grossAmt=milkAmount+opCost>
								           								<#assign grossTotAmt=grossTotAmt+grossAmt>
								           								<fo:table-cell>
								           									<fo:block text-align="right"><#if grossAmt?has_content>${grossAmt?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
								           								</fo:table-cell>
								           								<#assign avgRate= grossAmt/productList.getValue().get("recdQtyLtrs")>
								           								<fo:table-cell>
								           									<fo:block text-align="right"><#if avgRate?has_content>${avgRate?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
								           								</fo:table-cell>
								           							</fo:table-row>
								           						 </#if>	
							           						</#list>	
						           						</fo:table-body>
						           					</fo:table>
						           				</fo:block>
						           			</fo:table-cell>						           			
						           		</fo:table-row> 
						           	</#list>	
							</fo:table-body>
			 			</fo:table>
					</fo:block>
					<fo:block font-size="7pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block font-family="Courier,monospace" font-size="7pt">
						 	<fo:table >
						 		<fo:table-column column-width="20pt"/>
           						<fo:table-column column-width="100pt"/>
           						<fo:table-column column-width="100pt"/>
           						<fo:table-column column-width="100pt"/>   
           						<fo:table-column column-width="100pt"/>   
           						<fo:table-column column-width="50pt"/>   
           						<fo:table-column column-width="50pt"/>   
           						<fo:table-column column-width="50pt"/>       
           						<fo:table-column column-width="50pt"/> 
           						<fo:table-column column-width="50pt"/> 
           						<fo:table-column column-width="50pt"/> 
           						<fo:table-column column-width="50pt"/> 
           						<fo:table-column column-width="50pt"/> 
           						<fo:table-column column-width="50pt"/> 
           						<fo:table-column column-width="50pt"/>           							  						
           						<fo:table-body> 
           						<#assign productTotalsList = productWiseTotalsMap.entrySet()>
						           		<fo:table-row>
						           			<fo:table-cell>
						           				<fo:block></fo:block>
						           			</fo:table-cell>
						           			<fo:table-cell>
						           				<fo:block text-align="left" white-space-collapse="false" keep-together="always">TOTAL (RATES RECEIVED):</fo:block>
						           			</fo:table-cell>
						           			<fo:table-cell>
						           				<fo:block >
						           					<fo:table >
						           						<fo:table-column column-width="25pt"/>
						           						<fo:table-column column-width="53pt"/>
						           						<fo:table-column column-width="53pt"/>
						           						<fo:table-column column-width="45pt"/>
						           						<fo:table-column column-width="50pt"/>
						           						<fo:table-column column-width="55pt"/>
						           						<fo:table-column column-width="40pt"/>
						           						<fo:table-column column-width="30pt"/>
						           						<fo:table-column column-width="70pt"/>	
						           						<fo:table-column column-width="50pt"/>	
						           						<fo:table-column column-width="60pt"/>	
						           						<fo:table-column column-width="40pt"/>		
						           						<fo:table-column column-width="50pt"/>	
						           						<fo:table-column column-width="50pt"/>	
						           						<fo:table-column column-width="50pt"/>								           						
						           						<fo:table-body>
						           							<fo:table-row>
						           								<fo:table-cell>
						           									<fo:block keep-together="always"></fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right">${recdTotQtyLtrs?if_exists?string("##0.0")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right">${recdTotQtyKgs?if_exists?string("##0.0")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right">${recdTotKgFat?if_exists?string("##0.00")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right">${recdTotKgSnf?if_exists?string("##0.00")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right">${(recdTotKgFat+recdTotKgSnf)?if_exists?string("##0.000")}</fo:block>
						           								</fo:table-cell>
						           								<#assign grandFat=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(recdTotKgFat,recdTotQtyKgs))>
						           								<#assign grandSnf=(Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].calculateFatOrSnf(recdTotKgSnf,recdTotQtyKgs))>
						           								<fo:table-cell>
						           									<fo:block text-align="right">${grandFat?if_exists?string("##0.0")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right">${grandSnf?if_exists?string("##0.00")}</fo:block>
						           								</fo:table-cell>
						           								
						           								<fo:table-cell>
						           									<fo:block text-align="right">${mlkTotAmt?if_exists?string("##0.00")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right"></fo:block>
						           								</fo:table-cell>								           								
						           								<fo:table-cell>
						           									<fo:block text-align="right"><#if grossTotAmt?has_content>${grossTotAmt?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right"><#if recdTotQtyLtrs !=0>${(grossTotAmt/recdTotQtyLtrs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
						           								</fo:table-cell>
						           							</fo:table-row>
						           							<#--<#list productTotalsList as prodTotals>
						           								<#if prodTotals.getValue().get("recdQtyKgs") !=0>
								           							<fo:table-row>
								           								<#assign product = delegator.findOne("Product", {"productId" : prodTotals.getKey()}, true)>
								           								<fo:table-cell>
								           									<fo:block keep-together="always">${product.brandName?if_exists}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${prodTotals.getValue().get("recdQtyLtrs")?if_exists?string("##0.0")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${prodTotals.getValue().get("recdQtyKgs")?if_exists?string("##0.0")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${prodTotals.getValue().get("recdKgFat")?if_exists?string("##0.0")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${prodTotals.getValue().get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${prodTotals.getValue().get("totSolids")?if_exists?string("##0.00")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${prodTotals.getValue().get("recdFat")?if_exists?string("##0.00")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${prodTotals.getValue().get("recdSnf")?if_exists?string("##0.00")}</fo:block>
								           								</fo:table-cell>
								           								<#assign slabRate= Static["in.vasista.vbiz.procurement.PriceServices"].getProcurementProductPrice(dctx,Static["org.ofbiz.base.util.UtilMisc"].toMap("userLogin",userLogin,"facilityId",facility.facilityId,"priceDate",prodTotals.getValue().get("receiveDate"),"productId",product.productId,"fatPercent",prodTotals.getValue().get("recdFat"),"snfPercent",prodTotals.getValue().get("recdSnf")))>                								                     								
																		<#assign productRate = (slabRate.get("defaultRate"))>
																		<#assign milkAmount = (prodTotals.getValue().get("recdKgFat")*productRate)>
																		<#assign opCost=productList.getValue().get("opCost")>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${prodTotals.getValue().get("price")?if_exists?string("##0.00")}==<#if milkAmount?has_content>${milkAmount?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right"><#if opCost?has_content>${opCost?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
								           								</fo:table-cell>
								           								<#assign grossAmt=milkAmount+opCost>
								           								<fo:table-cell>
								           									<fo:block text-align="right"><#if grossAmt?has_content>${grossAmt?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
								           								</fo:table-cell>
								           								<#assign avgRate= grossAmt/prodTotals.getValue().get("recdQtyLtrs")>
								           								<fo:table-cell>
								           									<fo:block text-align="right"><#if avgRate?has_content>${avgRate?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
								           								</fo:table-cell>
								           							</fo:table-row>
								           						 </#if>		
								           					</#list>-->					           						
						           						</fo:table-body>
						           					</fo:table>
						           				</fo:block>
						           			</fo:table-cell>						           			
						           		</fo:table-row> 
						           		<fo:table-row>
						           			<fo:table-cell>
						           				<fo:block font-size="7pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
						           			</fo:table-cell>
						           		</fo:table-row>
						           		<fo:table-row>
						           			<fo:table-cell>
						           				<fo:block></fo:block>
						           			</fo:table-cell>
						           			<fo:table-cell>
						           				<fo:block text-align="left" white-space-collapse="false" keep-together="always">ABSTRACT</fo:block>
						           			</fo:table-cell>
						           			<fo:table-cell>
						           				<fo:block >
						           					<fo:table >
						           						<fo:table-column column-width="25pt"/>
						           						<fo:table-column column-width="53pt"/>
						           						<fo:table-column column-width="53pt"/>
						           						<fo:table-column column-width="45pt"/>
						           						<fo:table-column column-width="50pt"/>
						           						<fo:table-column column-width="55pt"/>
						           						<fo:table-column column-width="40pt"/>
						           						<fo:table-column column-width="30pt"/>
						           						<fo:table-column column-width="70pt"/>	
						           						<fo:table-column column-width="50pt"/>	
						           						<fo:table-column column-width="60pt"/>	
						           						<fo:table-column column-width="40pt"/>		
						           						<fo:table-column column-width="50pt"/>	
						           						<fo:table-column column-width="50pt"/>	
						           						<fo:table-column column-width="50pt"/>								           						
						           						<fo:table-body>
						           							<fo:table-row>
						           								<fo:table-cell>
						           									<fo:block keep-together="always"></fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right">${recdTotQtyLtrs?if_exists?string("##0.0")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right">${recdTotQtyKgs?if_exists?string("##0.0")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right">${recdTotKgFat?if_exists?string("##0.00")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right">${recdTotKgSnf?if_exists?string("##0.00")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right">${(recdTotKgFat+recdTotKgSnf)?if_exists?string("##0.000")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right">0.0</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right">0.00</fo:block>
						           								</fo:table-cell>
						           								
						           								<fo:table-cell>
						           									<fo:block text-align="right">${mlkTotAmt?if_exists?string("##0.00")}</fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right"></fo:block>
						           								</fo:table-cell>								           								
						           								<fo:table-cell>
						           									<fo:block text-align="right"><#if grossTotAmt?has_content>${grossTotAmt?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
						           								</fo:table-cell>
						           								<fo:table-cell>
						           									<fo:block text-align="right">0.00</fo:block>
						           								</fo:table-cell>
						           							</fo:table-row>
						           							<#--<#list productTotalsList as prodTotals>
						           								<#if prodTotals.getValue().get("recdQtyKgs") !=0>
								           							<fo:table-row>
								           								<#assign product = delegator.findOne("Product", {"productId" : prodTotals.getKey()}, true)>
								           								<fo:table-cell>
								           									<fo:block keep-together="always">${product.brandName?if_exists}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${prodTotals.getValue().get("recdQtyLtrs")?if_exists?string("##0.0")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${prodTotals.getValue().get("recdQtyKgs")?if_exists?string("##0.0")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${prodTotals.getValue().get("recdKgFat")?if_exists?string("##0.0")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${prodTotals.getValue().get("recdKgSnf")?if_exists?string("##0.00")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">${prodTotals.getValue().get("totSolids")?if_exists?string("##0.00")}</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">0.00</fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right">0.00</fo:block>
								           								</fo:table-cell>
								           								<#assign slabRate= Static["in.vasista.vbiz.procurement.PriceServices"].getProcurementProductPrice(dctx,Static["org.ofbiz.base.util.UtilMisc"].toMap("userLogin",userLogin,"facilityId",facility.facilityId,"priceDate",prodTotals.getValue().get("receiveDate"),"productId",product.productId,"fatPercent",prodTotals.getValue().get("recdFat"),"snfPercent",prodTotals.getValue().get("recdSnf")))>                								                     								
																		<#assign productRate = (slabRate.get("defaultRate"))>
																		<#assign milkAmount = (prodTotals.getValue().get("recdKgFat")*productRate)>
																		<#assign opCost=productList.getValue().get("opCost")>
								           								<fo:table-cell>
								           									<fo:block text-align="right"><#if milkAmount?has_content>${milkAmount?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right"><#if opCost?has_content>${opCost?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
								           								</fo:table-cell>
								           								<#assign grossAmt=milkAmount+opCost>
								           								<fo:table-cell>
								           									<fo:block text-align="right"><#if grossAmt?has_content>${grossAmt?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
								           								</fo:table-cell>
								           								<fo:table-cell>
								           									<fo:block text-align="right"></fo:block>
								           								</fo:table-cell>
								           							</fo:table-row>
								           						 </#if>		
								           					</#list>-->					           						
						           						</fo:table-body>
						           					</fo:table>
						           				</fo:block>
						           			</fo:table-cell>						           			
						           		</fo:table-row> 
						           		<fo:table-row>
						           			<fo:table-cell>
						           				<fo:block font-size="7pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
						           			</fo:table-cell>
						           		</fo:table-row>
						           		<fo:table-row>
						           			<fo:table-cell></fo:table-cell>
						           			<#assign totalQty=0>
						           			<#list productTotalsList as prodTotals>
						           				<#assign product = delegator.findOne("Product", {"productId" : prodTotals.getKey()}, true)>
						           				<#assign totalQty=totalQty+prodTotals.getValue()>
						           				<fo:table-cell>
						           				    <fo:block>
						           				    	<fo:table>
						           				    		<fo:table-column column-width="80pt"/>
						           				    		<fo:table-column column-width="60pt"/>
						           				    		<fo:table-column column-width="50pt"/>
						           				    		<fo:table-column column-width="50pt"/>
						           				    		<fo:table-column column-width="50pt"/>
						           				    		<fo:table-column column-width="50pt"/>
						           				    		<fo:table-column column-width="50pt"/>
						           				    		<fo:table-body>
						           				    			<fo:table-row>
						           				    				<fo:table-cell><fo:block text-align="left" keep-together="always">${product.brandName?if_exists}: ${prodTotals.getValue()?if_exists}</fo:block></fo:table-cell>
						           				    			</fo:table-row>
						           				    		</fo:table-body>
						           				    	</fo:table>
						           				    </fo:block>
						           				</fo:table-cell> 
						           			</#list>		           			
						           			<fo:table-cell><fo:block text-indent="40pt" keep-together="always">TOTAL :  ${totalQty}</fo:block></fo:table-cell>					           			
						           		</fo:table-row>
									</fo:table-body>
			 					</fo:table>
							</fo:block>
							<fo:block font-size="7pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	    					<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
	    					<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
	    					<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
	    					<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
	    					<fo:block keep-togehter="always" font-size="7pt" white-space-collapse="false">Copy submitted to the Managing Diretor,                                                                                         DEPUTY DIRECTOR  (MIS)</fo:block>
	    					<fo:block keep-togehter="always" font-size="7pt">Copy submitted to the Executive Director,</fo:block>
	    					<fo:block keep-togehter="always" font-size="7pt">Copy submitted to the General Manager (P&amp;I),</fo:block>
	    					<fo:block keep-togehter="always" font-size="7pt">Copy submitted to the General Manager (MPF),</fo:block>
	    					<fo:block keep-togehter="always" font-size="7pt">Copy submitted to the General Manager (F&amp;A),</fo:block>	    					
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