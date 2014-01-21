import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import javolution.util.FastMap;

tenantId = delegator.getDelegatorTenantId();
context.tenantId =  tenantId.toUpperCase();
