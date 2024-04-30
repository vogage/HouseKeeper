package io.getstream.webrtc.sample.compose.car.serialcom;


import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;


public class SerialCom {
  UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
  UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
  UsbSerialProber usbCustomProber = CustomProber.getCustomProber();
        listItems.clear();
}
