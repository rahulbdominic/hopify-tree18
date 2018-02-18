//
//  MapsViewController.swift
//  iOS_Hopify
//
//  Created by Bart Chrzaszcz on 2018-02-17.
//  Copyright © 2018 Bart Chrzaszcz. All rights reserved.
//

// Globals For The Win!!!
var __uuid__ = ""

import UIKit
import MapKit
import Branch
import PhoneNumberKit
import SDCAlertView
import Contacts

class MapsViewController: UIViewController, MKMapViewDelegate {
    @IBOutlet weak var mapView: MKMapView!

    @IBAction func shareRoute(_ sender: Any) {
        let alertController = UIAlertController(title: "Share Route", message: "", preferredStyle: .alert)

        alertController.addAction(UIAlertAction(title: "Send", style: .default, handler: {
            alert -> Void in
            let textField = alertController.textFields![0] as UITextField

            let number = textField.text!

            let buo = BranchUniversalObject(canonicalIdentifier: "")

            // index on Apple Spotlight
            buo.locallyIndex = true

            // index on Google, Branch, etc
            buo.publiclyIndex = true

            let lp: BranchLinkProperties = BranchLinkProperties()
            lp.addControlParam("uuid", withValue: __uuid__)

            buo.getShortUrl(with: lp) { (url, error) in
                print(url ?? "")
                HopifyNetwork.shared.sendDeepLinkToPhone(number: number, url: url!)
            }
        }))

        alertController.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))

        alertController.addTextField(configurationHandler: {(textField : UITextField!) -> Void in
            textField.placeholder = "Phone number"
            textField.keyboardType = .phonePad
        })

        self.present(alertController, animated: true, completion: nil)
    }



    var dataPoints: [MapObject]!

    // set initial location in San Francisco
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
            sourceAnnotation.title = "\(index). \(dataPoints[index - 1].name!)"

            if let location = sourcePlacemark.location {
                sourceAnnotation.coordinate = location.coordinate
            }


            let destinationAnnotation = MKPointAnnotation()
            destinationAnnotation.title = "\(index + 1). \(dataPoint.name!)"

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

    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        // 2
        // 3
        let identifier = "marker"
        var view: MKMarkerAnnotationView
        // 4
        if let dequeuedView = mapView.dequeueReusableAnnotationView(withIdentifier: identifier)
            as? MKMarkerAnnotationView {
            dequeuedView.annotation = annotation
            view = dequeuedView
        } else {
            // 5
            view = MKMarkerAnnotationView(annotation: annotation, reuseIdentifier: identifier)
            view.canShowCallout = true
            view.calloutOffset = CGPoint(x: -5, y: 5)
            view.rightCalloutAccessoryView = UIButton(type: .detailDisclosure)
        }
        return view
    }

    func openMapsAppWithDirections(to coordinate: CLLocationCoordinate2D, destinationName name: String) {
        let options = [MKLaunchOptionsDirectionsModeKey: MKLaunchOptionsDirectionsModeDriving]
        let placemark = MKPlacemark(coordinate: coordinate, addressDictionary: nil)
        let mapItem = MKMapItem(placemark: placemark)
        mapItem.name = name // Provide the name of the destination in the To: field
        mapItem.openInMaps(launchOptions: options)
    }

    func mapView(_ MapView: MKMapView, annotationView: MKAnnotationView, calloutAccessoryControlTapped Control: UIControl) {

        /*print("1")
        if Control == annotationView.leftCalloutAccessoryView {
            print("2")*/
            if let annotation = annotationView.annotation {
                // Unwrap the double-optional annotation.title property or
                // name the destination "Unknown" if the annotation has no title
                let destinationName = (annotation.title ?? nil) ?? "Unknown"
                openMapsAppWithDirections(to: annotation.coordinate, destinationName: destinationName)
            }
        //}

    }
}
