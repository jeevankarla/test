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

DELETE FROM `SERVER_HIT`;
DELETE FROM `SERVER_HIT_BIN`;
DELETE FROM `USER_LOGIN_HISTORY`;
DELETE FROM `VISIT`;
DELETE FROM `VISITOR`;
-- DELETE FROM `NOTE_DATA`;

DELETE FROM `GL_RECONCILIATION_ENTRY`;


DELETE FROM `GL_ACCOUNT_HISTORY`;
UPDATE `GL_ACCOUNT` SET `POSTED_BALANCE`=0;
UPDATE `GL_ACCOUNT_ORGANIZATION` SET `POSTED_BALANCE`=0;

DELETE FROM `ACCTG_TRANS_ENTRY`;
DELETE FROM `ACCTG_TRANS_ATTRIBUTE`;
DELETE FROM `ACCTG_TRANS`;
UPDATE `CUSTOM_TIME_PERIOD` SET `IS_CLOSED`='N';

DELETE FROM `SHIPMENT_PACKAGE_ROUTE_SEG`;
DELETE FROM `SHIPMENT_ROUTE_SEGMENT`;
DELETE FROM `SHIPMENT_PACKAGE_CONTENT`;
DELETE FROM `SHIPMENT_PACKAGE`;
DELETE FROM `SHIPMENT_STATUS`;
DELETE FROM `SHIPMENT_ITEM_BILLING`;
DELETE FROM `INVENTORY_ITEM_DETAIL`;
DELETE FROM `INVENTORY_ITEM`;
DELETE FROM `INVENTORY_ITEM_VARIANCE`;
DELETE FROM `PHYSICAL_INVENTORY`;
DELETE FROM `ITEM_ISSUANCE_ROLE`;
DELETE FROM `ORDER_ITEM_BILLING`;
DELETE FROM `ITEM_ISSUANCE`;
DELETE FROM `ORDER_ADJUSTMENT_BILLING`;
DELETE FROM `ORDER_ADJUSTMENT`;
DELETE FROM `ORDER_CONTACT_MECH`;
DELETE FROM `ORDER_ITEM_SHIP_GROUP_ASSOC`;
DELETE FROM `ORDER_ITEM_SHIP_GROUP`;
DELETE FROM `ORDER_ITEM_PRICE_INFO`;
DELETE FROM `ORDER_ITEM_CHANGE`;
DELETE FROM `ORDER_ITEM_PRICE_INFO`;
DELETE FROM `ORDER_PAYMENT_PREFERENCE`;
DELETE FROM `ORDER_SHIPMENT`;
DELETE FROM `SHIPMENT_ITEM`;
DELETE FROM `SHIPMENT_RECEIPT_SEQUENCE`;

DELETE FROM `SHIPMENT_RECEIPT`;
DELETE FROM `ORDER_ITEM_SHIP_GRP_INV_RES`;
DELETE FROM `ORDER_STATUS`;
DELETE FROM `ORDER_ROLE`;
DELETE FROM `ORDER_ITEM_ATTRIBUTE`;
DELETE FROM `ORDER_ITEM`;
DELETE FROM `ORDER_HEADER_NOTE`;
SET foreign_key_checks = 0;
DELETE FROM `ORDER_HEADER`;
DELETE FROM `SHIPMENT`;
SET foreign_key_checks = 1;

DELETE FROM `PAYMENT_APPLICATION`;
DELETE FROM `PAYMENT_GROUP_MEMBER`;
DELETE FROM `PAYMENT_GROUP`;

SET foreign_key_checks = 0;
DELETE FROM `PAYMENT`;
DELETE FROM `FIN_ACCOUNT_TRANS`;
SET foreign_key_checks = 1;
UPDATE `FIN_ACCOUNT` SET `ACTUAL_BALANCE`=0 ,`AVAILABLE_BALANCE`=0;

DELETE FROM `GL_RECONCILIATION`;

UPDATE `TIME_ENTRY` SET `INVOICE_ID`=NULL ,`INVOICE_ITEM_SEQ_ID`=NULL;

DELETE FROM `BILL_OF_SALE_INVOICE_SEQUENCE`;
DELETE FROM `INVOICE_STATUS`;
DELETE FROM `INVOICE_ITEM_ASSOC`;

SET foreign_key_checks = 0;
DELETE FROM `INVOICE_ITEM`;
SET foreign_key_checks = 1;

DELETE FROM `INVOICE_CONTACT_MECH`;
DELETE FROM `INVOICE_ROLE`;
DELETE FROM `INVOICE_ATTRIBUTE`;
DELETE FROM `INVOICE`;

DELETE FROM `MILK_CARD_ORDER_ITEM`;
DELETE FROM `MILK_CARD_ORDER`;
DELETE FROM `L_M_S_SALES_HISTORY_SUMMARY`;
-- COMMIT;
