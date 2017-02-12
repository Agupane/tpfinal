package com.example.agustin.tpfinal.VistasAndControllers;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.TimeUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.agustin.tpfinal.Dao.EstacionamientoDAO;
import com.example.agustin.tpfinal.Dao.JsonDBHelper;
import com.example.agustin.tpfinal.Dao.UbicacionVehiculoEstacionadoDAO;
import com.example.agustin.tpfinal.Exceptions.EstacionamientoException;
import com.example.agustin.tpfinal.Exceptions.UbicacionVehiculoException;
import com.example.agustin.tpfinal.Modelo.Estacionamiento;
import com.example.agustin.tpfinal.Modelo.UbicacionVehiculoEstacionado;
import com.example.agustin.tpfinal.R;
import com.example.agustin.tpfinal.Utils.AddressResultReceiver;
import com.example.agustin.tpfinal.Utils.AlarmEstacionamientoReceiver;
import com.example.agustin.tpfinal.Utils.ConstantsAddresses;
import com.example.agustin.tpfinal.Utils.ConstantsEstacionamientoService;
import com.example.agustin.tpfinal.Utils.ConstantsMenuNavegacion;
import com.example.agustin.tpfinal.Utils.ConstantsNotificaciones;
import com.example.agustin.tpfinal.Utils.FetchAddressIntentService;
import com.example.agustin.tpfinal.Utils.GeofenceTransitionsIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
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
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapaActivity extends AppCompatActivity implements TimePicker.OnTimeChangedListener, NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, AddressResultReceiver.Receiver,ResultCallback, View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
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
    /** Tag usado por el LOG  representando al menu   */
    private static final String TAG_MENU = "Menu_Navigation";
    /** Dao que almacena ubicacion de vehiculos estacionados */
    private static final UbicacionVehiculoEstacionadoDAO ubicacionVehiculoDAO = UbicacionVehiculoEstacionadoDAO.getInstance();
    /** Dao que almacena ubicacion de Estacionamientos */
    private static final EstacionamientoDAO estacionamientoDAO = EstacionamientoDAO.getInstance();
    /** Helper que administra la base de datos JSON LOCAL */
    private final JsonDBHelper jsonDbHelper = JsonDBHelper.getInstance();
    /** Representa el id del usuario que esta utilizando la aplicacion actualmente, TODO - hacer que la app obtenga el id en onCreate() */
    private static Integer ID_USUARIO_ACTUAL = 0;
    /** Pending intent que representa la notificacion que se genera debido a los marcadores */
    private PendingIntent geofencePendingIntent;
    /** Lista de todas las geofences creadas */
    private List mGeofenceList;
    /** Booleano que indica si se guardó la ubicación donde se estacionó */
    private boolean lugarEstacionamientoGuardado = false;
    /** Boton que permite switchear entre la lista de lugares y el mapa */
    FloatingActionButton fab;
    /** Booleano que sirve para identificar si pulsó dos veces para salir */
    boolean doubleBackToExitPressedOnce = false;
    /** Lista de Estacionamientos para marcar en el mapa */
    private Estacionamiento[] Estacionamientos;
    /** Bandera que determina si se pidieron o no permisos */
    private Boolean flagPermisoPedido = false;
    /** Codigo de request de permiso para cargar el mapa*/
    private static final int PERMISO_FINE_LOCATION_PARA_MAPA_READY = 1;
    /** Codigo de request de permiso para conectarse al servicio de localizacion */
    private static final int PERMISO_FINE_LOCATION_PARA_LOCATION_SERVICE = 2;
    /** Codigo de request de permiso para crear geofences */
    private static final int PERMISO_FINE_LOCATION_PARA_GEOFENCING = 3;
    /** Intent auxiliar que se utiliza al cargar el mapa */
    private Intent intentAuxMapa;
    /** Lista de marcadores en el mapa */
    public static Map<String,Marker> mapaMarcadores;
    /** Instancia actual usada por el alarm receiver */
    public static MapaActivity mapaActivityInstance;
    /** Objeto que representa el layout de navegation lateral */
    private static NavigationView navigationView;
    /** Objeto que representa el menu de navegacion lateral */
    private static Menu menuLateral;
    /** Objeto que representa el cuadro de dialogo que brinda informacion en caso de que el vehiculo se encuentre estacionado X Tiempo */
    private static AlertDialog dialogInfoVehiculoEstacionado;
    private int pickerHour = 0;
    private int pickerMin = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menuLateral = navigationView.getMenu();
        menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_LIMPIAR).setEnabled(false);
        menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_ALARMA).setEnabled(false);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        mapaMarcadores = new HashMap<>();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGeofenceList = new ArrayList<>();
        jsonDbHelper.setContext(this); // Le doy el contexto al json helper e instancia la bd
        mGoogleApiClient.connect();
        mResultReceiver = new AddressResultReceiver(new Handler());
        mResultReceiver.setReceiver(this);
        mapaActivityInstance = this;

        //Aca hago el primer try para intentar leer el archivo que tiene la lista de estacionamientos
        //Si no existe, lo creo, lo lleno (harcodeado..!) y lo leo
        try {
            Estacionamientos = estacionamientoDAO.llenarEstacionamientos(this);
        } catch (EstacionamientoException e) {
            //TODO manejar la excepcion
            String msgLog = "No se pudo leer el archivo";
            Log.v(TAG,msgLog);
            try {
                estacionamientoDAO.inicializarListaEstacionamientos(this);
                Estacionamientos = estacionamientoDAO.llenarEstacionamientos(this);
            } catch (EstacionamientoException e1) {
                msgLog = "Hubo un error al crear el archivo con la lista de Estacionamientos.";
                Log.v(TAG,msgLog);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        String estacionarAqui = getResources().getString(R.string.menuOptEstacionarAqui);
        String dondeEstacione = getResources().getString(R.string.menuOptDondeEstacione);
        switch (id) {
            case R.id.nav_estac: {
                /** Se ejecuta si el vehiculo no esta estacionado */
                if(item.getTitle().equals(estacionarAqui) && lugarEstacionamientoGuardado == false){
                    Log.v(TAG_MENU,"Estacionando en ubicacion actual");
                    estacionarEnPosicionActual();
                    this.lugarEstacionamientoGuardado = true;
                    item.setTitle(dondeEstacione);
                    menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_LIMPIAR).setEnabled(true);
                    menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_ALARMA).setEnabled(true);
                }
                else {
                    /** Se ejecuta si el vehiculo se encuentra actualmente estacionado */
                    if(item.getTitle().equals(dondeEstacione) && lugarEstacionamientoGuardado == true)
                    Log.v(TAG_MENU,"Recordando ubicación guardada");
                    //Creo una Location auxiliar con las coordenadas de la ubicacion guardada y enfoco el mapa ahi
                    Location lugarEstacionado = new Location(ubicacionActual);
                    lugarEstacionado.setLatitude(estCalle.getCoordenadas().latitude);
                    lugarEstacionado.setLongitude(estCalle.getCoordenadas().longitude);
                    enfocarMapaEnUbicacion(lugarEstacionado);
                }
                break;
            }
            case R.id.nav_clean: {
                if(this.lugarEstacionamientoGuardado == true) {
                    Log.v(TAG_MENU, "Borrando ubicación guardada");
                    this.marcarSalidaEstacionamiento(markerUltimoEstacionamiento);
                    markerUltimoEstacionamiento=null;
                }
                break;
            }
            case R.id.nav_alarma:{
                /** TODO - IMPLEMENTAR FUNCIONAMIENTO DEL BOTON DE ALARMA */
                if(markerUltimoEstacionamiento != null){
                    this.setearTimer(this);
                }
                break;
            }
            default: {
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mapa = googleMap;
        this.intentAuxMapa = getIntent();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            pedirPermisosUbicacion(PERMISO_FINE_LOCATION_PARA_MAPA_READY);
        }
        else{
            cargarMapa();
        }

    }

    /**
     * Accion que se ejecuta luego de que el mapa de google esta listo, se extrae en este metodo
     * debido a que el codigo se repite si es necesario pedir permisos
==
     */
    private void cargarMapa(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapa.setPadding(0,80,0,0);
            mapa.setMyLocationEnabled(true);
            mapa.setOnInfoWindowClickListener(this);
            mapa.setInfoWindowAdapter(ventanaInfo);
            marcarEstacionamientos();
            estCalle=cargarUltimoEstacionamiento(ID_USUARIO_ACTUAL);
            /** TODO refactorizar: si viene desde un intent (boton Ver Mapa en el Listado...) enfoca en ese punto */
            String intentExtraBandera = intentAuxMapa.getStringExtra("bandera");
            if(intentExtraBandera!=null && intentExtraBandera.equals("VER")){
                Bundle bundle = getIntent().getParcelableExtra("bundle");
                LatLng posicion = bundle.getParcelable("latlong");
                Location aux = new Location(ubicacionActual);
                aux.setLatitude(posicion.latitude);
                aux.setLongitude(posicion.longitude);

                //Como la funcion buscar podria devolver null, hago un if para prevenir
                Marker auxMarker = buscarMarker(posicion);
                if(auxMarker!=null){
                    auxMarker.showInfoWindow();

                } else {
                    addMarker(posicion,"Estacionamiento",R.drawable.marker_estacionamiento);
                }
                enfocarMapaEnUbicacion(aux);
            }
            else enfocarMapaEnUbicacion(ubicacionActual);
        }
    }

    /**
     *  Busca un Marker por su posicion dentro del Map-mapaMarcadores
     * @param posicion
     * @return retorna el Marker o Null, si no lo encuentra
     */
    private Marker buscarMarker(LatLng posicion){
            for(Marker aux : mapaMarcadores.values()) {
                if (aux.getPosition().equals(posicion)) {
                    return aux;
                }
            }
            return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        /** Pido permisos de ubicacion */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            pedirPermisosUbicacion(PERMISO_FINE_LOCATION_PARA_LOCATION_SERVICE);
        }
        /** Tengo permisos */
        else{
            conectarALocationServices();
        }
    }

    /**
     *  Se conecta con location services de google luego de haber solicitado permisos
     */
    private void conectarALocationServices(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            ubicacionActual = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            if (ubicacionActual != null) {
                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    Log.v(TAG, getResources().getString(R.string.no_geocoder_available));
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
     * Aunque ya existe el addMarker, esto me parece lo hace mas rapido mas legible y lo agrega a una lista
     *
     * @param latLng Posicion
     * @param title  Titulo
     * @param idIcon id del icono que va a tener
     */
    private void addMarker(LatLng latLng, String title, int idIcon) {
        Marker marker = mapa.addMarker(new MarkerOptions()
                .position(latLng) //Pongo el lugar
                .title(title));//Le meto titulo
        marker.setIcon(BitmapDescriptorFactory.fromResource(idIcon));
        marker.setVisible(true);
        mapaMarcadores.put(marker.getId(),marker);
    }
    /**
     * Agrega un marcador asociado al estacionamiento de una persona (la ubicacion donde estaciono)
     * A su vez agrega una geofence asociada al marcador
     * @param estacionamiento objeto que almacena la informacion acerca de donde realizo el estacionamiento el vehiculo
     * @return marcador que se agrego
     */
    public Marker agregarMarcadorEstacionamiento(UbicacionVehiculoEstacionado estacionamiento) {
        Marker marker = mapa.addMarker(new MarkerOptions()
                .position(estacionamiento.getCoordenadas())
                .title(estacionamiento.getTitulo()));

        marker.setIcon(BitmapDescriptorFactory.defaultMarker(ConstantsEstacionamientoService.MARCADOR_ESTACIONAMIENTO_CALLE));
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(estacionamiento.getCoordenadas(), 15));
        /* Agrego el objeto estacionamiento al marcador */
        marker.setTag(estacionamiento);

         /* Objeto que permite generar eventos de aproximacion al radio del marcador */
        Geofence.Builder geof = new Geofence.Builder();
        geof.setRequestId(marker.getId())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(ConstantsEstacionamientoService.GEOFENCE_STAY_TO_NOTIFICATION_TIME)
                .setExpirationDuration(ConstantsEstacionamientoService.GEOFENCE_EXPIRATION_DURATION_TIME)
                .setCircularRegion(marker.getPosition().latitude, marker.getPosition().longitude, ConstantsEstacionamientoService.GEOFENCE_RADIUS_IN_METERS)
                ;
        mGeofenceList.add(geof.build());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            pedirPermisosUbicacion(PERMISO_FINE_LOCATION_PARA_GEOFENCING);
        }
        else
        {
            agregarGeofence();
        }
        mapaMarcadores.put(marker.getId(),marker);
        return marker;
    }

    /** Una vez recibido permisos de ubicacion, agrega las geofences creadas al mapa */
    private void agregarGeofence(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        }
    }

    /**
     * Elimina una alarma al objeto estacionamiento
     * @param markerEstacionamiento
     */
    private void agregarAlarma(Marker markerEstacionamiento){
        UbicacionVehiculoEstacionado ubicacionVehiculo = (UbicacionVehiculoEstacionado) markerEstacionamiento.getTag();
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmEstacionamientoReceiver.class);
        intent.putExtra("idMarcador",markerEstacionamiento.getId());
        intent.setAction(String.valueOf(ConstantsNotificaciones.ACCION_GENERAR_ALARMA));
        Integer idPendingIntent = ubicacionVehiculo.getId();
        PendingIntent pi = PendingIntent.getBroadcast(this,idPendingIntent,intent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+ConstantsNotificaciones.TIEMPO_CONFIGURADO_ALARMA,pi);
    }

    /**
     * Asocia una alarma al objeto estacionamiento y la inicializa
     * Permite al usuario recordar que dejo el auto estacionado mediante una notificacion cada X Tiempo
     * @param markerEstacionamiento
     */
    private void eliminarAlarma(Marker markerEstacionamiento){
        UbicacionVehiculoEstacionado ubicacionVehiculo = (UbicacionVehiculoEstacionado) markerEstacionamiento.getTag();
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmEstacionamientoReceiver.class);
      //  intent.putExtra("idMarcador",markerEstacionamiento.getId());
      //  intent.setAction(String.valueOf(ConstantsNotificaciones.ACCION_GENERAR_ALARMA));
        Integer idPendingIntent = ubicacionVehiculo.getId();
        PendingIntent pi = PendingIntent.getBroadcast(this,idPendingIntent,intent,0);
        alarmManager.cancel(pi);
    }
    @Override
    /**
     * Evento que aparece cuando se hace clik sobre el info windows de un marcador
     */
    public void onInfoWindowClick(Marker marker) {
        marcadorSelected = marker;
        final String msgSalidaEstacionamiento = getResources().getString(R.string.btnMarcarSalida);
        final String msgEstacionarAqui = getResources().getString(R.string.menuOptEstacionarAqui);
        String msgNavegar = getResources().getString(R.string.btnAbrirEnNavigator);
        String msgCancelar = getResources().getString(R.string.btnCancelar);
        String msgTituloDialog = getResources().getString(R.string.menuDialogTitulo);
        /*
        // Crear un buildery vincularlo a la actividad que lo mostrará
        LayoutInflater linf = LayoutInflater.from(this);
        final View inflator = linf.inflate(R.layout.alert_distancia_busqueda, null);
        AlertDialog.Builder builder= new AlertDialog.Builder(this);

            builder.setPositiveButton( /** Listener de la opcion de navegar */
              /*      msgNavegar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            abrirNavigatorEnDestino(marcadorSelected);
                            dialog.dismiss();
                        }
                    }
            ).setNeutralButton( /** Listener de la opcion de marcar salida del estacionamiento */
                /*    msgSalidaEstacionamiento, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            marcarSalidaEstacionamiento(marcadorSelected);
                        }
                    }
            ).setNegativeButton( /** Listener de la opcion de cancelar */
                  /*  msgCancelar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
            );*/
        final Dialog dialogTest = new Dialog(this); // Context, this, etc.
        dialogTest.setContentView(R.layout.custom_info_window_estacionamiento);
        dialogTest.setTitle(msgTituloDialog);
        dialogTest.setCancelable(true);
        dialogTest.show();

        Button btnAbrirNavigator = (Button) dialogTest.findViewById(R.id.btnNavegar);
        final Button btnSalidaEntrada = (Button) dialogTest.findViewById(R.id.btnSalir_EntrarEstacionamiento);
        Button btnCancelar = (Button) dialogTest.findViewById(R.id.btnCancelar);
        btnAbrirNavigator.setText(msgNavegar);
        if(lugarEstacionamientoGuardado == false){
            btnSalidaEntrada.setText(msgEstacionarAqui);
        }
        else{
            btnSalidaEntrada.setText(msgSalidaEstacionamiento);
        }
        btnCancelar.setText(msgCancelar);
        /** Listener de la opcion de navegar */
        btnAbrirNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirNavigatorEnDestino(marcadorSelected);
                dialogTest.dismiss();
            }
        });
        /** Listener de la opcion de marcar salida del estacionamiento */
        btnSalidaEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Se ejecuta si el vehiculo no esta estacionado */
                if(btnSalidaEntrada.getText().equals(msgEstacionarAqui) && lugarEstacionamientoGuardado == false){
                    Log.v(TAG_MENU,"Estacionando en ubicacion actual");
                    estacionarEnPosicionActual();
                    lugarEstacionamientoGuardado = true;
                    btnSalidaEntrada.setText(msgSalidaEstacionamiento);
                    menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_ESTACIONAR_AQUI).setTitle(msgSalidaEstacionamiento);
                    menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_LIMPIAR).setEnabled(true);
                    menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_ALARMA).setEnabled(true);
                    dialogTest.dismiss();
                }
                else {
                    /** Se ejecuta si el vehiculo se encuentra actualmente estacionado */
                    if(btnSalidaEntrada.getText().equals(msgSalidaEstacionamiento) && lugarEstacionamientoGuardado == true) {
                        Log.v(TAG_MENU, "Borrando ubicacion guardada");
                        //Creo una Location auxiliar con las coordenadas de la ubicacion guardada y enfoco el mapa ahi
                        Location lugarEstacionado = new Location(ubicacionActual);
                        lugarEstacionado.setLatitude(estCalle.getCoordenadas().latitude);
                        lugarEstacionado.setLongitude(estCalle.getCoordenadas().longitude);
                        enfocarMapaEnUbicacion(lugarEstacionado);
                        btnSalidaEntrada.setText(msgEstacionarAqui);
                        menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_ESTACIONAR_AQUI).setTitle(msgEstacionarAqui);
                        menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_LIMPIAR).setEnabled(false);
                        menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_ALARMA).setEnabled(false);
                        dialogTest.dismiss();
                    }
                }
                marcarSalidaEstacionamiento(marcadorSelected);
            }
        });
        /** Listener de la opcion de cancelar */
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTest.dismiss();
            }
        });

        //AlertDialog dialog= builder.create();
        //Mostrarlo
        //dialog.show();
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
            msg = getResources().getString(R.string.parkLoggerEstacionamientoExitoso);
            Toast.makeText(this,msg,Toast.LENGTH_LONG);
            markerUltimoEstacionamiento = agregarMarcadorEstacionamiento(estCalle);
            /* Agrego el marcador a la lista de marcadores */
            mapaMarcadores.put(markerUltimoEstacionamiento.getId(),markerUltimoEstacionamiento);
            /* Agrego la alarma que se asocia al estacionamiento del usuario */
            agregarAlarma(markerUltimoEstacionamiento);
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
            msg = getResources().getString(R.string.errorProducidoIntenteNuevamente);
            Toast.makeText(this,msg,Toast.LENGTH_SHORT);
           // e.printStackTrace();
        }
    }

    /**
     * Actualiza la informacion en disco del objeto ubicacion vehiculo
     * @param estCalle
     */
    private void actualizarUbicacionPersistida(UbicacionVehiculoEstacionado estCalle) throws UbicacionVehiculoException {
        String msg = getResources().getString(R.string.parkLoggerInicioActualizacionUbicacion);
        Log.v(TAG,msg);
        ubicacionVehiculoDAO.actualizarUbicacionVehiculoEstacionado(estCalle,this);
    }

    /**
     * Recibe una ubicacion y enfoca el mapa en ese punto
     */
    private void enfocarMapaEnUbicacion(Location location){
        String msg;
        if(location!=null){
            msg = getResources().getString(R.string.ubicacionActualEncontrada);
            Log.v(TAG,msg);
           /* LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition position = this.mapa.getCameraPosition();
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(15);
            builder.target(target);
            this.mapa.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));*/
            String msgToast = location.getLatitude() + ", " + location.getLongitude();

            //Muevo la camara
            mapa.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));

            //Le doy zoom
            mapa.animateCamera(CameraUpdateFactory.zoomTo(17f));
            //TODO  Sacar el toast en un futuro
            Toast.makeText(this, msgToast, Toast.LENGTH_LONG).show();
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
            try {
                actualizarUbicacionPersistida(estCalle);
            }
            catch (UbicacionVehiculoException e){
                errorMessage = e.getMessage();
                Log.v(TAG,errorMessage);
                errorMessage = getResources().getString(R.string.errorProducidoIntenteNuevamente);
                Toast.makeText(this,errorMessage,Toast.LENGTH_LONG);
            }
        }
        /** Si el resultado no es exitoso, espero recibir el mensaje de error en forma de string */
        else{
            errorMessage = resultData.getString(ConstantsAddresses.RESULT_DATA_KEY);
            Log.v(TAG,errorMessage);
        }
    }

    /** Permite cargar el ultimo estacionamiento en el mapa del usuario */
    public UbicacionVehiculoEstacionado cargarUltimoEstacionamiento(int idUsuario){
        UbicacionVehiculoEstacionado ultimoEst = ubicacionVehiculoDAO.getUltimaUbicacionVehiculo(idUsuario,this);
        String msg;
        if(ultimoEst !=null) {
            /* Si no hay hora de egreso es porque no se produjo, y por lo tanto lo agrego como ubicacion del vehiculo */
            if(ultimoEst.getHoraEgreso() == null ){
                /* TODO - una implementacion mejor tendria en cuenta otra condicion para el if: que el tiempo actual vs el tiempo
                de ingreso sea chico, si paso mucho tiempo significa que se olvido de marcar el egreso y por lo tanto habria
                que marcar el egreso y no poner el marcador
                */
                markerUltimoEstacionamiento = agregarMarcadorEstacionamiento(ultimoEst);
               // agregarAlarma(markerUltimoEstacionamiento);
                generarVentanaRecordatorioEstacionamiento(markerUltimoEstacionamiento);
                this.lugarEstacionamientoGuardado = true;
                msg = getResources().getString(R.string.menuOptDondeEstacione);
                menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_ESTACIONAR_AQUI).setTitle(msg);
                menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_ESTACIONAR_AQUI).setChecked(true);
            }
        }
        return ultimoEst;
    }

    /**
     * Genera una ventana de informacion alertando de que existia un vehiculo previamente estacionado cuando se abrio la aplicacion
     * @param markerUltimoEstacionamiento
     */
    private void generarVentanaRecordatorioEstacionamiento(final Marker markerUltimoEstacionamiento){
        final UbicacionVehiculoEstacionado ubicacionEstacionamiento = (UbicacionVehiculoEstacionado) markerUltimoEstacionamiento.getTag();
        String titulo = this.getResources().getString(R.string.notificacionAlarmaEstTitulo);
        String tiempoDeIngreso;
        StringBuilder texto = new StringBuilder();
        texto.append("Su vehiculo se encuentra estacionado desde las ");
        Date date = new Date(ubicacionEstacionamiento.getHoraIngreso());
        tiempoDeIngreso = new SimpleDateFormat("HH:mm").format(date);
        texto.append(tiempoDeIngreso);
        texto.append(" , desea marcar la salida del estacionamiento?");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(texto)
        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String estacionarAqui = getResources().getString(R.string.menuOptEstacionarAqui);
                marcarSalidaEstacionamiento(markerUltimoEstacionamiento);
                menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_ESTACIONAR_AQUI).setTitle(estacionarAqui);
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                agregarAlarma(markerUltimoEstacionamiento);
            }
        })
        .setNeutralButton("Ir al estacionamiento", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Location lugarEstacionado = new Location(ubicacionActual);
                lugarEstacionado.setLatitude(ubicacionEstacionamiento.getCoordenadas().latitude);
                lugarEstacionado.setLongitude(ubicacionEstacionamiento.getCoordenadas().longitude);
                enfocarMapaEnUbicacion(lugarEstacionado);
            }
        });

        dialogInfoVehiculoEstacionado = builder.create();
        dialogInfoVehiculoEstacionado.show();
    }

    /**
     * Marca la salida del estacionamiento y elimina el marcador
     * @param marcadorSalida
     */
    public void marcarSalidaEstacionamiento(Marker marcadorSalida){
        String msg = getResources().getString(R.string.parkLoggerInicioSalidaEstacionamiento);
        Log.v(TAG,msg);
        String estacionarAqui = getResources().getString(R.string.menuOptEstacionarAqui);
        String dondeEstacione = getResources().getString(R.string.menuOptDondeEstacione);
        UbicacionVehiculoEstacionado ubicacionVehiculo = (UbicacionVehiculoEstacionado) marcadorSalida.getTag();
        ubicacionVehiculo.setHoraEgreso(System.currentTimeMillis());
        try{
            /* Elimino la alarma que se asocia al estacionamiento del usuario */
            eliminarAlarma(markerUltimoEstacionamiento);
            actualizarUbicacionPersistida(ubicacionVehiculo);
            marcadorSalida.remove();
        }
        catch (UbicacionVehiculoException e) {
            msg = getResources().getString(R.string.errorProducidoIntenteNuevamente);
            Toast.makeText(this, msg, Toast.LENGTH_LONG);
        }
        markerUltimoEstacionamiento = null;
        estCalle = null;
        this.lugarEstacionamientoGuardado = false;
        (menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_ESTACIONAR_AQUI)).setTitle(estacionarAqui);
        menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_LIMPIAR).setEnabled(false);
        menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_ALARMA).setEnabled(false);
    }

    /**
     * Abre el navigator hacia las coordenadas del navegador destino
     * @param marcadorDestino
     */
    private void abrirNavigatorEnDestino(Marker marcadorDestino){
        String msg = getResources().getString(R.string.parkLoggerInicioNavegacion);
        Log.v(TAG,msg);
        Double latitude = marcadorDestino.getPosition().latitude;
        Double longitude = marcadorDestino.getPosition().longitude;
        Intent navigation = new Intent(Intent.ACTION_VIEW, Uri
                .parse("http://maps.google.com/maps?saddr="
                        + "&daddr="
                        + latitude + "," + longitude));
        startActivity(navigation);
    }

    /**
     * Devuelve un pending intent relacionado a los geofences de los marcadores que permite realizar una notificacion
     * @return
     */
    private PendingIntent getGeofencePendingIntent(){
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    /**
     * Genera una geofence la cual se debe asociar a un marcador de estacionamiento
     * @return
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }
    /*
     * Pide permisos para acceder a la ubicacion mediante un mensaje de usuario
     */
    private void pedirPermisosUbicacion(final Integer idSolicitantePermiso){
        String msg;
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            msg = getResources().getString(R.string.tituloSolicitudPermisos);
            builder.setTitle(msg);
            builder.setPositiveButton(android.R.string.ok, null);
            msg = getResources().getString(R.string.mensajeSolicitudPermisos);
            builder.setMessage(msg);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onDismiss(DialogInterface dialog) {
                    flagPermisoPedido=true;
                    requestPermissions(
                            new String[]
                                    {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}
                            , idSolicitantePermiso);
                }
            });
            builder.show();
        }
        else {
            flagPermisoPedido=true;
            ActivityCompat.requestPermissions(this,
                    new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}
                    , idSolicitantePermiso);
        }
    }

    @Override
    public void onResult(@NonNull Result result) {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == fab.getId()) {
            Intent intent = new Intent(this, ListarLugaresActivity.class);
            intent.putExtra("ubicacionActual", ubicacionActual);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        String msg;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(Gravity.LEFT)){
            drawer.closeDrawer(Gravity.LEFT);
        }
        else{
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }
            else if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                msg = getResources().getString(R.string.presionarAtrasParaSalir);
                Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
            else {
                super.onBackPressed();
                return;
            }
        }
    }

    private void marcarEstacionamientos(){
        /*mapa.addMarker((new MarkerOptions()
                .position(Estacionamientos[0].getPosicionEstacionamiento()) //Pongo el lugar
                .title(Estacionamientos[0].getNombreEstacionamiento())) //Le meto titulo
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_estacionamiento)));
        addMarker(Estacionamientos[0].getPosicionEstacionamiento(),Estacionamientos[0].getNombreEstacionamiento(),R.drawable.marker_estacionamiento);*/
        for(int i=0; i<Estacionamientos.length;i++){
            addMarker(Estacionamientos[i].getPosicionEstacionamiento(),(Estacionamientos[i].getNombreEstacionamiento()).substring(8),R.drawable.marker_estacionamiento);
        }
    }

    @Override
    /** Aca se devuelve el resultado si el usuario decidio dar o no los permisos */
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        String msg;
        switch (requestCode) {
            case PERMISO_FINE_LOCATION_PARA_MAPA_READY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    msg = getResources().getString(R.string.permissionLocationObtenido);
                    Log.v(TAG,msg);
                    cargarMapa();
                }
                else {
                    msg = getResources().getString(R.string.permissionLocationNoObtenido);
                    Log.v(TAG,msg);
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case PERMISO_FINE_LOCATION_PARA_LOCATION_SERVICE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    msg = getResources().getString(R.string.permissionLocationObtenido);
                    Log.v(TAG,msg);
                    conectarALocationServices();
                }
                else {
                    msg = getResources().getString(R.string.permissionLocationNoObtenido);
                    Log.v(TAG,msg);
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case PERMISO_FINE_LOCATION_PARA_GEOFENCING:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    msg = getResources().getString(R.string.permissionLocationObtenido);
                    Log.v(TAG,msg);
                    agregarGeofence();
                }
                else {
                    msg = getResources().getString(R.string.permissionLocationNoObtenido);
                    Log.v(TAG,msg);
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void setearTimer(final Context context) {

        String msgCancelar = getResources().getString(R.string.btnCancelar);
        String msgGuardar = getResources().getString(R.string.btnDialogSetearAlarma);
        String msgTituloDialog = "Temporizador para alarma";

        final Dialog dialogTest = new Dialog(context); // Context, this, etc.
        dialogTest.setContentView(R.layout.custom_alarm_timer_window);
        dialogTest.setTitle(msgTituloDialog);
        dialogTest.setCancelable(true);
        dialogTest.show();

        Button btnSetearTimer = (Button) dialogTest.findViewById(R.id.btnSetearTimer);
        Button btnCancelar = (Button) dialogTest.findViewById(R.id.btnDialogCancel);
        btnSetearTimer.setText(msgGuardar);
        btnCancelar.setText(msgCancelar);

        TimePicker timePicker = (TimePicker) dialogTest.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener((TimePicker.OnTimeChangedListener) context);
        //TODO implementar todos los métodos TimePicker! (validaciones, etc)

        /** Listener de la opcion de setear el timer de la alarma */
        btnSetearTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO setear alarma/timer
                dialogTest.dismiss();
            }
        });

        /** Listener de la opcion de cancelar */
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTest.dismiss();
            }
        });


    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        pickerHour = hourOfDay;
        pickerMin = minute;
    }
}
