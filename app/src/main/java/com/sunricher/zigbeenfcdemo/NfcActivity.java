package com.sunricher.zigbeenfcdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.sunricher.zigbeenfcdemo.model.AttributeItem;
import com.sunricher.zigbeenfcdemo.model.HexUtil;
import com.sunricher.zigbeenfcdemo.model.NfcCommand;
import com.sunricher.zigbeenfcdemo.model.ZigbeeCctDevice;
import com.sunricher.zigbeenfcdemo.model.ZigbeeDimDevice;
import com.sunricher.zigbeenfcdemo.view.DeviceAdapter;
import com.sunricher.zigbeenfcdemo.view.OnItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NfcActivity extends AppCompatActivity {

    public static final String LOG_TAG = "NfcActivity";

    enum ViewStatus {
        initial,
        zigbeeDim,
        zigbeeCct
    }

    enum NfcStatus {
        none,
        reading,
        writing
    }

    enum NfcResult {
        ok,  // The nfc operation is successful.
        nfcError, // The nfc operation is failed.
        deviceTypeError // The device type is wrong.
    }

    private ViewStatus viewStatus = ViewStatus.initial;
    private NfcStatus nfcStatus = NfcStatus.none;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private ZigbeeDimDevice zigbeeDimDevice;
    private ZigbeeCctDevice zigbeeCctDevice;

    private Button scanBtn;
    private Button createBtn;
    private RecyclerView recyclerView;
    private AlertDialog alertDialog;
    private DeviceAdapter deviceAdapter;

    private AttributeItem selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        setUpNfc();

        scanBtn = findViewById(R.id.scan_btn);
        scanBtn.setOnClickListener(v -> {
            handleScan();
        });
        createBtn = findViewById(R.id.create_btn);
        createBtn.setOnClickListener(v -> {
            handleCreate();
        });

        recyclerView = findViewById(R.id.device_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        OnItemClickListener listener = new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                handleAttributeItemClicked(position);
            }
        };
        deviceAdapter = new DeviceAdapter(new ArrayList<>(), listener);
        recyclerView.setAdapter(deviceAdapter);

        updateViewStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nfcAdapter = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        switch (nfcStatus) {
            case none:
                break;
            case reading: {
                NfcResult readingResult = handleReadingIntent(intent);
                showNfcResult(readingResult);
                break;
            }
            case writing: {
                NfcResult writingResult = handleWritingIntent(intent);
                showNfcResult(writingResult);
                break;
            }
        }
    }

    private void setUpNfc() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(),
                    getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        } else {
            // Please don't change the flags to other value, it must be set to 0.
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(),
                    getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        }
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    private NfcResult handleReadingIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) {
            return NfcResult.nfcError;
        }
        NfcA nfcA = NfcA.get(tag);
        if (nfcA == null) {
            return NfcResult.nfcError;
        }
        try {
            nfcA.connect();
            // Before send data to the NFC, you have to send SECRET_KEY to the device.
            nfcA.transceive(NfcCommand.SECRET_KEY);
            // Read the product ID of the device.
            byte[] productIdResult = nfcA.transceive(NfcCommand.readSingleAttribute(NfcCommand.ZigbeeNfcPage.PRODUCT_ID));
            int productId = HexUtil.bytesToInt(productIdResult);
            Log.i(LOG_TAG, "productId " + productId);
            if (viewStatus == ViewStatus.zigbeeDim) {
                if (zigbeeDimDevice.getProductId() != productId) {
                    // If the productId doesn't equal the product ID of the zigbee dim, return device type error.
                    return NfcResult.deviceTypeError;
                }
            } else if (viewStatus == ViewStatus.zigbeeCct) {
                if (zigbeeCctDevice.getProductId() != productId) {
                    return NfcResult.deviceTypeError;
                }
            }

            if (selectedItem == null) {
                return NfcResult.nfcError;
            }

            // Read the value of the selected item nfc page.
            byte[] itemValueBytes = nfcA.transceive(NfcCommand.readSingleAttribute(selectedItem.getNfcPage()));
            selectedItem.setNfcValue(itemValueBytes);
            deviceAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            return NfcResult.nfcError;
        }
        return NfcResult.ok;
    }

    private NfcResult handleWritingIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) {
            return NfcResult.nfcError;
        }
        NfcA nfcA = NfcA.get(tag);
        if (nfcA == null) {
            return NfcResult.nfcError;
        }
        try {
            nfcA.connect();
            // Before send data to the NFC, you have to send SECRET_KEY to the device.
            nfcA.transceive(NfcCommand.SECRET_KEY);
            // Read the product ID of the device.
            byte[] productIdResult = nfcA.transceive(NfcCommand.readSingleAttribute(NfcCommand.ZigbeeNfcPage.PRODUCT_ID));
            int productId = HexUtil.bytesToInt(productIdResult);
            Log.i(LOG_TAG, "productId " + productId);
            if (viewStatus == ViewStatus.zigbeeDim) {
                if (zigbeeDimDevice.getProductId() != productId) {
                    // If the productId doesn't equal the product ID of the zigbee dim, return device type error.
                    return NfcResult.deviceTypeError;
                }
            } else if (viewStatus == ViewStatus.zigbeeCct) {
                if (zigbeeCctDevice.getProductId() != productId) {
                    return NfcResult.deviceTypeError;
                }
            }

            if (selectedItem == null) {
                return NfcResult.nfcError;
            }

            // Send the value to the device.
            byte nfcPage = selectedItem.getNfcPage();
            byte[] sendResultBytes = nfcA.transceive(NfcCommand.writeNfcData(nfcPage, selectedItem.getItemValue()));
        } catch (Exception e) {
            e.printStackTrace();
            return NfcResult.nfcError;
        }
        return NfcResult.ok;
    }

    private void showNfcResult(NfcResult result) {
        switch (result) {
            case ok: {
                showResultDialog("OK", "Operation successful.");
                break;
            }
            case nfcError: {
                showResultDialog("NFC Error", "There is an error of the NFC operation.");
                break;
            }
            case deviceTypeError: {
                showResultDialog("Device Type Error", "This device has a wrong device type, the product ID is not valid.");
                break;
            }
        }
        nfcStatus = NfcStatus.none;
    }

    private void handleScan() {
        // TODO: handleScan a device.
    }

    private void handleCreate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.create_a_device);
        builder.setMessage(null);
        builder.setPositiveButton(R.string.zigbee_dim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                zigbeeDimDevice = new ZigbeeDimDevice();
                deviceAdapter.setItems(zigbeeDimDevice.getAttributeItems());
                viewStatus = ViewStatus.zigbeeDim;
                updateViewStatus();
            }
        });
        builder.setNegativeButton(R.string.zigbee_cct, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                zigbeeCctDevice = new ZigbeeCctDevice();
                deviceAdapter.setItems(zigbeeCctDevice.getAttributeItems());
                viewStatus = ViewStatus.zigbeeCct;
                updateViewStatus();
            }
        });
        builder.setNeutralButton(R.string.cancel, null);
        builder.create().show();
    }

    private void updateViewStatus() {
        switch (viewStatus) {
            case initial: {
                scanBtn.setVisibility(View.VISIBLE);
                createBtn.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                break;
            }
            case zigbeeDim:
            case zigbeeCct: {
                scanBtn.setVisibility(View.INVISIBLE);
                createBtn.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    private void handleAttributeItemClicked(int position) {
        if (deviceAdapter.getItems().size() > position) {
            AttributeItem item = deviceAdapter.getItems().get(position);
            selectedItem = item;
            if (item.getNfcPage() == NfcCommand.ZigbeeNfcPage.PRODUCT_ID) {
                // Product ID is a const value.
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(item.getTitle());
            builder.setMessage(item.getDetail());
            builder.setPositiveButton(R.string.write, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nfcStatus = NfcStatus.writing;
                    showReadingOrWritingDialog("Writing", selectedItem.getTitle());
                }
            });
            builder.setNegativeButton(R.string.read, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nfcStatus = NfcStatus.reading;
                    showReadingOrWritingDialog("Reading", selectedItem.getTitle());
                }
            });
            builder.setNeutralButton(R.string.cancel, null);
            builder.create().show();
        }
    }

    private void showReadingOrWritingDialog(String title, String message) {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton("STOP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nfcStatus = NfcStatus.none;
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showResultDialog(String title, String message) {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton("OK", null);
        alertDialog = builder.create();
        alertDialog.show();
    }
}