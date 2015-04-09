#!/bin/bash
START=`date +%s`

DEPLOY_DIR=/root/deploy
DEPLOY_STAGING_DIR=/root/deploy/staging
DEPLOY_CUSTOMFILES_DIR=/root/deploy/customfiles
GITHUB_DIR=/root/deploy/github/mdkmf

TARBALL_NAME=kmf
TODAY=$(date +%Y%m%d)

# This script assumes that the tarball exists in the DEPLOY_STAGING_DIR

function error_exit
{
	echo "$1" 1>&2
	exit 1
}

clear

echo "*****Begin deployment initialization*****"

# First cleanup old tarball if it exists
cd ${DEPLOY_DIR}
if [ -f ${TARBALL_NAME}.tar.gz ];
then
	rm ${TARBALL_NAME}.tar.gz;
fi
if [ -d staging/vbiz-new ];
then
	rm -rf staging/vbiz-new;
fi 

# Fetch codebase from github
cd ${GITHUB_DIR}
git fetch origin
git reset --hard origin/master
END=`date +%s`
ELAPSED=$(( $END - $START ))
echo "-->Code fetch completed ($ELAPSED seconds)"
./ant refresh  >/dev/null 2>&1
END=`date +%s`
ELAPSED=$(( $END - $START ))
echo "-->Build completed ($ELAPSED seconds)"
./ant kmf-tar >/dev/null 2>&1
END=`date +%s`
ELAPSED=$(( $END - $START ))
echo "-->tarball created ($ELAPSED seconds)"


# setup the deployment staging area
cd ${DEPLOY_DIR}

if mv ${GITHUB_DIR}/../${TARBALL_NAME}.tar.gz ${TARBALL_NAME}.tar.gz; 
then
	if mkdir ${DEPLOY_STAGING_DIR}/vbiz-new
	then 
		mv ${TARBALL_NAME}.tar.gz ${DEPLOY_STAGING_DIR}/vbiz-new;
	else
		echo "mkdir ${DEPLOY_STAGING_DIR}/vbiz-new failed. Aborting.."; exit 1;
	fi
else
	echo "${TARBALL_NAME}.tar.gz does not exist. Aborting.."; exit 1;
fi	

cd ${DEPLOY_STAGING_DIR}/vbiz-new/

tar -zxvf ${TARBALL_NAME}.tar.gz >/dev/null 2>&1

mv ${TARBALL_NAME}.tar.gz ../..

cd ../..

cp customfiles/framework/base/config/cache.properties ${DEPLOY_STAGING_DIR}/vbiz-new/framework/base/config/cache.properties


cp customfiles/framework/common/config/general.properties ${DEPLOY_STAGING_DIR}/vbiz-new/framework/common/config/general.properties

cp customfiles/framework/common/webcommon/forgotPassword.ftl ${DEPLOY_STAGING_DIR}/vbiz-new/framework/common/webcommon/forgotPassword.ftl

cp customfiles/framework/common/webcommon/login.ftl ${DEPLOY_STAGING_DIR}/vbiz-new/framework/common/webcommon/login.ftl

cp customfiles/framework/entity/config/entityengine.xml ${DEPLOY_STAGING_DIR}/vbiz-new/framework/entity/config/entityengine.xml


cp customfiles/tools/bin/pdftotext ${DEPLOY_STAGING_DIR}/vbiz-new/tools/bin/pdftotext

chown -R vbiz ${DEPLOY_STAGING_DIR}/vbiz-new
END=`date +%s`
ELAPSED=$(( $END - $START ))
echo "*****End deployment initialization! ($ELAPSED seconds)*****"
