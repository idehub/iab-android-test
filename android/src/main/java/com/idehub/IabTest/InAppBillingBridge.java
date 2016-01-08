package com.idehub.IabTest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.Map;

import com.idehub.IabTest.BillingHandler;

public class InAppBillingBridge extends ReactContextBaseJavaModule {
    ReactApplicationContext _reactContext;
    String LICENSE_KEY = null;
    String MERCHANT_ID = null;

    public InAppBillingBridge(ReactApplicationContext reactContext, String licenseKey, String merchantId) {
        super(reactContext);
        _reactContext = reactContext;
        LICENSE_KEY = licenseKey;
        MERCHANT_ID = merchantId;
    }

    @Override
    public String getName() {
        return "InAppBillingBridge";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        return constants;
    }

    @ReactMethod
    public void getPurchaseListingDetails(final String productId, final Promise promise) {
        try {
            if (isIabServiceAvailable()) {
                BillingHandler handler = new BillingHandler(
                    new BillingHandler.IBillingInitialized() {
                        @Override
                        public void invoke(BillingProcessor bp) {
                            SkuDetails details = bp.getPurchaseListingDetails(productId);
                            if (details != null) {
                                WritableMap map = Arguments.createMap();
        
                                map.putString("productId", details.productId);
                                map.putString("title", details.title);
                                map.putString("description", details.description);
                                map.putBoolean("isSubscription", details.isSubscription);
                                map.putString("currency", details.currency);
                                map.putDouble("priceValue", details.priceValue);
                                map.putString("priceText", details.priceText);

                                promise.resolve(map);
                            }
                            else {
                                promise.reject("Details was not found.");
                            }
                            bp.release();
                        }
                    }, null, null, null);
                handler.setupBillingProcessor(_reactContext, LICENSE_KEY, MERCHANT_ID);
            }
        } catch (Exception e) {
            promise.reject("Unknown error.");
        }
    }

    private Boolean isIabServiceAvailable() {
        return BillingProcessor.isIabServiceAvailable(_reactContext);
    }
}
