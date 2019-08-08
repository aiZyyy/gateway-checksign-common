/**
 * Project Name:json-scanner
 * File Name:Feature.java
 * Package Name:com.bus.card.common.json
 *  
 * Copyright (c) 2015, www.mmaildo-soft.com All Rights Reserved.
 *
*/

package com.sixi.gateway.checksignservice.oauth.json;

/**
 * ClassName:Feature <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 *  
 * @author   mmail
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
/**
 * @author wenshao[szujobs@hotmail.com]
 */
public enum Feature {
    /**
	 * 
	 */
    AutoCloseSource,

    /**
	 * 
	 */
    AllowComment,

    /**
	 * 
	 */
    AllowUnQuotedFieldNames,

    /**
	 * 
	 */
    AllowSingleQuotes,

    /**
	 * 
	 */
    InternFieldNames,

    /**
	 * 
	 */
    AllowISO8601DateFormat,

    /**
     * {"a":1,,,"b":2}
     */
    AllowArbitraryCommas,

    /**
     * 
     */
    UseBigDecimal,
    
    /**
     * @since 1.1.2
     */
    IgnoreNotMatch,

    /**
     * @since 1.1.3
     */
    SortFeidFastMatch,
    
    /**
     * @since 1.1.3
     */
    DisableASM,
    
    /**
     * @since 1.1.7
     */
    DisableCircularReferenceDetect,
    
    /**
     * @since 1.1.10
     */
    InitStringFieldAsEmpty,
    
    /**
     * @since 1.1.35
     * 
     */
    SupportArrayToBean,
    
    /**
     * @since 1.2.3
     * 
     */
    OrderedField
    ;

    private Feature(){
        mask = (1 << ordinal());
    }

    private final int mask;

    public final int getMask() {
        return mask;
    }

    public static boolean isEnabled(int features, Feature feature) {
        return (features & feature.getMask()) != 0;
    }

    public static int config(int features, Feature feature, boolean state) {
        if (state) {
            features |= feature.getMask();
        } else {
            features &= ~feature.getMask();
        }

        return features;
    }
    
    public static int of(Feature[] features) {
        if (features == null) {
            return 0;
        }
        
        int value = 0;
        
        for (Feature feature: features) {
            value |= feature.getMask();
        }
        
        return value;
    }
}