package com.treehacks.hopify.hopify

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.jakewharton.rxbinding2.view.clicks
import com.treehacks.hopify.hopify.model.MapsViewModel
import com.treehacks.hopify.hopify.server.DataParser
import com.treehacks.hopify.hopify.server.HopifyApiManager
import com.treehacks.hopify.hopify.server.HopifyOnboardingResponse
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotterknife.bindView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class MapsActivity :
        FragmentActivity(),
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private val shareButton by bindView<Button>(R.id.maps_share_button)
    private val listViewButton by bindView<Button>(R.id.maps_list_view_button)

    private lateinit var mMap: GoogleMap
    private val manager = HopifyApiManager()
    private lateinit var viewModel: MapsViewModel

    private lateinit var mLastLocation: Location
    private lateinit var mLocationRequest: LocationRequest
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mCurrLocationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Branch.getAutoInstance(this)

        setContentView(R.layout.activity_map)

        viewModel = MapsViewModel(intent.extras.get(MAPS_DATA) as HopifyOnboardingResponse)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()

        setupUiElements()
    }

    public override fun onNewIntent(intent: Intent) {
        this.intent = intent
    }

    override fun onResume() {
        super.onResume()
        Branch.getAutoInstance(this)
    }

    private fun setupUiElements() {
        shareButton.clicks().subscribe {
            launchContactsPicker()
        }
        listViewButton.clicks().subscribe {

        }
    }

    private fun launchContactsPicker() {
        val contactPickerIntent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(contactPickerIntent, REQUEST_CODE_PICK_CONTACT)
    }

    private fun createBranchIoLinkAndPost(phone: String) {
        val buo = BranchUniversalObject()
                .setTitle("My plan")
                .setContentDescription("Desc")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(ContentMetadata().addCustomMetadata("uuid", viewModel.id))

        val lp = LinkProperties()
                .setFeature("sharing")
                .setCampaign("City Crawler")
                .setStage("Sharing trip")
                .addControlParameter("\$uuid", viewModel.id)

        buo.generateShortUrl(this, lp, { url, error ->
            if (error == null) {
                manager.sendMessage(url, phone)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onError = { Log.i(LOG_TAG, it.message) },
                                onComplete = {},
                                onNext = { Log.i(LOG_TAG, it.string()) }
                        )
                Log.i("BRANCH SDK", "url: " + url)
            }
        })
    }

    private fun handleContactPicked(data: Intent) {
        val cursor: Cursor
        try {
            val uri = data.data
            cursor = contentResolver.query(uri, null, null, null, null)
            cursor.moveToFirst()

            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val phone = cursor.getString(phoneIndex)
            createBranchIoLinkAndPost(phone)

            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v(LOG_TAG, "here1")
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_CONTACT) {
                Log.v(LOG_TAG, "here")
                handleContactPicked(data!!)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))

        if (viewModel.data.isEmpty())
            return

        // Add a marker in Sydney, Australia, and move the camera.
        viewModel.data.forEachIndexed { index, item ->
            val point = LatLng(
                    item.geometry?.location?.lat ?: 0.toDouble(),
                    item.geometry?.location?.lng ?: 0.toDouble()
            )
            mMap.addMarker(MarkerOptions().position(point).title("${index + 1}. ${item.name
                    ?: ""}"))
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(
                viewModel.data[0].geometry?.location?.lat ?: 0.toDouble(),
                viewModel.data[0].geometry?.location?.lng ?: 0.toDouble()
        )))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL))
        (0..(viewModel.data.size - 2)).forEach {
            val pt1 = viewModel.data[it].geometry!!.location!!
            val pt2 = viewModel.data[it + 1].geometry!!.location!!
            val url = getUrl(LatLng(pt1.lat!!, pt1.lng!!), LatLng(pt2.lat!!, pt2.lng!!))
            val FetchUrl = FetchUrl()

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url)
            //move map camera
        }
    }

    private fun getUrl(origin: LatLng, dest: LatLng): String {

        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude

        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude


        // Sensor enabled
        val sensor = "sensor=false"

        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor"

        // Output format
        val output = "json"

        // Building the url to the web service


        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    /**
     * A method to download json data from url
     */
    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)

            // Creating an http connection to communicate with url
            urlConnection = url.openConnection() as HttpURLConnection

            // Connecting to url
            urlConnection.connect()

            // Reading data from url
            iStream = urlConnection.inputStream

            val br = BufferedReader(InputStreamReader(iStream!!))

            val sb = StringBuffer()

            var line = br.readLine()
            while (line != null) {
                sb.append(line)
                line = br.readLine()
            }

            data = sb.toString()
            br.close()

        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }

    // Fetches data from url passed
    private inner class FetchUrl : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg url: String): String {

            // For storing data from web service
            var data = ""

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0])
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }

            return data
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            val parserTask = ParserTask()

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result)

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private inner class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

        // Parsing the data in non-ui thread
        override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {

            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null

            try {
                jObject = JSONObject(jsonData[0])
                val parser = DataParser()

                // Starts parsing data
                routes = parser.parse(jObject)

            } catch (e: Exception) {
                Log.d("ParserTask", e.toString())
                e.printStackTrace()
            }

            return routes
        }

        // Executes in UI thread, after the parsing process
        override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
            var points: ArrayList<LatLng>
            var lineOptions: PolylineOptions? = null

            // Traversing through all the routes
            for (i in result.indices) {
                points = ArrayList()
                lineOptions = PolylineOptions()

                // Fetching i-th route
                val path = result[i]

                // Fetching all the points in i-th route
                for (j in path.indices) {
                    val point = path[j]

                    val lat = java.lang.Double.parseDouble(point["lat"])
                    val lng = java.lang.Double.parseDouble(point["lng"])
                    val position = LatLng(lat, lng)

                    points.add(position)
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points)
                lineOptions.width(10f)
                lineOptions.color(Color.RED)

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions)
            } else {
                Log.d("onPostExecute", "without Polylines drawn")
            }
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(bundle: Bundle?) {

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
        }

    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onLocationChanged(location: Location) {

        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }

        //Place current location marker
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        mCurrLocationMarker = mMap.addMarker(markerOptions)

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL))
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient()
                        }
                        mMap.isMyLocationEnabled = true
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }// other 'case' lines to check for other permissions this app might request.
        // You can add here other case statements according to your requirement.
    }

    companion object {
        val LOG_TAG: String = MapsActivity::class.java.name
        private const val ZOOM_LEVEL = 11f
        private const val MAPS_DATA = "MAPS_DATA_ID"
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val REQUEST_CODE_PICK_CONTACT = 1

        fun createIntent(context: Context, data: HopifyOnboardingResponse): Intent {
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra(MAPS_DATA, data)

            return intent
        }
    }

}
