//
//  SettingsViewController.swift
//  iOS_Hopify
//
//  Created by Bart Chrzaszcz on 2018-02-17.
//  Copyright © 2018 Bart Chrzaszcz. All rights reserved.
//

import UIKit
import GooglePlaces

class SettingsViewController: UIViewController {

    struct Data {
        var likes: [String] = []
        var city: String = "San Fransisco"
        var time: Date = Date()
        var price: Int = 0
        var radius: Int = 10
    }

    var data = Data()
    @IBOutlet weak var price: UILabel!
    @IBOutlet weak var priceSlider: UISlider!
    @IBOutlet weak var radiusSlider: UISlider!
    @IBOutlet weak var radiusValue: UILabel!
    @IBOutlet weak var city: UILabel!
    @IBOutlet weak var date: UIDatePicker!
    var cityTuple: (name: String?, address: String?, attributions: String?) = ("","","") {
        didSet {
            city.text = cityTuple.name
        }
    }

    override func viewDidLoad() {
        price.text = "Price: $"
        radiusValue.text = String(data.radius)
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
        setPriceLabel()
    }

    @IBAction func slideValueChanged(_ sender: Any) {
        radiusValue.text = String(Int(radiusSlider.value))
    }

    @IBAction func getRecommendation(_ sender: Any) {
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

}

extension SettingsViewController: GMSAutocompleteViewControllerDelegate {

    // Handle the user's selection.
    func viewController(_ viewController: GMSAutocompleteViewController, didAutocompleteWith place: GMSPlace) {
        print("Place name: \(place.name)")
        print("Place address: \(place.formattedAddress)")
        print("Place attributions: \(place.attributions)")
        cityTuple = (place.name, place.formattedAddress, place.attributions?.string)
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

