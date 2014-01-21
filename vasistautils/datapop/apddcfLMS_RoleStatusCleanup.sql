-- This script is meant to cleanup all unnecessary order/invoice roles and status data


DELETE FROM `ORDER_STATUS`;
DELETE FROM `ORDER_ROLE`;
DELETE FROM `INVOICE_STATUS`;
DELETE FROM `INVOICE_ROLE`;







