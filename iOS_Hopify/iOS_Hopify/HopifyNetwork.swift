//
//  HopifyNetwork.swift
//  iOS_Hopify
//
//  Created by Bart Chrzaszcz on 2018-02-17.
//  Copyright © 2018 Bart Chrzaszcz. All rights reserved.
//

import RxSwift
import RxAlamofire
import GooglePlaces

func initLoc() -> CLLocation {
    var loc = CLLocation()
    let geoCoder = CLGeocoder()
    geoCoder.geocodeAddressString("San Fransisco") { (placemarks, error) in
        guard
            let placemarks = placemarks,
            let location = placemarks.first?.location
            else {
                // handle no location found
                return
        }
        // Use your location
        loc = location
    }
    return loc
}

struct Data {
    var likes: [String] = []
    var city: String = "San Fransisco"
    var cityLocation: CLLocation = initLoc()
    var lat: Double = 0
    var long: Double = 0
    var time: Date = Date()
    var price: Int = 0
    var radius: Int = 1000
}

struct MapObject {
    var latitude: Double!
    var longitude: Double!
    var name: String!
}

class HopifyNetwork {

    let disposeBag = DisposeBag()
    //let sourceStringURL = "https://neelmehta247.lib.id/hopify@dev/?lat=37.427475&lng=-122.1697190&radius=2000&interests=[%22cafe%22,%22bar%22,%22plumber%22]&maxPrice=4&hours=25"
    let url = NSURLComponents(string: "https://neelmehta247.lib.id/hopify@1.0.0/")!

    static let shared = HopifyNetwork()

    private init() { }

    func getPlacesFor(observable: PublishSubject<[MapObject]>, data: Data) {

        let queryItemsList = [
            getLatQueryParam(data: data),
            getLongQueryParam(data: data),
            getRadiusQueryParam(data: data),
            getInterestsQueryParam(data: data),
            getMaxPriceQueryParam(data: data),
            getHoursQueryParam(data: data)
        ]
        url.queryItems = queryItemsList
        var newURLString = url.string!.replacingOccurrences(of: "%5B", with: "[", options: .literal, range: nil).replacingOccurrences(of: "%5D", with: "]", options: .literal, range: nil)
         //newURLString = "https://neelmehta247.lib.id/hopify@dev/?lat=37.427475&lng=-122.1697190&radius=2000&interests=[%22cafe%22,%22bar%22,%22plumber%22]&maxPrice=4&hours=4"

        RxAlamofire.requestJSON(.get, newURLString)
            .debug()
            .subscribe(onNext: { (r, json) in
                var mapList: [MapObject] = []

                if let jsonData = json as? [String: AnyObject] {
                    if let jsonList = jsonData["data"] as? [AnyObject] {
                        for item in jsonList {
                            var map = MapObject(latitude: nil, longitude: nil, name: nil)

                            if let dict = item as? [String: AnyObject] {
                                // get long/lat
                                if let location = dict["geometry"] as? [String: AnyObject] {
                                    if let lat = location["location"]?["lat"] as? Double, let long = location["location"]?["lng"] as? Double {
                                        map.latitude = lat
                                        map.longitude = long
                                    }
                                }

                                // get name
                                if let name = dict["name"] as? String {
                                    map.name = name
                                }
                            }

                            if map.latitude != nil, map.longitude != nil, map.name != nil {
                                mapList.append(map)
                            }
                        }
                    }
                }

                if mapList.count == 0 {
                    observable.onCompleted()
                } else {
                    observable.onNext(mapList)
                }
            })
            .disposed(by: disposeBag)
    }
}

// MARK - Query Params
extension HopifyNetwork {
    func getLatQueryParam(data: Data) -> URLQueryItem {
        return URLQueryItem(name: "lat", value: String(data.cityLocation.coordinate.latitude))
    }

    func getLongQueryParam(data: Data) -> URLQueryItem {
        return URLQueryItem(name: "lng", value: String(data.cityLocation.coordinate.longitude))
        // return URLQueryItem(name: "lng", value: json(from: data.cityLocation.coordinate.longitude as AnyObject))
    }

    func getRadiusQueryParam(data: Data) -> URLQueryItem {
        return URLQueryItem(name: "radius", value: String(data.radius))
        // return URLQueryItem(name: "radius", value: json(from: data.radius as AnyObject))
    }

    func getInterestsQueryParam(data: Data) -> URLQueryItem {
        return URLQueryItem(name: "interests", value: stringify(list: data.likes))
        // return URLQueryItem(name: "interests", value: json(from: data.likes as AnyObject))
    }

    func getMaxPriceQueryParam(data: Data) -> URLQueryItem {
        return URLQueryItem(name: "maxPrice", value: String(data.price))
        // return URLQueryItem(name: "maxPrice", value: json(from: String(data.price) as AnyObject))
    }

    func getHoursQueryParam(data: Data) -> URLQueryItem {
        return URLQueryItem(name: "hours", value: String(5))
        // return URLQueryItem(name: "hours", value: json(from: 5 as AnyObject))
    }

    func stringify(list: [String]) -> String {
        var returningString = "["

        for like in list {
            returningString += ("\"" + like + "\",")
        }

        let index = returningString.index(returningString.startIndex, offsetBy: returningString.count - 1)
        returningString = String(returningString.prefix(upTo: index))
        //returningString = returningString.substring(to: String.Index(returningString.count - 1))

        returningString += "]"

        return returningString
    }

    func json(from jsonObject: AnyObject) -> String {

        var jsonString: String = ""

        switch jsonObject {

        case _ as [String: AnyObject] :

            let tempObject: [String: AnyObject] = jsonObject as! [String: AnyObject]
            jsonString += "{"
            for (key , value) in tempObject {
                if jsonString.characters.count > 1 {
                    jsonString += ","
                }
                jsonString += "\"" + String(key) + "\":"
                jsonString += json(from: value)
            }
            jsonString += "}"

        case _ as [AnyObject] :

            jsonString += "["
            for i in 0..<jsonObject.count {
                if i > 0 {
                    jsonString += ","
                }
                jsonString += json(from: jsonObject[i])
            }
            jsonString += "]"

        case _ as String :

            jsonString += ("\"" + String(describing: jsonObject) + "\"")

        case _ as NSNumber :

            jsonString += "\(jsonObject)"

        case _ as Bool :

            if jsonObject.isEqual(NSNumber(value: true)) {
                jsonString += "true"
            } else if jsonObject.isEqual(NSNumber(value: false)) {
                jsonString += "false"
            } else {
                return String(describing: jsonObject)
            }

        case _ as NSNull :

            jsonString += "null"

        default :

            jsonString += ""
        }
        return jsonString
    }
}
