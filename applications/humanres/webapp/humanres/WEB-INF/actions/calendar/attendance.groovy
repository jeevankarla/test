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

import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import java.util.*;
import java.lang.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;
import javolution.util.FastMap;
import org.ofbiz.entity.util.EntityFindOptions;
//partyId = parameters.get("partyId");
//delegator=request.getAttribute("delegator");
EmployeeId = parameters.get("EmployeeId");
//System.out.println("\n\n\n==============================party id==================================="+partyId);
if(parameters.get("EmployeeId") != null)
{
GenericValue party = delegator.findByPrimaryKey("Employee", UtilMisc.toMap("EmployeeId", EmployeeId));    
partyId = party.get("partyId");


}
if(partyId==null)
partyId = userLogin.getString("partyId");
//else
//partyId=null;
//if (parameters.get("partyId") != null) 

/*if (partyId == null) 
{
userLogin = request.getAttribute("userLogin");
System.out.println("\n\n================================= User Login ===========================");
System.out.println(userLogin);
partyId = userLogin.getString("partyId");
condition = new EntityExpr("partyId", EntityOperator.EQUALS, userLogin.getString("partyId"));
data=delegator.findByCondition("EmplPunch" , condition, null, null);

}
else 

{*/
condition = new EntityExpr("partyId", EntityOperator.EQUALS, partyId);
data=delegator.findByCondition("EmplPunch" , condition, null, null);

//}


//condition = new EntityExpr("InOut",EntityOperator.EQUALS,"Out");

//data = delegator.findByCondition("EmplPunch", condition, null, null);
//data=delegator.findAll("EmplPunch");


context.put("data",data);
context.put("partyId",partyId);

