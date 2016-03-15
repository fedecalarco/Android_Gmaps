package proyecto.fede.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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

/**
 * A fragment that launches other parts of the demo application.
 */
public class MapFragment extends Fragment {

    MapView mMapView;
    GoogleMap googleMap;
 //   ObtenerWebService hiloConexion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_location_info, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        // latitude and longitude
        LatLng madryn = new LatLng(-42.772287, -65.027726);

        // create marker
        MarkerOptions marker = new MarkerOptions().position(madryn).title("Test PM");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(madryn).zoom(14).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));


        String IP = "http://madrynmap.esy.es";
        String GET = IP + "/obtener_markers.php";
        ObtenerWebService fede = new ObtenerWebService();

        fede.execute(GET,"1");


        // Perform any camera updates here
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
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
                    googleMap.addMarker(new MarkerOptions().position(pos).title(name).snippet(adress));


                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
     //       Toast.makeText(getBaseContext(), "Actualizado", Toast.LENGTH_SHORT).show();



        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
       //     Toast.makeText(getBaseContext(),"texto onCancelled", Toast.LENGTH_SHORT).show();
        }
    }








}