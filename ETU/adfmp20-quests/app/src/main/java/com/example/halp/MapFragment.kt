package com.example.halp


import android.graphics.Color
import android.graphics.PointF
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.location.*
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.geometry.SubpolylineHelper
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.mapkit.search.Session.SearchListener
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.MasstransitRouter
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.SectionMetadata.SectionData
import com.yandex.mapkit.transport.masstransit.Session.RouteListener
import com.yandex.mapkit.transport.masstransit.Transport
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import kotlinx.android.synthetic.main.fragment_map.*
import java.util.*

class MapFragment : Fragment(), UserLocationObjectListener,
    RouteListener, DrivingSession.DrivingRouteListener {

    private val MAPKIT_API_KEY = "161a85a8-6f5e-475b-9b54-7e43cb5cde73"

    private var ROUTE_END_LOCATION: Point =
        Point(59.960000,30.320045)
    private var CURRENT_LOCATION: Point =
        Point(59.960000,30.320045)

    //private var mapView: MapView? = null
    private var mtRouter: MasstransitRouter? = null
    lateinit var mapObjects: MapObjectCollection
    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null

    private var userLocationLayer: UserLocationLayer? = null

    lateinit var mFusedLocationClient: FusedLocationProviderClient
    /*
    lateinit var searchManager: SearchManager
    private var searchSession: com.yandex.mapkit.search.Session? = null

    private fun submitQuery(query: String) {
        searchSession = searchManager!!.submit(
            query,
            VisibleRegionUtils.toPolygon(mapview.map.getVisibleRegion()),
            SearchOptions(),
            this
        )
    }
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)

    }

    override fun onStop() {
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapview.onStart()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this.context)
        TransportFactory.initialize(this.context)
        SearchFactory.initialize(this.context)
        DirectionsFactory.initialize(this.context)
        val rootView: View = inflater.inflate(R.layout.fragment_map, container, false)

        val mapView = rootView.findViewById<View>(R.id.mapview) as MapView
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        val act = activity as MainActivity
        if(act.quest != null) ROUTE_END_LOCATION = Point(act.quest!!.coords.latitude, act.quest!!.coords.longitude)
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity!!)
        getLastLocation()

        setHasOptionsMenu(false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val act = activity as MainActivity
        //ROUTE_END_LOCATION = act.quest!!.coords
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity!!)
        //getLastLocation()


        if (act.quest != null) {
            map_quest_name.setText(act.quest!!.name)
        }
        more_quest.setOnClickListener { v ->
            v.findNavController().navigate(R.id.questCardFragment)
        }

        /*
        if(mapview != null){mapview.map.move(
            CameraPosition(CURRENT_LOCATION, 14.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 5F),
            null
        )}*/
        /*
        val options = MasstransitOptions(
            ArrayList(),
            ArrayList(),
            TimeOptions()
        )
        val points: MutableList<RequestPoint> =
            ArrayList()
        points.add(RequestPoint(ROUTE_START_LOCATION, RequestPointType.WAYPOINT, null))
        points.add(RequestPoint(ROUTE_END_LOCATION , RequestPointType.WAYPOINT, null))
        mtRouter = TransportFactory.getInstance().createMasstransitRouter()
        mtRouter!!.requestRoutes(points, options, this as Session.RouteListener)
        */
        /* User Coords */
        /*
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        mapview.map.addCameraListener(this)
        search_edit.setOnEditorActionListener(OnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitQuery(search_edit.getText().toString())
            }
            false
        })
        */
    }


    override fun onMasstransitRoutes(routes: List<Route>) {
        if (routes.size > 0) {
            for (section in routes[0].sections) {
                drawSection(
                    section.metadata.data,
                    SubpolylineHelper.subpolyline(
                        routes[0].geometry, section.geometry
                    )
                )
            }
        }
    }

    override fun onMasstransitRoutesError(p0: Error) {
        var errorMessage = getString(R.string.unknown_error_message)
        if (p0 is RemoteError) {
            errorMessage = getString(R.string.remote_error_message)
        } else if (p0 is NetworkError) {
            errorMessage = getString(R.string.network_error_message)
        }
        Toast.makeText(this.context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun drawSection(
        data: SectionData,
        geometry: Polyline
    ) {
        val polylineMapObject = mapObjects.addPolyline(geometry)

        if (data.transports != null) {
            for (transport in data.transports!!) {
                if (transport.line.style != null) {
                    polylineMapObject.strokeColor = transport.line.style!!.color!! or -0x1000000
                    return
                }
            }

            val knownVehicleTypes =
                HashSet<String>()
            knownVehicleTypes.add("bus")
            knownVehicleTypes.add("tramway")
            for (transport in data.transports!!) {
                val sectionVehicleType =
                    getVehicleType(transport, knownVehicleTypes)
                if (sectionVehicleType == "bus") {
                    polylineMapObject.strokeColor = -0xff0100 // Green
                    return
                } else if (sectionVehicleType == "tramway") {
                    polylineMapObject.strokeColor = -0x10000 // Red
                    return
                }
            }
            polylineMapObject.strokeColor = -0xffff01 // Blue
        } else {
            polylineMapObject.strokeColor = -0x1000000 // Black
        }
    }

    private fun getVehicleType(
        transport: Transport,
        knownVehicleTypes: HashSet<String>
    ): String? {
        for (type in transport.line.vehicleTypes) {
            if (knownVehicleTypes.contains(type)) {
                return type
            }
        }
        return null
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationLayer!!.setAnchor(
            PointF((mapview!!.width * 0.5).toFloat(), (mapview!!.height * 0.5).toFloat()),
            PointF((mapview!!.width * 0.5).toFloat(), (mapview!!.height * 0.83).toFloat())
        )
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                this.context, R.drawable.user_arrow
            )
        )
        val pinIcon = userLocationView.pin.useCompositeIcon()
        pinIcon.setIcon(
            "icon",
            ImageProvider.fromResource(this.context, R.drawable.icon),
            IconStyle().setAnchor(PointF(0f, 0f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(1f)
        )
        pinIcon.setIcon(
            "pin",
            ImageProvider.fromResource(this.context, R.drawable.search_result),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(0.5f)
        )
        userLocationView.accuracyCircle.fillColor = Color.BLUE
    }

    override fun onObjectRemoved(p0: UserLocationView) {}

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}

    private fun getLastLocation() {

        mFusedLocationClient.lastLocation.addOnCompleteListener(this.requireActivity()) { task ->
            var location: Location? = task.result
            if (location == null) {
                requestNewLocationData()
            } else {
                CURRENT_LOCATION = Point(location.latitude,  location.longitude)
                if(mapview != null){mapview.map.move(
                    CameraPosition(ROUTE_END_LOCATION, 14.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 5F),
                    null
                )}
                /*val options = MasstransitOptions(
                    ArrayList(),
                    ArrayList(),
                    TimeOptions()
                )
                val points: MutableList<RequestPoint> =
                    ArrayList()
                points.add(RequestPoint(CURRENT_LOCATION, RequestPointType.WAYPOINT, null))
                points.add(RequestPoint(ROUTE_END_LOCATION , RequestPointType.WAYPOINT, null))
                mtRouter = TransportFactory.getInstance().createMasstransitRouter()
                mtRouter!!.requestRoutes(points, options, this as Session.RouteListener)*/
                submitRequest()
            }
        }
    }

    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )

    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            CURRENT_LOCATION = Point(mLastLocation.latitude,  mLastLocation.longitude)
            if(mapview != null){mapview.map.move(
                CameraPosition(ROUTE_END_LOCATION, 14.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 5F),
                null
            )}
            /*val options = MasstransitOptions(
                ArrayList(),
                ArrayList(),
                TimeOptions()
            )
            val points: MutableList<RequestPoint> =
                ArrayList()
            points.add(RequestPoint(CURRENT_LOCATION, RequestPointType.WAYPOINT, null))
            points.add(RequestPoint(ROUTE_END_LOCATION , RequestPointType.WAYPOINT, null))
            mtRouter = TransportFactory.getInstance().createMasstransitRouter()
            mtRouter!!.requestRoutes(points, options, this as Session.RouteListener)

             */
            submitRequest()
        }
    }

    /*
    override fun onSearchResponse(response: Response) {
        val mapObjects: MapObjectCollection = mapview.getMap().getMapObjects()
        //mapObjects.clear()
        for (searchResult in response.collection.children) {
            val resultLocation =
                searchResult.obj!!.geometry[0].point
            if (resultLocation != null) {
                mapObjects.addPlacemark(
                    resultLocation,
                    ImageProvider.fromResource(this.context, R.drawable.search_result)
                )
            }
        }
    }

    override fun onSearchError(p0: Error) {
        var errorMessage = getString(R.string.unknown_error_message)
        if (p0 is RemoteError) {
            errorMessage = getString(R.string.remote_error_message)
        } else if (p0 is NetworkError) {
            errorMessage = getString(R.string.network_error_message)
        }
        Toast.makeText(this.context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateSource,
        finished: Boolean
    ) {
        if (finished) {
            submitQuery(search_edit.getText().toString())
        }
    }
    */
    override fun onDrivingRoutes(routes: List<DrivingRoute>) {
        for (route in routes) mapObjects.addPolyline(route.geometry)
    }

    override fun onDrivingRoutesError(p0: Error) {
        var errorMessage = getString(R.string.unknown_error_message)
        if (p0 is RemoteError) {
            errorMessage = getString(R.string.remote_error_message)
        } else if (p0 is NetworkError) {
            errorMessage = getString(R.string.network_error_message)
        }
        Toast.makeText(this.context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun submitRequest() {
        val options = DrivingOptions()
        val requestPoints = ArrayList<RequestPoint>()
        requestPoints.add(
            RequestPoint(
                CURRENT_LOCATION,
                RequestPointType.WAYPOINT,
                null
            )
        )
        requestPoints.add(
            RequestPoint(
                ROUTE_END_LOCATION,
                RequestPointType.WAYPOINT,
                null
            )
        )
        drivingSession = drivingRouter!!.requestRoutes(requestPoints, options, this)
    }
}


