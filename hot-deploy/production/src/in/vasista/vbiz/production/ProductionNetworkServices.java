/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package in.vasista.vbiz.production;

/**
 * @author vadmin
 *
 */
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.model.ModelReader;
import org.ofbiz.entity.util.EntityUtil;

import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.lang.NullPointerException;


import java.math.BigDecimal;


public class ProductionNetworkServices {

	 public static final String module = ProductionNetworkServices.class.getName();
	 private static BigDecimal ZERO = BigDecimal.ZERO;
	    private static int decimals;
	    private static int rounding;
	    public static final String resource_error = "OrderErrorUiLabels";
	    static {
	        decimals = 3;//UtilNumber.getBigDecimalScale("order.decimals");
	        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

	        // set zero to the proper scale
	        if (decimals != -1) ZERO = ZERO.setScale(decimals); 
	    }	
	    
	   /**
	    * It will returns All types of silos
	    * @param delegator
	    * @return
	    */
	    public static List getSilos(Delegator delegator) {
	    	
	    	List silosList = FastList.newInstance();
	    	try {
	    		silosList = delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS,"SILO"), null, null, null, true);
	    		     
	    	}catch (GenericEntityException e) {
	            Debug.logError(e, module);	           
	        }
	        return silosList;
	    } // End of the service
	   
	    public static String getRootProductionRun(Delegator delegator, String productionRunId)  throws GenericEntityException {
	        List<GenericValue> linkedWorkEfforts = delegator.findByAnd("WorkEffortAssoc", UtilMisc.toMap("workEffortIdFrom", productionRunId, "workEffortAssocTypeId", "WORK_EFF_PRECEDENCY"));
	        GenericValue linkedWorkEffort = EntityUtil.getFirst(linkedWorkEfforts);
	        if (linkedWorkEffort != null) {
	            productionRunId = getRootProductionRun(delegator, linkedWorkEffort.getString("workEffortIdTo"));
	        }
	        return productionRunId;
	    }
	   
	   
	   
	   
}
