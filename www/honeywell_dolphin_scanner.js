var cordova = require('cordova');
var exec = require('cordova/exec');

var HoneywellDolphinScanner = function() {

  this.scan = function(success_cb, error_cb){
    exec(success_cb, error_cb, "HoneywellDolphinScannerPlugin", "scan", []);
  };

  this.startListen = function(success_cb, error_cb){
    exec(success_cb, error_cb, "HoneywellDolphinScannerPlugin", "start", []);
  };

};

var honeywellDolphinScanner = new HoneywellDolphinScanner();
module.exports = honeywellDolphinScanner;