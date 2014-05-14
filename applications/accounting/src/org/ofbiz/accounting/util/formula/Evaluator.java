package org.ofbiz.accounting.util.formula;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.accounting.invoice.InvoiceServices;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;


public class Evaluator {
	
    public static String module = Evaluator.class.getName();	
    private HashMap<String, Double>	variableValues = new HashMap<String, Double>();	
    private HashMap<String, Double> dependentFormulaValues = new HashMap<String, Double>();
    private String formulaId;
    private boolean isSlabBased;
    private Double slabAmount;
	private String origExpr;
	private String processedExpr;
	private MathEvaluator mathEvaluator;
	DispatchContext dctx;
	/**
	 * Default constructor, used in conjunction with setFormulaId to evaluate the formula
	 * associated with a given formula id.
	 */
	public Evaluator(DispatchContext dctx) {
		this.dctx = dctx;
	}
	
	/**
	 * Constructor 
	 * @param expr formula expression
	 */
	public Evaluator (String expr, DispatchContext dctx) {
		this.dctx = dctx;
		origExpr = expr;
		init();
	}
	
	private void init() {

		processedExpr = preprocessExpr();
		mathEvaluator = new MathEvaluator(processedExpr);		
	}

	/*
	 * Example formula:
	 * .15*${Invoice.Amount} + %[SALES_TAX]
	 * Here ${Invoice.Amount} refers to a variable and %[SALES_TAX] refers to a
	 * dependent formula (which will also need to be evaluated).
	 */
	private String preprocessExpr() {
		boolean done = false;
		StringBuffer retVal = new StringBuffer();
		String temp = origExpr;
		// process any variable fields
		while (!done) {
			int startIdx = temp.indexOf("${");
			if (startIdx >= 0) {
				retVal.append(temp.substring(0, startIdx));
				int lastIdx = temp.indexOf("}", startIdx);
				if (lastIdx == -1) {
					throw new IllegalArgumentException("Invalid variable in formula: " + origExpr);
				}
				String variableName = temp.substring(startIdx+2, lastIdx);
				variableValues.put(variableName, null);
				retVal.append(variableName);
				temp = temp.substring(++lastIdx);
			}
			else {
				retVal.append(temp);
				done = true;
			}
		}
		
		// process any derived formulas
		done = false;
		temp = retVal.toString();
		retVal.setLength(0);
		while (!done) {
			int startIdx = temp.indexOf("%[");
			if (startIdx >= 0) {
				retVal.append(temp.substring(0, startIdx));
				int lastIdx = temp.indexOf("]", startIdx);
				if (lastIdx == -1) {
					throw new IllegalArgumentException("Invalid dependent formula in formula: " + origExpr);
				}
				String formulaName = temp.substring(startIdx+2, lastIdx);
				dependentFormulaValues.put(formulaName, null);
				retVal.append(formulaName);
				temp = temp.substring(++lastIdx);
			}
			else {
				retVal.append(temp);
				done = true;
			}
		}		
		
		return retVal.toString();
	}

	private void fetchSlabBasedFormula() {
        Delegator delegator = dctx.getDelegator();	
    	if (slabAmount == null) {
            Debug.logError("Mising slabAmount, cannot determine formula '" + 
            		formulaId + "'", module);
            throw new IllegalArgumentException("Mising slabAmount, cannot determine formula '" + 
            		formulaId + "'");            		
    	}     
        try {
            List mainExprs = FastList.newInstance();
            mainExprs.add(EntityCondition.makeCondition("acctgFormulaId", EntityOperator.EQUALS, formulaId));            
            mainExprs.add(EntityCondition.makeCondition("slabStart", EntityOperator.LESS_THAN_EQUAL_TO, slabAmount));
            mainExprs.add(EntityCondition.makeCondition("slabEnd", EntityOperator.GREATER_THAN_EQUAL_TO, slabAmount));            
            EntityCondition condition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);        	
            List slabList = delegator.findList("AcctgFormulaSlabs", condition, null, null, null, false);
            if (slabList.size() != 1) {
        		Debug.logError("Expected 1 row from AcctgFormulaSlabs for '" + 
        				formulaId + "'; slabAmount=" + slabAmount + ", but received " + slabList.size() + " rows", module);
        		throw new IllegalArgumentException("Expected 1 row from AcctgFormulaSlabs for '" + 
        				formulaId + "'; slabAmount=" + slabAmount + ", but received " + slabList.size() + " rows");            	
            }
        	GenericValue formulaRow = (GenericValue)slabList.get(0);
        	if (formulaRow == null) {
        		Debug.logError("No matching record for formula '" + 
        				formulaId + "'; slabAmount=" + slabAmount, module);
        		throw new IllegalArgumentException("No matching record for formula '" + 
        				formulaId + "'; slabAmount=" + slabAmount);
        	}
        	origExpr = formulaRow.getString("formula");
            init();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error retrieving slab record for formula '" + 
            		formulaId + "'; slabAmount=" + slabAmount, module);
            throw new RuntimeException("Error retrieving slab record for formula '" + 
	            		formulaId + "'; slabAmount=" + slabAmount);
        }	
	}
	
	private void fetchFormula() {
        Delegator delegator = dctx.getDelegator();
	
        try {
            GenericValue formulaRow = delegator.findByPrimaryKey("AcctgFormula", UtilMisc.toMap("acctgFormulaId", formulaId));
            if (formulaRow == null) {
	            Debug.logError("No matching record for formula '" + 
	            		formulaId + "'", module);
	            throw new IllegalArgumentException("No matching record for formula '" + 
	            		formulaId + "'");
            }	
            if (formulaRow.getBoolean("isSlabBased") != null) {
            	isSlabBased = formulaRow.getBoolean("isSlabBased");
            }
            if (isSlabBased) {
            	// go fetch the correct formula
            	fetchSlabBasedFormula();
            }
            else {
            	origExpr = formulaRow.getString("formula");
                init();
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error retrieving record for formula '" + 
            		formulaId + "'", module);
            throw new RuntimeException("Error retrieving record for formula '" + 
	            		formulaId + "'");
        }
	}
	
	/**
	 * return variableValues
	 * @return result value
	 */
	public  Map getVariableValues() {
		return variableValues;
	}
	
	/**
	 * Set the formula id (it must exist in the entity)
	 * @param id
	 * @param amt (optional) slab amount for slab-based formulas
	 * @param dctx
	 */
	public void setFormulaIdAndSlabAmount(String id, Double amt) {
		formulaId = id;	
		slabAmount = amt;
		fetchFormula();	
	}	
	
	/**
	 * Add any variable values needed within this formula
	 * @param inValues any variable values needed within the formula
	 */
	public void addVariableValues(HashMap<String, Double> inValues) {
		Iterator<Map.Entry<String, Double>> iter = inValues.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Double> pairs = iter.next();
			variableValues.put(pairs.getKey(), pairs.getValue());
		}		
	}

	private void processDependentFormulas() {
		Iterator<Map.Entry<String, Double>> iter = dependentFormulaValues.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Double> pairs = iter.next();
			Evaluator depEvltr = new Evaluator(dctx);
			depEvltr.setFormulaIdAndSlabAmount(pairs.getKey(), slabAmount);
			depEvltr.addVariableValues(variableValues);
			double value = depEvltr.evaluate();
			dependentFormulaValues.put(pairs.getKey(), value);
			mathEvaluator.addVariable(pairs.getKey(), value);
		}
	}
	
	/**
	 * Evaluate the given expression
	 * @return result value
	 */
	public double evaluate() {
		Iterator<Map.Entry<String, Double>> iter = variableValues.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Double> pairs = iter.next();
			if (pairs.getValue() == null) {
				//log error and bailout
	            Debug.logError("missing value for variable '" + 
	            		pairs.getKey() + "', formula {" + 
						origExpr + "} cannot be computed", module);
	            throw new IllegalArgumentException("missing value for variable '" + 
	            		pairs.getKey() + "', formula {" + 
						origExpr + "} cannot be computed");
			}
			mathEvaluator.addVariable(pairs.getKey(), pairs.getValue());
		}	
		// next process any dependent formulas
		processDependentFormulas();
		return mathEvaluator.getValue();
	}
	
	/**
	 * String representation mainly for testing purposes
	 */
	public String toString()
	{
		StringBuffer retVal = new StringBuffer("");
		retVal.append("==========Evaluator.toString=========\n");
		retVal.append("origExpr ==> " + origExpr + "\n");
		retVal.append("processedExpr ==> " + processedExpr + "\n");
		retVal.append("isSlabBased ==> " + isSlabBased + "\n");		
		retVal.append("variables==>\n");
		Iterator<Map.Entry<String, Double>> iter = variableValues.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Double> pairs = iter.next();
			retVal.append(pairs.getKey() + " = " + pairs.getValue());
			retVal.append("\n");
		}
		retVal.append("dependentFormulas==>\n");
		iter = dependentFormulaValues.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Double> pairs = iter.next();
			retVal.append(pairs.getKey());
			retVal.append("\n");
		}		
		return retVal.toString();
	}

	/**
	 * Service function used by the formula testing screen
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> evaluate(DispatchContext dctx,
			Map<String, Object> context) {
		String formulaId = (String) context.get("acctgFormulaId");
		//String formula = (String) context.get("formula");
		String variableValuesStr = (String) context.get("variableValues");
		Double slabAmt = (Double) context.get("slabAmount");		

		//Evaluator evltr = new Evaluator(formula);
		Evaluator evltr = new Evaluator(dctx);
		evltr.setFormulaIdAndSlabAmount(formulaId, slabAmt);		
		if (variableValuesStr != null && !variableValuesStr.isEmpty()) {
			String[] variableTokens = variableValuesStr.split(",");
			for (int i = 0; i < variableTokens.length; i++) {
				String[] keyValue = variableTokens[i].split("=");
				if (keyValue.length != 2) {
					Debug.logError("incorrect variable value '"
							+ variableTokens[i]
							+ "', variable values string =  {"
							+ variableValuesStr + "} cannot be computed",
							module);
					throw new IllegalArgumentException(
							"incorrect variable value '" + variableTokens[i]
									+ "', variable values string =  {"
									+ variableValuesStr
									+ "} cannot be computed");
				}
				HashMap<String, Double> variables = new HashMap<String, Double>();
				variables.put(keyValue[0], Double.valueOf(keyValue[1]));
				evltr.addVariableValues(variables);
			}
		}
		//Debug.logInfo(evltr.toString(), module);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("formulaResult", evltr.evaluate());

		return result;

	}
	 
    public static void main(String[] args)
    {
    	/*
    	if ( args == null || args.length != 1)
        {
        	System.err.println("Usage: java Evaluator.main [your math expression]");
            System.exit(0);
        }
*/
    	try
        {
		   	//Evaluator m = new Evaluator(args[0]);
    		Evaluator m = new Evaluator("25*2-${Invoice.field2} + 7 - ${Invoice.amount}", null);
    		HashMap<String, Double> variables = new HashMap<String, Double>();
    		variables.put("Invoice.field2", new Double(10.0));
    		variables.put("Invoice.amount", new Double(7.0));    		
    		System.out.println(m);
            System.out.println( m.evaluate() );
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
    }
}
