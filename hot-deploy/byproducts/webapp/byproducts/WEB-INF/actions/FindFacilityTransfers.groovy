/*
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
 */

 
 import java.math.BigDecimal;
 import java.math.RoundingMode;
 import java.text.ParseException;
 import java.util.*;
 import java.util.Map.Entry;
 import java.sql.Date;
 import java.sql.Timestamp;
 
 import javolution.util.FastList;
 import javolution.util.FastMap;
 import javolution.util.FastSet;
 
 import org.apache.tools.ant.filters.TokenFilter.ContainsString;
 import org.ofbiz.entity.*;
 import org.ofbiz.entity.condition.*;
 import org.ofbiz.entity.util.*;
 import org.ofbiz.base.util.*;
 import org.ofbiz.service.DispatchContext;
 import org.ofbiz.service.GenericServiceException;
 import org.ofbiz.service.LocalDispatcher;
 import org.ofbiz.service.ModelService;
 import org.ofbiz.service.ServiceUtil;
 
 
import org.ofbiz.entity.condition.*;

import org.ofbiz.entity.*
import org.ofbiz.entity.util.*
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import com.ibm.icu.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

inventoryXferId = parameters.inventoryTransferId;
inventoryTransferIdList = parameters.inventoryTransferIdList;

HashSet xferIdsSet = new HashSet(inventoryTransferIdList);

//default this to true, ie only show active
activeOnly = !"false".equals(request.getParameter("activeOnly"));
context.activeOnly = activeOnly;

// if the completeRequested was set, then we'll lookup only requested status
completeRequested = "true".equals(request.getParameter("completeRequested"));
context.completeRequested = completeRequested;

// get the 'to' this facility transfers

exprsTo = [EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, facilityId),
		   EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN, xferIdsSet)];

/*if (activeOnly) {
    exprsTo = [EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, facilityId),
               EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "IXF_COMPLETE"),
               EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "IXF_CANCELLED")];
} else {
    exprsTo = [EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, facilityId)];
}
if (completeRequested) {
    exprsTo = [EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, facilityId),
               EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "IXF_REQUESTED")];
}*/
ecl = EntityCondition.makeCondition(exprsTo, EntityOperator.AND);
toTransfers = delegator.findList("InventoryTransfer", ecl, null, ['sendDate'], null, false);
if (toTransfers) {
    context.toTransfers = toTransfers;
}

// get the 'from' this facility transfers

exprsFrom = [EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId),
			 EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN, xferIdsSet)];

/*if (activeOnly) {
    exprsFrom = [EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId),
                 EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "IXF_COMPLETE"),
                 EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "IXF_CANCELLED")];
} else {
    exprsFrom = [EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId)];
}
if (completeRequested) {
    exprsFrom = [EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId),
                 EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "IXF_REQUESTED")];
}*/
ecl = EntityCondition.makeCondition(exprsFrom, EntityOperator.AND);
fromTransfers = delegator.findList("InventoryTransfer", ecl, null, ['sendDate'], null, false);
if (fromTransfers) {
    context.fromTransfers = fromTransfers;
}
