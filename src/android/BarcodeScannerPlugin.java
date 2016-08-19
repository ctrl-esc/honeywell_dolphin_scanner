package org.limitstate.honeywell;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;

import org.pluginporo.aamva.Decoder;
import org.pluginporo.aamva.DriverLicense;

import com.honeywell.decodemanager.DecodeManager;
import com.honeywell.decodemanager.SymbologyConfigs;
import com.honeywell.decodemanager.barcode.DecodeResult;
import com.honeywell.decodemanager.barcode.CommonDefine;

// Add symbologies here
import com.honeywell.decodemanager.symbologyconfig.SymbologyConfigCode39;
import com.honeywell.decodemanager.symbologyconfig.SymbologyConfigCodeAztec;

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

			if ((decodeManager == null)) {
				decodeManager = new DecodeManager(((CordovaActivity)this.cordova.getActivity()), ScanResultHandler);
			}
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
				String strDecodeResult = "";
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
