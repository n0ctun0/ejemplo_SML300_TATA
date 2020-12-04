package com.example.ejemplo_sml300_tata;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ejemplo_sml300_tata.sdkstar.Communication;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import java.nio.charset.Charset;

public class MainActivity2 extends AppCompatActivity {


    private ProgressDialog mProgressDialog;
    byte[] printData;
    Button imprimir;
    EditText textbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mProgressDialog = new ProgressDialog(MainActivity2.this);
        mProgressDialog.setMessage("Communicating...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        imprimir = findViewById(R.id.btn_imprimir);
        textbt= findViewById(R.id.txt_bluetoth);

        imprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                print();
            }
        });

    }

    private void print() {

        mProgressDialog.show();


        Charset encoding = Charset.forName("CP437");

        byte[] nombreproducto= "Descripcion del Producto".getBytes(encoding);
        byte[] codigointerno = ("Codigo Interno: "+ "1234").getBytes(encoding);
        byte[] codigobarra = ("1234567895215").getBytes();
        byte[] precio = ("$:"+"150").getBytes();

        ICommandBuilder builder = StarIoExt.createCommandBuilder(StarIoExt.Emulation.StarPRNTL);

        builder.beginDocument();
        builder.appendTopMargin(6);
        builder.appendCodePage(ICommandBuilder.CodePageType.CP437);

        //*********************************
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Left);

        builder.appendAbsolutePosition(nombreproducto,10);
        builder.appendLineFeed();
        builder.appendLineSpace(24);

        builder.appendAbsolutePosition(codigointerno,10);
        builder.appendMultiple(3, 3);
        builder.appendAbsolutePosition(precio,250);
        builder.appendLineFeed();

        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
        builder.appendBarcodeWithAbsolutePosition(codigobarra, ICommandBuilder.BarcodeSymbology.JAN13, ICommandBuilder.BarcodeWidth.Mode1, 50, false, 10);

        //**********************

        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();

        printData = builder.getCommands();

        Communication.sendCommands(this, printData, "BT:" + textbt.getText().toString(), "Portable", 10000, 30000, MainActivity2.this, mCallback);     // 10000mS!!!

    }

    private final Communication.SendCallback mCallback = new Communication.SendCallback() {
        @Override
        public void onStatus(Communication.CommunicationResult communicationResult) {

            String a =  communicationResult.getResult().toString();;
            Log.e("Mensaje", a);

            if (a.equals("Success")){

                Toast.makeText(MainActivity2.this,
                        "Impresión Correcta",
                        Toast.LENGTH_SHORT).show();

            }else if (a.equals("ErrorOpenPort")){

                Toast.makeText(MainActivity2.this,
                        "Error por Bluetooth",
                        Toast.LENGTH_SHORT).show();
            }else if(a.equals("ErrorBeginCheckedBlock")){

                Toast.makeText(MainActivity2.this,
                        "Error Tapa Abierta",
                        Toast.LENGTH_SHORT).show();
            }else if(a.equals("ErrorEndCheckedBlock")) {

                Toast.makeText(MainActivity2.this,
                        "Error por interrupción",
                        Toast.LENGTH_SHORT).show();

            }else{

                Toast.makeText(MainActivity2.this,
                        "Error Desconocido",
                        Toast.LENGTH_SHORT).show();
            }

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                Log.e("mensaje", "cerrar dialog");
            }

        }
    };

}