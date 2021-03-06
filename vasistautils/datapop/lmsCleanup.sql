-- This script can be used to reset all accounting-related transaction data (invoices, payments, accounting
-- transactions, etc).  This script will need to be enhanced as and when more modules come online.
-- The end result from running this script should result in a fresh database with no accounting-related
-- transaction data and a zero balance sheet.
--
-- The main use of this script to reset all accounting-related data after testing and prior to going live.
-- Hence, even all visit/login data will be purged to have a clean slate.

-- USE `vbiz_VST`;

-- SET AUTOCOMMIT=0;
-- START TRANSACTION;


DELETE FROM `GL_RECONCILIATION_ENTRY`;


DELETE FROM `GL_ACCOUNT_HISTORY`;
UPDATE `GL_ACCOUNT` SET `POSTED_BALANCE`=0;
UPDATE `GL_ACCOUNT_ORGANIZATION` SET `POSTED_BALANCE`=0;

DELETE FROM `ACCTG_TRANS_ENTRY`;
DELETE FROM `ACCTG_TRANS_ATTRIBUTE`;
DELETE FROM `ACCTG_TRANS`;
-- DELETE FROM `ACCTG_FORMULA`;
-- DELETE FROM `ACCTG_FORMULA_SLABS`;


DELETE FROM `SHIPMENT_PACKAGE_ROUTE_SEG`;
DELETE FROM `SHIPMENT_ROUTE_SEGMENT`;
DELETE FROM `SHIPMENT_PACKAGE_CONTENT`;
DELETE FROM `SHIPMENT_PACKAGE`;
DELETE FROM `SHIPMENT_STATUS`;
DELETE FROM `SHIPMENT_ITEM_BILLING`;
DELETE FROM `INVENTORY_ITEM_DETAIL`;
DELETE FROM `ITEM_ISSUANCE_ROLE`;
DELETE FROM `ORDER_ITEM_BILLING`;
DELETE FROM `ITEM_ISSUANCE`;
DELETE FROM `ORDER_CONTACT_MECH`;
DELETE FROM `ORDER_ITEM_SHIP_GROUP_ASSOC`;
DELETE FROM `ORDER_ITEM_SHIP_GROUP`;
DELETE FROM `ORDER_ITEM_PRICE_INFO`;
DELETE FROM `ORDER_ITEM_CHANGE`;
DELETE FROM `ORDER_ITEM_PRICE_INFO`;
DELETE FROM `ORDER_PAYMENT_PREFERENCE`;
DELETE FROM `ORDER_SHIPMENT`;
DELETE FROM `SHIPMENT_ITEM`;
DELETE FROM `SHIPMENT_RECEIPT`;

DELETE FROM ORDER_ITEM_BILLING WHERE ORDER_ID NOT IN (SELECT ORDER_ID FROM  ORDER_HEADER WHERE PURPOSE_TYPE_ID ="MILK_PROCUREMENT" AND ORDER_TYPE_ID = "PURCHASE_ORDER"); 		
DELETE  FROM INVOICE_STATUS WHERE INVOICE_ID NOT IN (SELECT INVOICE_ID FROM INVOICE WHERE  REFERENCE_NUMBER LIKE "PROC_BILLING_%");
SET foreign_key_checks = 0;
DELETE  FROM INVOICE_ITEM   WHERE INVOICE_ID NOT IN (SELECT INVOICE_ID FROM INVOICE WHERE  REFERENCE_NUMBER LIKE "PROC_BILLING_%");
SET foreign_key_checks = 1;
DELETE FROM `INVOICE_CONTACT_MECH` WHERE INVOICE_ID NOT IN (SELECT INVOICE_ID FROM INVOICE WHERE  REFERENCE_NUMBER LIKE "PROC_BILLING_%");
DELETE FROM `INVOICE_ROLE` WHERE INVOICE_ID NOT IN (SELECT INVOICE_ID FROM INVOICE WHERE  REFERENCE_NUMBER LIKE "PROC_BILLING_%");
DELETE FROM `INVOICE_ATTRIBUTE` WHERE INVOICE_ID NOT IN (SELECT INVOICE_ID FROM INVOICE WHERE  REFERENCE_NUMBER LIKE "PROC_BILLING_%");
DELETE FROM `INVOICE_STATUS` WHERE INVOICE_ID NOT IN (SELECT INVOICE_ID FROM INVOICE WHERE  REFERENCE_NUMBER LIKE "PROC_BILLING_%");
DELETE FROM `INVOICE_ITEM_ASSOC`;
DELETE FROM INVOICE WHERE REFERENCE_NUMBER NOT LIKE "PROC_BILLING_%";
DELETE FROM ORDER_ITEM WHERE ORDER_ID NOT IN (SELECT ORDER_ID FROM  ORDER_HEADER WHERE PURPOSE_TYPE_ID ="MILK_PROCUREMENT" AND ORDER_TYPE_ID = "PURCHASE_ORDER");
DELETE FROM ORDER_STATUS WHERE ORDER_ID NOT IN (SELECT ORDER_ID FROM  ORDER_HEADER WHERE PURPOSE_TYPE_ID ="MILK_PROCUREMENT" AND ORDER_TYPE_ID = "PURCHASE_ORDER");
DELETE FROM ORDER_ROLE WHERE ORDER_ID NOT IN (SELECT ORDER_ID FROM  ORDER_HEADER WHERE PURPOSE_TYPE_ID ="MILK_PROCUREMENT" AND ORDER_TYPE_ID = "PURCHASE_ORDER");    
DELETE FROM `ORDER_HEADER_NOTE` WHERE ORDER_ID NOT IN (SELECT ORDER_ID FROM  ORDER_HEADER WHERE PURPOSE_TYPE_ID ="MILK_PROCUREMENT" AND ORDER_TYPE_ID = "PURCHASE_ORDER");
SET foreign_key_checks = 0;

-- HERE WE ARE USING `TEMP_ORDER_HEADER` TABLE FOR CLEANUP THE DATA FROM ORDER_HEADER
CREATE TABLE `TEMP_ORDER_HEADER` LIKE `ORDER_HEADER`;
INSERT `TEMP_ORDER_HEADER` (SELECT * FROM ORDER_HEADER);	

DELETE FROM  ORDER_HEADER WHERE ORDER_ID NOT IN (SELECT ORDER_ID FROM  TEMP_ORDER_HEADER WHERE PURPOSE_TYPE_ID ="MILK_PROCUREMENT" AND ORDER_TYPE_ID = "PURCHASE_ORDER");   

DROP TABLE TEMP_ORDER_HEADER;

DELETE FROM `SHIPMENT`;
SET foreign_key_checks = 1;

DELETE FROM `PAYMENT_APPLICATION`;
DELETE FROM `PAYMENT_GROUP_MEMBER`;
DELETE FROM `PAYMENT_GROUP`;

SET foreign_key_checks = 0;
DELETE FROM `PAYMENT`;
DELETE FROM `FIN_ACCOUNT_TRANS`;
SET foreign_key_checks = 1;
-- UPDATE `FIN_ACCOUNT` SET `ACTUAL_BALANCE`=0 ,`AVAILABLE_BALANCE`=0;
-- DELETE FROM `FIN_ACCOUNT` WHERE `FIN_ACCOUNT_TYPE_ID` == 'BANK_ACCOUNT';
DELETE FROM `GL_RECONCILIATION`;

UPDATE `TIME_ENTRY` SET `INVOICE_ID`=NULL ,`INVOICE_ITEM_SEQ_ID`=NULL;





DELETE FROM `MILK_CARD_ORDER_ITEM`;
DELETE FROM `MILK_CARD_ORDER`;
DELETE FROM `MILK_CARD_TOTAL`;
DELETE FROM `MILK_CARD_PRODUCT_MAP`;
DELETE FROM `MILK_CARD_TYPE`;
DELETE FROM `CRATES_CANS_ACCNT`;
DELETE FROM `L_M_S_SALES_HISTORY_SUMMARY`;
DELETE FROM `L_M_S_SALES_HISTORY_SUMMARY_DETAIL`;
DELETE FROM `BANK_REMITTANCE`;




-- COMMIT;
