package com.example.user.bluetooth_discoverdevices;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TCC";

    private final static int REQUEST_ENABLE_BT=0;
    BluetoothAdapter mBluetoothAdapter;
    Button btnEnableDisable_Discoverable;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;
    public List<Devices> myDevices = new ArrayList<Devices>();
    final Handler handler = new Handler();

    /**
     *  Itens para fazer
     **/

    // - Habilitar a quantidade de repetição;
    // - Ao desabilitar o Bluetooth fechar o app

    /**
     * Encontra algo
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Quando inicia a busca por dispositivos
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                //Encontrou

                //Obtem o nivel de sinal
                int RSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);

                //Adiciona a Lista
                mBTDevices.add(device);

                //Lista o dispositivo no log no Android Studio com a força do sinal (Para desenvolvimento)
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress() + " RSSI : " + RSSI);

                for(int i = 0; myDevices.size() > i ; i++ ) {
                    //Se encontrou um ou mais dispositivos...

                    if( myDevices.get(i).code.equals(device.getAddress()) && myDevices.get(i).signalLimit < RSSI ) {
                        //Se o sinal estiver dentro do definido no objeto (Lista de dispositivo)

                        Log.i(TAG,"entrou");

                        //Executa o audio
                        myDevices.get(i).sound.start();
                    }
                }

                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };



    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        /*----------------------------------Cadastro de dispositivos para execução de ação */

        //--------ADD Bem vindo a Faculdade
        myDevices.add( new Devices("Bolacha","0C:F3:EE:04:1D:A4", MediaPlayer.create(MainActivity.this, R.raw.laboratorio_de_eletronica), -60 , 10 ) );
        //--------Add Iphone
        myDevices.add( new Devices("Iphone","4C:7C:5F:0A:9A:09", MediaPlayer.create(MainActivity.this, R.raw.bem_vindo), -60 , 10 ) );
        //--------Add Windows phone
        myDevices.add( new Devices("Windows","14:9A:10:1C:76:95", MediaPlayer.create(MainActivity.this, R.raw.voce_esta_no_predio_de_eng), -60 , 10 ) );
        //--------Add Arduino
        myDevices.add( new Devices("Arduino","20:16:10:25:55:07",MediaPlayer.create(MainActivity.this, R.raw.laboratorio_de_informatica), -60 , 10 ) );




        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBTPermissions();

    }


    @Override
    protected void onStart() {
        super.onStart();
        getDelegate().onStart();

        //Inicia aqui o loop

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Inicia a procura
                if(!mBluetoothAdapter.isEnabled()) {
                    Log.i(TAG, String.valueOf(REQUEST_ENABLE_BT));
                    checkBTPermissions();
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    handler.removeCallbacks(this);

                } else {
                    mBluetoothAdapter.cancelDiscovery();
                    checkBTPermissions();
                    mBluetoothAdapter.startDiscovery();

                    IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
                }

                Log.i(TAG,"loop");

                handler.postDelayed(this, 10000);
            }
        }, 10000);
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

}