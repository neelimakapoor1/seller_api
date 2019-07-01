/******************************************************************************* 
 *  Copyright 2009 Amazon Services.
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  
 *  You may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at: http://aws.amazon.com/apache2.0
 *  This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 *  CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 * ***************************************************************************** 
 *
 *  Marketplace Web Service Java Library
 *  API Version: 2009-01-01
 *  Generated: Wed Feb 18 13:28:48 PST 2009 
 * 
 */



package com.amazonaws.mws.samples;

import java.util.List;
import java.util.ArrayList;
import com.amazonaws.mws.*;
import com.amazonaws.mws.model.*;
import com.amazonaws.mws.mock.MarketplaceWebServiceMock;

/**
 *
 * Manage Report Schedule  Samples
 *
 *
 */
public class ManageReportScheduleSample {

    /**
     * Just add a few required parameters, and try the service
     * Manage Report Schedule functionality
     *
     * @param args unused
     */
    public static void main(String... args) {

        /************************************************************************
         * Access Key ID and Secret Access Key ID, obtained from:
         * http://aws.amazon.com
         ***********************************************************************/
        final String accessKeyId = "<Your Access Key ID>";
        final String secretAccessKey = "<Your Secret Access Key>";

        final String appName = "<Your Application or Company Name>";
        final String appVersion = "<Your Application Version or Build Number or Release Date>";

        MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();

        /************************************************************************
         * Uncomment to set the appropriate MWS endpoint.
         ************************************************************************/
        // US
        // config.setServiceURL("https://mws.amazonaws.com/");
        // UK
        // config.setServiceURL("https://mws.amazonaws.co.uk/");
        // Germany
        // config.setServiceURL("https://mws.amazonaws.de/");
        // France
        // config.setServiceURL("https://mws.amazonaws.fr/");
        // Italy
        // config.setServiceURL("https://mws.amazonaws.it/");
        // Japan
        // config.setServiceURL("https://mws.amazonaws.jp/");
        // China
        // config.setServiceURL("https://mws.amazonaws.com.cn/");
        // Canada
        // config.setServiceURL("https://mws.amazonaws.ca/");
        // India
        // config.setServiceURL("https://mws.amazonaws.in/");

        /************************************************************************
         * You can also try advanced configuration options. Available options are:
         *
         *  - Signature Version
         *  - Proxy Host and Proxy Port
         *  - User Agent String to be sent to Marketplace Web Service
         *
         ***********************************************************************/

        /************************************************************************
         * Instantiate Http Client Implementation of Marketplace Web Service        
         ***********************************************************************/

        MarketplaceWebService service = new MarketplaceWebServiceClient(
                accessKeyId, secretAccessKey, appName, appVersion, config);

        /************************************************************************
         * Uncomment to try out Mock Service that simulates Marketplace Web Service 
         * responses without calling Marketplace Web Service  service.
         *
         * Responses are loaded from local XML files. You can tweak XML files to
         * experiment with various outputs during development
         *
         * XML files available under com/amazonaws/mws/mock tree
         *
         ***********************************************************************/
        // MarketplaceWebService service = new MarketplaceWebServiceMock();

        /************************************************************************
         * Setup request parameters and uncomment invoke to try out 
         * sample for Manage Report Schedule 
         ***********************************************************************/

        /************************************************************************
         * Marketplace and Merchant IDs are required parameters for all 
         * Marketplace Web Service calls.
         ***********************************************************************/
        final String merchantId = "<Your Merchant ID>";
        final String sellerDevAuthToken = "<Merchant Developer MWS Auth Token>";

        ManageReportScheduleRequest request = new ManageReportScheduleRequest();
        request.setMerchant( merchantId );
        //request.setMWSAuthToken(sellerDevAuthToken);

        // @TODO: set request parameters here

        // invokeManageReportSchedule(service, request);

    }



    /**
     * Manage Report Schedule  request sample
     * Creates, updates, or deletes a report schedule
     * for a given report type, such as order reports in particular.
     *   
     * @param service instance of MarketplaceWebService service
     * @param request Action to invoke
     */
    public static void invokeManageReportSchedule(MarketplaceWebService service, ManageReportScheduleRequest request) {
        try {

            ManageReportScheduleResponse response = service.manageReportSchedule(request);


            System.out.println ("ManageReportSchedule Action Response");
            System.out.println ("=============================================================================");
            System.out.println ();

            System.out.print("    ManageReportScheduleResponse");
            System.out.println();
            if (response.isSetManageReportScheduleResult()) {
                System.out.print("        ManageReportScheduleResult");
                System.out.println();
                ManageReportScheduleResult  manageReportScheduleResult = response.getManageReportScheduleResult();
                if (manageReportScheduleResult.isSetCount()) {
                    System.out.print("            Count");
                    System.out.println();
                    System.out.print("                " + manageReportScheduleResult.getCount());
                    System.out.println();
                }
                java.util.List<ReportSchedule> reportScheduleList = manageReportScheduleResult.getReportScheduleList();
                for (ReportSchedule reportSchedule : reportScheduleList) {
                    System.out.print("            ReportSchedule");
                    System.out.println();
                    if (reportSchedule.isSetReportType()) {
                        System.out.print("                ReportType");
                        System.out.println();
                        System.out.print("                    " + reportSchedule.getReportType());
                        System.out.println();
                    }
                    if (reportSchedule.isSetSchedule()) {
                        System.out.print("                Schedule");
                        System.out.println();
                        System.out.print("                    " + reportSchedule.getSchedule());
                        System.out.println();
                    }
                    if (reportSchedule.isSetScheduledDate()) {
                        System.out.print("                ScheduledDate");
                        System.out.println();
                        System.out.print("                    " + reportSchedule.getScheduledDate());
                        System.out.println();
                    }
                }
            } 
            if (response.isSetResponseMetadata()) {
                System.out.print("        ResponseMetadata");
                System.out.println();
                ResponseMetadata  responseMetadata = response.getResponseMetadata();
                if (responseMetadata.isSetRequestId()) {
                    System.out.print("            RequestId");
                    System.out.println();
                    System.out.print("                " + responseMetadata.getRequestId());
                    System.out.println();
                }
            } 
            System.out.println();
            System.out.println(response.getResponseHeaderMetadata());
            System.out.println();


        } catch (MarketplaceWebServiceException ex) {

            System.out.println("Caught Exception: " + ex.getMessage());
            System.out.println("Response Status Code: " + ex.getStatusCode());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Error Type: " + ex.getErrorType());
            System.out.println("Request ID: " + ex.getRequestId());
            System.out.print("XML: " + ex.getXML());
            System.out.println("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata());
        }
    }

}
