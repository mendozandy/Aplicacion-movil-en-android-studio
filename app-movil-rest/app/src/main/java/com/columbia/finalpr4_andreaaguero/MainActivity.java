package com.columbia.finalpr4_andreaaguero;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import Dto.EmpresasDto;
import Genericos.CONSTANTES;
import ManejoV.ConnectionVolley;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    private Gson gson;
    private String urlEmpresas;
    private EditText txId,razon_social,ruc, direccion, telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txId = (EditText) findViewById(R.id.txId);
        razon_social = (EditText) findViewById(R.id.txRazon_social);
        ruc = (EditText) findViewById(R.id.txRuc);
        direccion = (EditText) findViewById(R.id.txDireccion);
        telefono = (EditText) findViewById(R.id.txTelefono);
    }

    public void recuperar(View view){
        if (txId.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"Ingrese Valor",Toast.LENGTH_SHORT).show();
        }else{
            recuperarRegistro(txId.getText().toString().trim());
        }

    }

    private void recuperarRegistro(String x){

        urlEmpresas= CONSTANTES.VPS_GOOGLE + "/empresas?id="+x+"";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlEmpresas,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseJson) {
                        Log.i("Valor respuesta " , responseJson);
                        EmpresasDto[] puDTO=new Gson().fromJson(responseJson, EmpresasDto [].class);
                        for (EmpresasDto  item : puDTO){
                            if (item.getId().toString().equals("0")){
                                Toast.makeText(getApplicationContext(),"No existe valor Ingresado",Toast.LENGTH_SHORT).show();
                                txId.requestFocus();
                            }else{
                                txId.setText(item.getId().toString());
                                razon_social.setText(item.getRazon_social());
                                ruc.setText(item.getRuc());
                                direccion.setText(item.getDireccion());
                                telefono.setText(item.getTelefono());
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                razon_social.setText(error.toString());

            }
        });
        ConnectionVolley.getInstancia(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void Agregar(View view) {
        urlEmpresas= CONSTANTES.VPS_GOOGLE + "/empresas";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", 0);
            jsonBody.put("razon_social", razon_social.getText());
            jsonBody.put("ruc", ruc.getText());
            jsonBody.put("direccion", direccion.getText());
            jsonBody.put("telefono", telefono.getText());

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlEmpresas, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), "Registro guardado con exito!!!", Toast.LENGTH_SHORT).show();
                    reset();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            ConnectionVolley.getInstancia(getApplicationContext()).addToRequestQueue(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Modificar(View view) {

        try {
            urlEmpresas= CONSTANTES.VPS_GOOGLE + "/empresas" +"/update";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", txId.getText().toString().trim());
            jsonBody.put("razon_social", razon_social.getText());
            jsonBody.put("ruc", ruc.getText());
            jsonBody.put("direccion", direccion.getText());
            jsonBody.put("telefono", telefono.getText());

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlEmpresas, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), "Registro modificado con exito!!!", Toast.LENGTH_SHORT).show();
                    reset();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            ConnectionVolley.getInstancia(getApplicationContext()).addToRequestQueue(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Eliminar(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext())
                .setTitle("Eliminación")
                .setMessage("Está seguro que desea borrar ?")
                .setCancelable(false)
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        eliminar();
                    }
                });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                reset();
            }
        });
        builder.create();
        builder.show();
    }

    private void eliminar() {
        try {
            urlEmpresas= CONSTANTES.VPS_GOOGLE + "/empresas/delete";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", txId.getText().toString().trim());


            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlEmpresas, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), "Registro eliminado con exito!!!", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            ConnectionVolley.getInstancia(getApplicationContext()).addToRequestQueue(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void reset(){
        txId.setText("");
        razon_social.setText("");
        ruc.setText("");
        direccion.setText("");
        telefono.setText("");

    }
}