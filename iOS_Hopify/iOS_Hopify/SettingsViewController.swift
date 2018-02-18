//
//  SettingsViewController.swift
//  iOS_Hopify
//
//  Created by Bart Chrzaszcz on 2018-02-17.
//  Copyright © 2018 Bart Chrzaszcz. All rights reserved.
//

import UIKit
import GooglePlaces
import RxSwift

class SettingsViewController: UIViewController {

    var data = Data()
    var requestObservable = PublishSubject<[MapObject]>()
    var disposeBag = DisposeBag()

    let myModalViewController = ModalViewController()

    @IBOutlet weak var price: UILabel!
    @IBOutlet weak var priceSlider: UISlider!
    @IBOutlet weak var radiusSlider: UISlider!
    @IBOutlet weak var radiusValue: UILabel!
    @IBOutlet weak var city: UILabel!
    @IBOutlet weak var date: UIDatePicker!
    var cityName: String? = "" {
        didSet {
            city.text = cityName
            data.city = cityName!

            let geoCoder = CLGeocoder()
            geoCoder.geocodeAddressString(cityName!) { [weak self] (placemarks, error) in
                guard
                    let placemarks = placemarks,
                    let location = placemarks.first?.location
                    else {
                        // handle no location found
                        return
                }
                // Use your location
                self?.data.cityLocation = location
            }
        }
    }

    override func viewDidLoad() {
        price.text = "Price: $"
        radiusValue.text = String(data.radius / 1000)
        radiusSlider.value = Float(data.radius)
        setPriceLabel()
        city.text = data.city
        date.date = data.time
    }

    // Present the Autocomplete view controller when the button is pressed.
    @IBAction func autocompleteClicked(_ sender: UIButton) {
        let autocompleteController = GMSAutocompleteViewController()
        autocompleteController.delegate = self
        present(autocompleteController, animated: true, completion: nil)
    }

    @IBAction func priceValueChanged(_ sender: Any) {
        data.price = Int(priceSlider.value)
        setPriceLabel()
    }

    @IBAction func slideValueChanged(_ sender: Any) {
        radiusValue.text = String(Int(radiusSlider.value))
        data.radius = Int(radiusSlider.value) * 1000
    }

    @IBAction func getRecommendation(_ sender: Any) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let controller = storyboard.instantiateViewController(withIdentifier: "MapsViewController") as? MapsViewController
        showModal()
        HopifyNetwork.shared.getPlacesFor(observable: requestObservable, data: data)
        requestObservable.subscribe(onNext: { [weak self] (mapList: [MapObject]) in
            for map in mapList {
                print("Latitude: \(map.latitude!)\nLongitude: \(map.longitude!)\nName: \(map.name!)\n\n")
            }
            self?.myModalViewController.dismiss(animated: true, completion: nil)
            controller?.dataPoints = mapList
            self?.present(controller!, animated: true, completion: nil)
        })
        .disposed(by: disposeBag)
        /*let when = DispatchTime.now() + 7
        DispatchQueue.main.asyncAfter(deadline: when) {
            self.present(controller!, animated: true, completion: nil)
        }*/
    }

    func setPriceLabel() {
        switch Int(priceSlider.value) {
        case 0:
            price.text = "Price: $"
        case 1:
            price.text = "Price: $$"
        case 2:
            price.text = "Price: $$$"
        case 3:
            price.text = "Price: $$$$"
        default:
            price.text = "Price: $"
        }
    }

    func showModal() {
        myModalViewController.modalPresentationStyle = .overCurrentContext
        myModalViewController.modalTransitionStyle = .coverVertical

        let blurEffect = UIBlurEffect(style: UIBlurEffectStyle.dark)
        let blurEffectView = UIVisualEffectView(effect: blurEffect)
        blurEffectView.alpha = 0.7
        blurEffectView.frame = view.bounds
        blurEffectView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        view.addSubview(blurEffectView)

        present(myModalViewController, animated: true, completion: nil)/*{
            let when = DispatchTime.now() + 6
            DispatchQueue.main.asyncAfter(deadline: when) {
                print("done")
                modalViewController.dismiss(animated: true, completion: nil)
            }
        })*/
    }

}

extension SettingsViewController: GMSAutocompleteViewControllerDelegate {

    // Handle the user's selection.
    func viewController(_ viewController: GMSAutocompleteViewController, didAutocompleteWith place: GMSPlace) {
        print("Place name: \(place.name)")
        print("Place address: \(place.formattedAddress)")
        print("Place attributions: \(place.attributions)")
        cityName = place.name
        dismiss(animated: true, completion: nil)
    }

    func viewController(_ viewController: GMSAutocompleteViewController, didFailAutocompleteWithError error: Error) {
        // TODO: handle the error.
        print("Error: ", error.localizedDescription)
    }

    // User canceled the operation.
    func wasCancelled(_ viewController: GMSAutocompleteViewController) {
        dismiss(animated: true, completion: nil)
    }

    // Turn the network activity indicator on and off again.
    func didRequestAutocompletePredictions(_ viewController: GMSAutocompleteViewController) {
        UIApplication.shared.isNetworkActivityIndicatorVisible = true
    }

    func didUpdateAutocompletePredictions(_ viewController: GMSAutocompleteViewController) {
        UIApplication.shared.isNetworkActivityIndicatorVisible = false
    }

}

