package com.example.myapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.renderscript.Sampler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapp.Common.Common;
import com.example.myapp.Common.Config;
import com.example.myapp.Database.Database;
import com.example.myapp.Model.Order;
import com.example.myapp.Model.Request;
import com.example.myapp.ViewHolder.CartAdapter;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Cart extends AppCompatActivity {



    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txtTotalPrice;
    Button btnPlace;


    List<Order> cart =new ArrayList<>();

    CartAdapter adapter;

    //paypal payment
    static PayPalConfiguration config=new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    String address,comment;
    private static  final int PAYPAL_REQUEST_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Init paypal
        Intent intent =new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        //fire base
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");



        //int
        recyclerView=(RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        txtTotalPrice=(TextView)findViewById(R.id.total);
        btnPlace=(Button)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create new request
                if(cart.size()>0)
                    showAlertDialog();
                else
                {
                    Toast.makeText(Cart.this,"your cart is empty!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        loadListFood();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("one more step");
        alertDialog.setMessage("enter you address");

        final EditText edtAddress=new EditText(Cart.this);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        edtAddress.setLayoutParams(lp);
        alertDialog.setView(edtAddress);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //show paypal to payment
                //first ,get adress and comment from aler diagra
                address=edtAddress.getText().toString();
                String formatAmount=txtTotalPrice.getText().toString().replace("$","")
                        .replace(",","");
              //  PayPalPayment payPalPayment=new PayPalPayment(new BigDecimal(formatAmount),"USD","Eat It App Order",
                //        PayPalPayment.PAYMENT_INTENT_SALE);


                PayPalPayment payPalPayment = getStuffToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent=new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);


                startActivityForResult(intent,PAYPAL_REQUEST_CODE);

            }

              /*  Request request=new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart
                );
                //submit to fire base
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);
                //delete cart
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this,"Thank you,Order Place", Toast.LENGTH_SHORT).show();
                finish();
            }*/
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();


    }

    /*
     * This method shows use of optional payment details and item list.
     */
    private PayPalPayment getStuffToBuy(String paymentIntent) {
        //--- include an item list, payment amount details

        PayPalItem[] items=new PayPalItem[cart.size()];
        for(int i=0;i<items.length;i++)
        {
            items[i]=  new PayPalItem("#"+i, Integer.valueOf(cart.get(i).getQuantity()), new BigDecimal((Integer.valueOf(cart.get(i).getQuantity())*(Integer.valueOf(cart.get(i).getPrice())))), "USD",
                    cart.get(i).getProductName());
        }
        BigDecimal subtotal = PayPalItem.getItemTotal(items);
        BigDecimal shipping = new BigDecimal("7.21");
        BigDecimal tax = new BigDecimal("4.67");
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        PayPalPayment payment = new PayPalPayment(amount, "USD", "sample item", paymentIntent);
        payment.items(items).paymentDetails(paymentDetails);

        //--- set other optional fields like invoice_number, custom field, and soft_descriptor
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PAYPAL_REQUEST_CODE)
        {
            Toast.makeText(this,"Payment cancel",Toast.LENGTH_SHORT).show();

           if(resultCode==Activity.RESULT_OK)
            {

                PaymentConfirmation confirmation=data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation!=null)
                {
                    try{
                        String paymentDetail=confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject=new JSONObject(paymentDetail);

                        //crate new request

                        Request request=new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        address,
                        txtTotalPrice.getText().toString(),
                        "0",
                        jsonObject.getJSONObject("response").getString("state"),cart
                       );
                        //submit to fire base
                        requests.child(String.valueOf(System.currentTimeMillis()))
                                .setValue(request);
                        //delete cart
                        new Database(getBaseContext()).cleanCart();
                        Toast.makeText(Cart.this,"Thank you,Order Place", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (JSONException e) {
                        Log.e("Payment", "failure occured:",e);

                        e.printStackTrace();

                    }
                }

            }
            else if(resultCode== Activity.RESULT_CANCELED){
                Toast.makeText(this,"Payment cancel",Toast.LENGTH_SHORT).show();
            }
            else if(resultCode==PaymentActivity.RESULT_EXTRAS_INVALID) {

                Toast.makeText(this, "invalid payment", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadListFood() {
        cart= new Database(this).getCarts();
        adapter=new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        // total
        int total=0;
        for(Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));

        Locale locale=new Locale("en","US");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
        
    }

    private void deleteCart(int position) {
        //we will remove item at list<order>by position
        cart.remove(position);
        //after that we will delete all old data from sqlite
        new Database(this).cleanCart();
        //and final , we will update new data from list<order> to sqlite
        for(Order item:cart)
            new Database(this).addToCart(item);
        //refresh
        loadListFood();

    }
}
