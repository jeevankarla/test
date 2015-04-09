#!/bin/bash

# This script assumes that deployInit.sh has been run successfully

DEPLOY_DIR=/root/deploy
DEPLOY_STAGING_DIR=/root/deploy/staging
VBIZ_PARENT_DIR=/opt


function error_exit
{
	echo "$1" 1>&2
	exit 1
}

clear

echo "*****Begin deployment*****"

cd ${DEPLOY_DIR}

if [ ! -d staging/vbiz-new ];
then 
	echo "staging/vbiz-new dir does not exist. Aborting.."; exit 1;
fi
if [ ! -d ${VBIZ_PARENT_DIR} ];
then
        echo "${VBIZ_PARENT_DIR} dir does not exist. Aborting.."; exit 1;
fi

if mv staging/vbiz-new ${VBIZ_PARENT_DIR}; 
then
	echo "Moved staging/vbiz-new to ${VBIZ_PARENT_DIR}"
else
	echo "mv staging/vbiz-new to ${VBIZ_PARENT_DIR} failed. Aborting.."; exit 1;
fi	

if [ -d ${VBIZ_PARENT_DIR}/vbiz-prev ];
then
	if rm -rf ${VBIZ_PARENT_DIR}/vbiz-prev
        then
		echo "Removed ${VBIZ_PARENT_DIR}/vbiz-prev";
	else
		echo "rm -rf ${VBIZ_PARENT_DIR}/vbiz-prev failed. Aborting.."; exit 1;
 	fi	
fi

if /etc/init.d/vbiz stop;
then
	if mv ${VBIZ_PARENT_DIR}/vbiz ${VBIZ_PARENT_DIR}/vbiz-prev
	then
		echo "moved ${VBIZ_PARENT_DIR}/vbiz to ${VBIZ_PARENT_DIR}/vbiz-prev";
	else
		echo "mv ${VBIZ_PARENT_DIR}/vbiz ${VBIZ_PARENT_DIR}/vbiz-prev failed. ABorting.."; /etc/init.d/vbiz start; exit 1;
 	fi	
fi

mv ${VBIZ_PARENT_DIR}/vbiz-new ${VBIZ_PARENT_DIR}/vbiz
/etc/init.d/vbiz start;

echo "*****End Deployment!*****"
