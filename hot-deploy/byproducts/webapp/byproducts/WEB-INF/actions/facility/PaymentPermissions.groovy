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

// standard partymgr permissions
 import org.ofbiz.base.util.*;

hasChequePermission = security.hasEntityPermission("BYPRODUCTS", "_CHEQUE", session);
if(hasChequePermission){
	context.paymentMethodTypeId = "CHEQUE_PAYIN";
	context.subTabItem = "ChequePayment";
}
hasChallanPermission = security.hasEntityPermission("BYPRODUCTS", "_CHALLAN", session);
if(hasChallanPermission){
	context.paymentMethodTypeId = "CHALLAN_PAYIN";
	context.subTabItem = "ChallanPayment";
}
hasCashPermission = security.hasEntityPermission("BYPRODUCTS", "_CASH", session);
if(hasCashPermission){
	context.paymentMethodTypeId = "CASH_PAYIN";
	context.subTabItem = "CashPayment";
	
}