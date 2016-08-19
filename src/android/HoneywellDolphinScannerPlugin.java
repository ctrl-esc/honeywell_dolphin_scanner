package org.limitstate.honeywell;

import android.app.Activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;

import com.honeywell.decodemanager.DecodeManager;
import com.honeywell.decodemanager.SymbologyConfigs;
import com.honeywell.decodemanager.barcode.DecodeResult;
import com.honeywell.decodemanager.barcode.CommonDefine;

// Add symbologies here
import com.honeywell.decodemanager.symbologyconfig.SymbologyConfigCode128;
import com.honeywell.decodemanager.symbologyconfig.SymbologyConfigCodeQRCode;

public class HoneywellDolphinScannerPlugin extends CordovaPlugin {

	private static final String LOG_TAG = "HoneywellDolphinScannerPlugin";
	private static final int SCANTIMEOUT = 3000;

	DecodeManager decodeManager = null;
	BroadcastReceiver scannerReceiver = null;
	CallbackContext pluginCallbackContext = null;

	public HoneywellDolphinScannerPlugin() {
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (action.equals("scan")) {
			this.pluginCallbackContext = callbackContext;

			if ((decodeManager == null) && (Build.MODEL.toLowerCase().contains("dolphin 70e".toLowerCase()))) {
				decodeManager = new DecodeManager(((CordovaActivity)this.cordova.getActivity()), ScanResultHandler);
			}
			else sendUpdate('Scan not started', false);
			try {
				this.doScan();
				return true;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (action.equals("stop")) {
			callbackContext.success("stopped");
			return true;
		}
		return false;
	}

	private Handler ScanResultHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DecodeManager.MESSAGE_DECODER_COMPLETE:
				DecodeResult decodeResult = (DecodeResult) msg.obj;

				byte codeid = decodeResult.codeId;
				byte aimid = decodeResult.aimId;
				int iLength = decodeResult.length;
				String r = decodeResult.barcodeData;
				sendUpdate(r, false);
				pluginCallbackContext.success("done");
				break;
			case DecodeManager.MESSAGE_DECODER_FAIL: 
				break;
			case DecodeManager.MESSAGE_DECODER_READY: 
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	private void doScan() throws Exception {
		try {
			decodeManager.doDecode(SCANTIMEOUT);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void sendUpdate(String data, boolean keepCallback) {
	    if (this.pluginCallbackContext != null) {
			PluginResult result = new PluginResult(PluginResult.Status.OK, data);
			result.setKeepCallback(keepCallback);
			this.pluginCallbackContext.sendPluginResult(result);
		}
	}

}
