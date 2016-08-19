var cordova = require('cordova');
var exec = require('cordova/exec');

var HoneywellScannerPlugin = function() {

  this.scan = function(success_cb, error_cb){
    exec(success_cb, error_cb, "BarcodeScannerPlugin", "scan", ["SYM_ALL"]);
  };

  this.startListen = function(success_cb, error_cb){
    exec(success_cb, error_cb, "BarcodeScannerPlugin", "start", []);
  };

};

var honeywellScannerPlugin = new HoneywellScannerPlugin();
module.exports = honeywellScannerPlugin;
