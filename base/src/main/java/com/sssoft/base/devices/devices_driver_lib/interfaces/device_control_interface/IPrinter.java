package com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface;


import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PrinterListener;

public interface IPrinter {
     void addText(android.os.Bundle format, String text) throws android.os.RemoteException;
     void addPicture(android.os.Bundle format, android.graphics.Bitmap bitmap) throws android.os.RemoteException;
     void addBarCode(android.os.Bundle format, String barCode) throws android.os.RemoteException;
     void addQrCode(android.os.Bundle format, String qrCode) throws android.os.RemoteException;
     void paperSkip(int line) throws android.os.RemoteException;
     void startPrinter(final PrinterListener printerLis) throws android.os.RemoteException;
     String getStatus() throws android.os.RemoteException;
}
