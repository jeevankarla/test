
/**
 * APDairyWCFServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5  Built on : Apr 30, 2009 (06:07:24 EDT)
 */

    package org.procurementURI;

    /**
     *  APDairyWCFServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class APDairyWCFServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public APDairyWCFServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public APDairyWCFServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getDataByBMC_Village_Date method
            * override this method for handling normal response from getDataByBMC_Village_Date operation
            */
           public void receiveResultgetDataByBMC_Village_Date(
                    org.procurementURI.APDairyWCFServiceStub.GetDataByBMC_Village_DateResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getDataByBMC_Village_Date operation
           */
            public void receiveErrorgetDataByBMC_Village_Date(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getDataByBMC_Date method
            * override this method for handling normal response from getDataByBMC_Date operation
            */
           public void receiveResultgetDataByBMC_Date(
                    org.procurementURI.APDairyWCFServiceStub.GetDataByBMC_DateResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getDataByBMC_Date operation
           */
            public void receiveErrorgetDataByBMC_Date(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getDataByDate method
            * override this method for handling normal response from getDataByDate operation
            */
           public void receiveResultgetDataByDate(
                    org.procurementURI.APDairyWCFServiceStub.GetDataByDateResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getDataByDate operation
           */
            public void receiveErrorgetDataByDate(java.lang.Exception e) {
            }
                


    }
    