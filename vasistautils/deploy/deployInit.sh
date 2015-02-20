#!/bin/bash

#
#  This script can be used to setup a staging area for deploying a new version
#  of vbiz/milkosoft.  It expands the named tarball containing the latest version 
#  and creates the necessary dir structure while overriding any production-specific
#  files.  Doing this in a separate staging area ensures that running this script will 
#  not impact the existing live instance.
#
#  This resulting dir (vbiz-new) in the staging dir can then replace the current version.  
#  A separate script (deploy.sh) does the actual deployment.
#

DEPLOY_DIR=/root/deploy
DEPLOY_STAGING_DIR=/root/deploy/staging
DEPLOY_CUSTOMFILES_DIR=/root/deploy/customfiles

TARBALL_NAME=kmf
TODAY=$(date +%Y%m%d)

# This script assumes that the tarball exists in the DEPLOY_STAGING_DIR

function error_exit
{
	echo "$1" 1>&2
	exit 1
}

clear

echo "Begin deployment initialization"

cd ${DEPLOY_DIR}

if mv ${TARBALL_NAME}.tar.gz ${TARBALL_NAME}_$TODAY.tar.gz; 
then
	if mkdir ${DEPLOY_STAGING_DIR}/vbiz-new
	then 
		mv ${TARBALL_NAME}_$TODAY.tar.gz ${DEPLOY_STAGING_DIR}/vbiz-new;
	else
		echo "mkdir ${DEPLOY_STAGING_DIR}/vbiz-new failed. Aborting.."
	fi
else
	echo "${TARBALL_NAME}.tar.gz does not exist. Aborting.."
fi	

cd ${DEPLOY_STAGING_DIR}/vbiz-new/

tar -zxvf ${TARBALL_NAME}_$TODAY.tar.gz

mv ${TARBALL_NAME}_$TODAY.tar.gz ../..

cd ../..

cp customfiles/framework/base/config/cache.properties ${DEPLOY_STAGING_DIR}/vbiz-new/framework/base/config/cache.properties


cp customfiles/framework/common/config/general.properties ${DEPLOY_STAGING_DIR}/vbiz-new/framework/common/config/general.properties

cp customfiles/framework/common/webcommon/forgotPassword.ftl ${DEPLOY_STAGING_DIR}/vbiz-new/framework/common/webcommon/forgotPassword.ftl

cp customfiles/framework/common/webcommon/login.ftl ${DEPLOY_STAGING_DIR}/vbiz-new/framework/common/webcommon/login.ftl

cp customfiles/framework/entity/config/entityengine.xml ${DEPLOY_STAGING_DIR}/vbiz-new/framework/entity/config/entityengine.xml


cp customfiles/tools/bin/pdftotext ${DEPLOY_STAGING_DIR}/vbiz-new/tools/bin/pdftotext

chown -R vbiz ${DEPLOY_STAGING_DIR}/vbiz-new

echo "Completed deployment initialization!"
