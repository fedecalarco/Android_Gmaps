package proyecto.fede.myapplication;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.android.gms.maps.model.Marker;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    // Variables
    private GoogleMap mMap;
    Button actualizar;

    //URLS
    String IP = "http://madrynmap.esy.es";
    String GET = IP + "/obtener_markers.php";

    // Otras variables
    ObtenerWebService hiloConexion;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Hola hola hola");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Enlaces con el XML
       actualizar = (Button) findViewById(R.id.botonActualizar);
        // Listener
      actualizar.setOnClickListener(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng madryn = new LatLng(-42.772287, -65.027726);
        mMap.addMarker(new MarkerOptions().position(madryn).title("Puerto Madryn"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(madryn));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        // Cargamos los puntos
        hiloConexion = new ObtenerWebService();
        hiloConexion.execute(GET,"1");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.botonActualizar:
                hiloConexion = new ObtenerWebService();
                hiloConexion.execute(GET,"1");
                break;
            default:
                break;
        }

    }



    public class ObtenerWebService extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            String urlPuntos = params[0];
            URL url = null;
            String devuelve = "";
            JSONArray arrayMarkers = null;

            if(params[1] == "1"){
                //Consulta de todos los puntos
                try {
                    url = new URL(urlPuntos); // URL de donde queremos obtener la informacion
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                    // Identificador para navegadores CABE
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                            " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                    //connection.setHeader("content-type", "application/json");

                    int respuesta = connection.getResponseCode();

                    StringBuilder result = new StringBuilder();

                    // Verifica si la proyecto.fede.myapplication.conexion es correcta
                    if (respuesta == HttpURLConnection.HTTP_OK){

                        InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada

                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader

                        // El siguiente proceso lo hago porque el JSONOBject necesita un String y tengo
                        // que tranformar el BufferedReader a String. Esto lo hago a traves de un
                        // StringBuilder.

                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);        // Paso toda la entrada al StringBuilder
                        }

                        //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                        JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena

                        arrayMarkers = respuestaJSON.getJSONArray("markers");

                        return arrayMarkers;

                    }else{
                        return null;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return arrayMarkers;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(JSONArray markersJSON) {
            //resultado.setText(s);
            //super.onPostExecute(s);
            for (int i = 0; i < markersJSON.length(); i++) {
                try {
                    String id = markersJSON.getJSONObject(i).getString("id");
                    String name = markersJSON.getJSONObject(i).getString("name");
                    String adress = markersJSON.getJSONObject(i).getString("address");
                    String lat = markersJSON.getJSONObject(i).getString("lat");
                    String lng = markersJSON.getJSONObject(i).getString("lng");


                    double lat1 = Double.parseDouble(lat);
                    double lng1 = Double.parseDouble(lng);

                    LatLng pos = new LatLng(lat1,lng1);
                    mMap.addMarker(new MarkerOptions().position(pos).title(name).snippet(adress));


                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
            Toast.makeText(getBaseContext(),"Actualizado", Toast.LENGTH_SHORT).show();



        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(getBaseContext(),"texto onCancelled", Toast.LENGTH_SHORT).show();
        }
    }

























}
