//
//  MapsViewController.swift
//  iOS_Hopify
//
//  Created by Bart Chrzaszcz on 2018-02-17.
//  Copyright © 2018 Bart Chrzaszcz. All rights reserved.
//

import UIKit
import MapKit

class MapsViewController: UIViewController, MKMapViewDelegate {
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
        super.viewDidLoad()

        // 1.
        mapView.delegate = self

        /*let firstPoint = CLLocation(latitude: dataPoints[0].latitude, longitude: dataPoints[0].longitude)
        centerMapOnLocation(location: firstPoint)*/

        /*for dataPoint in dataPoints {
            let annotation = MKPointAnnotation()

            //let point = CLLocation(latitude: dataPoint.latitude, longitude: dataPoint.longitude)
            annotation.coordinate = CLLocationCoordinate2D(latitude: dataPoint.latitude, longitude: dataPoint.longitude)
            annotation.title = dataPoint.name

            mapView.addAnnotation(annotation)
        }*/

        for (index, dataPoint) in dataPoints.enumerated() {

            if dataPoint.latitude == dataPoints[0].latitude && dataPoint.longitude == dataPoints[0].longitude {
                continue
            }
            // 2.
            let sourceLocation = CLLocationCoordinate2D(latitude: dataPoints[index - 1].latitude, longitude: dataPoints[index - 1].longitude)
            let destinationLocation = CLLocationCoordinate2D(latitude: dataPoint.latitude, longitude: dataPoint.longitude)

            // 3.
            let sourcePlacemark = MKPlacemark(coordinate: sourceLocation, addressDictionary: nil)
            let destinationPlacemark = MKPlacemark(coordinate: destinationLocation, addressDictionary: nil)

            // 4.
            let sourceMapItem = MKMapItem(placemark: sourcePlacemark)
            let destinationMapItem = MKMapItem(placemark: destinationPlacemark)

            // 5.
            let sourceAnnotation = MKPointAnnotation()
            sourceAnnotation.title = dataPoints[index - 1].name

            if let location = sourcePlacemark.location {
                sourceAnnotation.coordinate = location.coordinate
            }


            let destinationAnnotation = MKPointAnnotation()
            destinationAnnotation.title = dataPoint.name

            if let location = destinationPlacemark.location {
                destinationAnnotation.coordinate = location.coordinate
            }

            // 6.
            self.mapView.showAnnotations([sourceAnnotation,destinationAnnotation], animated: true )

            // 7.
            let directionRequest = MKDirectionsRequest()
            directionRequest.source = sourceMapItem
            directionRequest.destination = destinationMapItem
            directionRequest.transportType = .automobile

            // Calculate the direction
            let directions = MKDirections(request: directionRequest)

            // 8.
            directions.calculate {
                (response, error) -> Void in

                guard let response = response else {
                    if let error = error {
                        print("Error: \(error)")
                    }

                    return
                }

                let route = response.routes[0]
                self.mapView.add((route.polyline), level: MKOverlayLevel.aboveRoads)

                let rect = route.polyline.boundingMapRect
                self.mapView.setRegion(MKCoordinateRegionForMapRect(rect), animated: true)
            }
        }
    }

    func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
        let renderer = MKPolylineRenderer(overlay: overlay)
        renderer.strokeColor = .red
        renderer.lineWidth = 4.0

        return renderer
    }
}
