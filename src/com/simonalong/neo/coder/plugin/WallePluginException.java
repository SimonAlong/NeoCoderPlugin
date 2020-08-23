package com.simonalong.neo.coder.plugin;

/**
 * @author shizi
 * @since 2020/7/15 3:23 PM
 */
public class WallePluginException extends RuntimeException {

    public WallePluginException(){
        super();
    }

    public WallePluginException(String errMsg) {
        super(errMsg);
    }
}
