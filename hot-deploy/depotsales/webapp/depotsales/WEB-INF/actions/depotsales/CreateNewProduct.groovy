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
	 import java.util.*;
	 import java.sql.Timestamp;
	 import org.ofbiz.entity.*;
	 import org.ofbiz.entity.condition.*;
	 import org.ofbiz.entity.util.*;
	 import org.ofbiz.base.util.*;
	 import applications.product.src.org.ofbiz.product.feature.GenericValue;
	 import applications.product.src.org.ofbiz.product.feature.List;
	 import java.util.*;
	 import java.text.ParseException;
	 import java.text.SimpleDateFormat;
	 import net.sf.json.JSONArray;
	 import java.util.SortedMap;
	 import java.math.RoundingMode;
	 import javolution.util.FastList;
		 
	 productFeatureCategoryAppls = delegator.findByAndCache("ProductFeatureCategoryAppl", UtilMisc.toMap("productCategoryId", productCategoryId));
	 productFeatureCategoryAppls = EntityUtil.filterByDate(productFeatureCategoryAppls, true);
	 
	 
	 
	 
	 
	 //context.totalRevenue = totalRevenue;
	 