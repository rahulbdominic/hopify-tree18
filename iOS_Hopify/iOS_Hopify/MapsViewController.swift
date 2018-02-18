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
import Contacts

class MapsViewController: UIViewController, MKMapViewDelegate {
    @IBOutlet weak var mapView: MKMapView!

    @IBAction func shareRoute(_ sender: Any) {
        let buo = BranchUniversalObject(canonicalIdentifier: "")

        // index on Apple Spotlight
        buo.locallyIndex = true

        // index on Google, Branch, etc
        buo.publiclyIndex = true

        let lp: BranchLinkProperties = BranchLinkProperties()
        lp.addControlParam("uuid", withValue: __uuid__)

        buo.getShortUrl(with: lp) { (url, error) in
            print(url ?? "")
            HopifyNetwork.shared.sendDeepLinkToPhone(number: "650-575-8401", url: url!)
        }
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
            sourceAnnotation.title = "\(index - 1). \(dataPoints[index - 1].name!)"

            if let location = sourcePlacemark.location {
                sourceAnnotation.coordinate = location.coordinate
            }


            let destinationAnnotation = MKPointAnnotation()
            destinationAnnotation.title = "\(index). \(dataPoint.name!)"

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

extension MapsViewController {
    func accessContacts() {
        let status = CNContactStore.authorizationStatus(for: .contacts)
        if status == .denied || status == .restricted {
            presentSettingsActionSheet()
            return
        }

        // open it

        let store = CNContactStore()
        store.requestAccess(for: .contacts) { granted, error in
            guard granted else {
                DispatchQueue.main.async {
                    self.presentSettingsActionSheet()
                }
                return
            }

            // get the contacts

            var contacts = [CNContact]()
            let request = CNContactFetchRequest(keysToFetch: [CNContactIdentifierKey as NSString, CNContactFormatter.descriptorForRequiredKeys(for: .fullName)])
            do {
                try store.enumerateContacts(with: request) { contact, stop in
                    contacts.append(contact)
                }
            } catch {
                print(error)
            }

            // do something with the contacts array (e.g. print the names)

            let formatter = CNContactFormatter()
            formatter.style = .fullName
            for contact in contacts {
                print(formatter.string(from: contact) ?? "???")
            }
        }
    }

    func presentSettingsActionSheet() {
        let alert = UIAlertController(title: "Permission to Contacts", message: "This app needs access to contacts in order to ...", preferredStyle: .actionSheet)
        alert.addAction(UIAlertAction(title: "Go to Settings", style: .default) { _ in
            let url = URL(string: UIApplicationOpenSettingsURLString)!
            UIApplication.shared.open(url)
        })
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
        present(alert, animated: true)
    }
}
