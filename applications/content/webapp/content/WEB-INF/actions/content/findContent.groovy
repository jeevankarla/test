


parameters.isPublic = 'Y';
if(userLogin){
	if(security.hasEntityPermission("CONTENTMGR", "_ADMIN", session)){
		parameters.isPublic = '';
	}
}