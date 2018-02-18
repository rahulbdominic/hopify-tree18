//
//  MapsViewController.swift
//  iOS_Hopify
//
//  Created by Bart Chrzaszcz on 2018-02-17.
//  Copyright © 2018 Bart Chrzaszcz. All rights reserved.
//

import UIKit
import MapKit

class MapsViewController: UIViewController {
    @IBOutlet weak var mapView: MKMapView!

    var dataPoints: [MapObject]!

    // set initial location in San Fransisco
    let initialLocation = CLLocation(latitude: 37.766007, longitude: -122.439961)
    let regionRadius: CLLocationDistance = 10000

    func centerMapOnLocation(location: CLLocation) {
        let coordinateRegion = MKCoordinateRegionMakeWithDistance(location.coordinate,
                                                                  regionRadius, regionRadius)
        mapView.setRegion(coordinateRegion, animated: true)
    }

    override func viewDidLoad() {
        let firstPoint = CLLocation(latitude: dataPoints[0].latitude, longitude: dataPoints[0].longitude)
        centerMapOnLocation(location: firstPoint)

        for dataPoint in dataPoints {
            let annotation = MKPointAnnotation()

            //let point = CLLocation(latitude: dataPoint.latitude, longitude: dataPoint.longitude)
            annotation.coordinate = CLLocationCoordinate2D(latitude: dataPoint.latitude, longitude: dataPoint.longitude)
            annotation.title = dataPoint.name

            mapView.addAnnotation(annotation)
        }

    }
}
