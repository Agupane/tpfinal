package com.example.agustin.tpfinal;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.agustin.tpfinal.Dao.JsonDBHelper;
import com.example.agustin.tpfinal.Dao.UbicacionVehiculoEstacionadoDAO;
import com.example.agustin.tpfinal.Exceptions.UbicacionVehiculoException;
import com.example.agustin.tpfinal.Modelo.UbicacionVehiculoEstacionado;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, AddressResultReceiver.Receiver {
    /** Mapa de google a mostrar */
    private GoogleMap mapa;
    /** Cliente de api de google para utilizar el servicio de localizacion */
    private GoogleApiClient mGoogleApiClient;
    /** Ultima ubicacion en donde se encuentra la persona */
    private Location ubicacionActual;
    /** Adaptador que permite cargar con datos la ventana de informacion de los marcadores */
    private InfoWindowsAdapter ventanaInfo;
    /** Ubicacion del vehiculo cuando estaciona en la calle */
    private UbicacionVehiculoEstacionado estCalle;
    /** Marcador que indica el ultimo lugar donde la persona realizo un estacionamiento */
    private Marker markerUltimoEstacionamiento;
    /** Marcador que indica el ultimo marcador que fue seleccionado por el usuario */
    private Marker marcadorSelected;
    /** Clase que recibe de manera asincronica resultados del servicio de calles y envia los mismos a esta actividad */
    private static AddressResultReceiver mResultReceiver;
    /** Indica si se solicito obtener una direccion o no */
    private Boolean mAddressRequested = false;
    /** Servicio que permite obtener la direccion acorde a una ubicacion pasada */
    private FetchAddressIntentService buscarCallesService;
    /** Tag usado por el LOG    */
    private static final String TAG = "ServicioUbicacion";
    /** Dao que almacena ubicacion de vehiculos estacionados */
    private static final UbicacionVehiculoEstacionadoDAO ubicacionVehiculoDAO = UbicacionVehiculoEstacionadoDAO.getInstance();
    /** Helper que administra la base de datos JSON LOCAL */
    private final JsonDBHelper jsonDbHelper = JsonDBHelper.getInstance();
    /** Representa el id del usuario que esta utilizando la aplicacion actualmente, TODO - hacer que la app obtenga el id en onCreate() */
    private static Integer ID_USUARIO_ACTUAL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        jsonDbHelper.setContext(this); // Le doy el contexto al json helper e instancia la bd
        mGoogleApiClient.connect();
        mResultReceiver = new AddressResultReceiver(new Handler());
        mResultReceiver.setReceiver(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mapa, menu);
        return true;
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume(){
        mResultReceiver.setReceiver(this);
        super.onResume();
    }

    @Override
    public void onPause(){
        mResultReceiver.setReceiver(null);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_estacionar: {
                Log.v("OPCIONES_USUARIO: ","Estacionando en ubicacion actual");
                estacionarEnPosicionActual();
                break;
            }
            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMyLocationEnabled(true);
        mapa.setOnMapLongClickListener(this);
        mapa.setOnInfoWindowClickListener(this);
        mapa.setInfoWindowAdapter(ventanaInfo);
        enfocarMapaEnUbicacion(ubicacionActual);
        cargarUltimoEstacionamiento(ID_USUARIO_ACTUAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //  return;
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        /*
        Intent i = new Intent(MapaActivity.this, AltaReclamoActivity.class);
        i.putExtra("coordenadas",latLng);
        ubicacion=latLng;
        startActivityForResult(i, CODIGO_RESULTADO_ALTA_RECLAMO);
        */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        switch(resultCode){
            case Activity.RESULT_OK:{
                Bundle extras = data.getExtras();
                nuevoReclamo = (Reclamo) extras.get("reclamo");
                agregarMarcador(nuevoReclamo);
                break;
            }
            case Activity.RESULT_CANCELED:{
                break;
            }
        }
        */
    }

    @Override
    /** Se conecta con location services de google */
    public void onConnected(@Nullable Bundle bundle) {
        /** Pido permisos de ubicacion */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.v("PERMISOS ","No hay permisos para obtener la ubicacion actual");
        }
        /** Tengo permisos */
        else{
            Log.v("PERMISOS","Permisos para obtener ubicacion concedidos, obteniendo ubicacion y generando mapa...");
            ubicacionActual = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            if (ubicacionActual != null) {
                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    Log.v("LOCALIZADOR", getResources().getString(R.string.no_geocoder_available));
                }
                if (mAddressRequested) {
                    startAddressFetchService();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /**
     * Inicializa el servicio que se encarga de buscar direcciones dada una ubicacion
     */
    private void startAddressFetchService(){
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(ConstantsAddresses.RECEIVER, mResultReceiver);
        intent.putExtra(ConstantsAddresses.LOCATION_DATA_EXTRA, ubicacionActual);
        startService(intent);
    }

    /**
     * Agrega un marcador asociado al estacionamiento de una persona (la ubicacion donde estaciono)
     * @param estacionamiento objeto que almacena la informacion acerca de donde realizo el estacionamiento el vehiculo
     * @return marcador que se agrego
     */
    public Marker agregarMarcadorEstacionamiento(UbicacionVehiculoEstacionado estacionamiento) {
        Marker marker = mapa.addMarker(new MarkerOptions()
                .position(estacionamiento.getCoordenadas())
                .title(estacionamiento.getTitulo()));

        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(estacionamiento.getCoordenadas(),15));
        /*
        if(!listaReclamos.containsKey(reclamo)){
            listaReclamos.put(marker,reclamo);
        }
        */
      //  ventanaInfo.setListaReclamos(listaReclamos);
        return marker;
    }
    /*
    public Marker agregarMarcador(LatLng latLng, String titulo, File foto) {
        Marker marker = mapa.addMarker(new MarkerOptions()
                .position(latLng)
                .title(titulo));

        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        marker.setTag(foto);

        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        marker.showInfoWindow();
        return marker;
    }
    */

    @Override
    public void onInfoWindowClick(Marker marker) {
        marcadorSelected = marker;
        System.out.println("asaaa");
        // Crear un buildery vincularlo a la actividad que lo mostrará
        LayoutInflater linf = LayoutInflater.from(this);
        final View inflator = linf.inflate(R.layout.alert_distancia_busqueda, null);
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        //Configurar las características
            builder.setView(inflator)
                    .setMessage("Desea marcar todos lo reclamos que esten a X Distancia?");
            final EditText etCantKm = (EditText) inflator.findViewById(R.id.etDistanciaReclamo);

            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String valorKm = etCantKm.getText().toString();
                            /*
                            cantKmReclamo = Integer.parseInt(valorKm);
                            cantMetrosReclamo = cantKmReclamo*1000;
                            listaReclamosCercanos = obtenerListaMarcadoresCercanos(marcadorSelected,cantMetrosReclamo);
                            unirReclamos(listaReclamosCercanos);
                            */
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        AlertDialog dialog= builder.create();
        //Mostrarlo
        System.out.println("clik dentro");
        dialog.show();
    }

    /**
     * Genera un marcador de estacionamiento en la posicion actual
     * Guarda la informacion del lugar de la calle donde estaciono
     */
    private void estacionarEnPosicionActual() {
        String msg;
        if (mGoogleApiClient.isConnected() && ubicacionActual != null) {
            estCalle = new UbicacionVehiculoEstacionado(ubicacionActual);
            estCalle.setHoraIngreso(System.currentTimeMillis());
            startAddressFetchService();
            mAddressRequested = false;
            /*
            LatLng latLngActual = new LatLng(ubicacionActual.getLatitude(),ubicacionActual.getLongitude());
            String titulo = getResources().getString(R.string.titMarcadorEstacionamiento);

            /** TODO -- Agregar foto del lugar de la calle obteniendolo de google */
           // markerUltimoEstacionamiento = agregarMarcador(latLngActual,titulo,null);
            /*                                                                     */
            markerUltimoEstacionamiento = agregarMarcadorEstacionamiento(estCalle);
            msg = getResources().getString(R.string.parkLoggerEstacionamientoCallejeroExitoso);
            Log.v(TAG,msg);
            persistirUbicacion(estCalle);
        }
        mAddressRequested = true;
    }

    /**
     * Guarda el ultimo lugar en donde se realizo el estacionamiento
     */
    private void persistirUbicacion(UbicacionVehiculoEstacionado ubicacionEstacionado){
        String msg = getResources().getString(R.string.parkLoggerInicioPersistiendoUbicacion);
        Log.v(TAG,msg);
        try {
            ubicacionVehiculoDAO.guardarOActualizarUbicacionVehiculo(ubicacionEstacionado,this);
        }
        catch (UbicacionVehiculoException e) {
            /** TODO IMPLEMENTAR TRATAMIENTO */
            Toast.makeText(this,"Error producido",Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la informacion en disco del objeto ubicacion vehiculo
     * @param estCalle
     */
    private void actualizarUbicacionPersistida(UbicacionVehiculoEstacionado estCalle) {
        String msg = getResources().getString(R.string.parkLoggerInicioActualizacionUbicacion);
        Log.v(TAG,msg);
        try {
            ubicacionVehiculoDAO.actualizarUbicacionVehiculoEstacionado(estCalle,this);
        }
        catch (UbicacionVehiculoException e) {
            /** TODO IMPLEMENTAR TRATAMIENTO */
            Toast.makeText(this,"Error producido",Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    /**
     * Recibe una ubicacion y enfoca el mapa en ese punto
     */
    private void enfocarMapaEnUbicacion(Location location){
        String msg;
        if(location!=null){
            msg = getResources().getString(R.string.ubicacionActualEncontrada);
            Log.v(TAG,msg);
            LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition position = this.mapa.getCameraPosition();
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(15);
            builder.target(target);
            this.mapa.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
        }
        else{
            msg = getResources().getString(R.string.ubicacionActualInexistente);
            Log.v(TAG,msg);
        }

    }

    /**
     * Metodo que permite recibir el resultado de la busqueda de direccion de calle asincronicamente
     * @param resultCode codigo del resultado
     * @param resultData informacion resultante
     */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        String errorMessage; // Msg de error obtenido en la busqueda
        Address direccion; // Direccion obtenida en la busqueda
        /** Si el resultado es exitoso, espero recibir la direccion en formado de address */
        if (resultCode == ConstantsAddresses.SUCCESS_RESULT) {
            direccion = resultData.getParcelable(ConstantsAddresses.RESULT_DATA_KEY);
            estCalle.setDireccion(direccion);
            markerUltimoEstacionamiento.setTitle(estCalle.getTitulo());
            actualizarUbicacionPersistida(estCalle);
        }
        /** Si el resultado no es exitoso, espero recibir el mensaje de error en forma de string */
        else{
            errorMessage = resultData.getString(ConstantsAddresses.RESULT_DATA_KEY);
            Log.v(TAG,errorMessage);
        }
    }

    /** Permite cargar el ultimo estacionamiento en el mapa del usuario */
    public void cargarUltimoEstacionamiento(int idUsuario){
        UbicacionVehiculoEstacionado ultimoEst = ubicacionVehiculoDAO.getUltimaUbicacionVehiculo(idUsuario,this);
        if(ultimoEst !=null) {
            agregarMarcadorEstacionamiento(ultimoEst);
        }
    }

}
