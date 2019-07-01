package com.amazonaws.mws.client;

import java.net.URI;

/**
 * Published URI's for MWS services.
 * 
 * @author mayerj
 * 
 */
public class MwsEndpoints {

    /** URI for CN production. */
    public static final URI CN_PROD;

    /** URI for DE production. */
    public static final URI DE_PROD;

    /** URI for ES production. */
    public static final URI ES_PROD;

    /** URI for EU production. */
    public static final URI EU_PROD;

    /** URI for FR production. */
    public static final URI FR_PROD;

    /** URI for IN production. */
    public static final URI IN_PROD;

    /** URI for IT production. */
    public static final URI IT_PROD;

    /** URI for JP production. */
    public static final URI JP_PROD;

    /** URI for NA production. */
    public static final URI NA_PROD;

    /** URI for UK production. */
    public static final URI UK_PROD;

    static {
        try {
            CN_PROD = new URI("https://mws.amazonaws.com.cn");
            DE_PROD = new URI("https://mws.amazonaws.de");
            ES_PROD = new URI("https://mws.amazonaws.es");
            EU_PROD = new URI("https://mws-eu.amazonaws.com");
            FR_PROD = new URI("https://mws.amazonaws.fr");
            IN_PROD = new URI("https://mws.amazonaws.in");
            IT_PROD = new URI("https://mws.amazonaws.it");
            JP_PROD = new URI("https://mws.amazonaws.jp");
            NA_PROD = new URI("https://mws.amazonaws.com");
            UK_PROD = new URI("https://mws.amazonaws.co.uk");
        } catch (Exception e) {
            throw MwsUtl.wrap(e);
        }
    }

    /** Never instantiated, but is extended. */
    protected MwsEndpoints() {
        //
    }

}
