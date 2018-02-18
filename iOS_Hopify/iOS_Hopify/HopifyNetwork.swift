//
//  HopifyNetwork.swift
//  iOS_Hopify
//
//  Created by Bart Chrzaszcz on 2018-02-17.
//  Copyright © 2018 Bart Chrzaszcz. All rights reserved.
//

import RxSwift
import RxAlamofire

struct Data {
    var likes: [String] = []
    var city: String = "San Fransisco"
    var time: Date = Date()
    var price: Int = 0
    var radius: Int = 10
}

struct MapObject {
    var latitude: Double!
    var longitude: Double!
    var name: String!
}

class HopifyNetwork {

    let disposeBag = DisposeBag()
    let sourceStringURL = "https://neelmehta247.lib.id/hopify@dev/?lat=37.427475&lng=-122.1697190&radius=2000&interests=[%22cafe%22,%22bar%22,%22plumber%22]&maxPrice=4&hours=4"

    static let shared = HopifyNetwork()

    private init() { }

    func getPlacesFor(observable: PublishSubject<[MapObject]>/*data: Data*/) {

        RxAlamofire.requestJSON(.get, sourceStringURL)
            .debug()
            .subscribe(onNext: { (r, json) in
                var mapList: [MapObject] = []

                if let jsonList = json as? [AnyObject] {
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

                if mapList.count == 0 {
                    observable.onCompleted()
                } else {
                    observable.onNext(mapList)
                }
            })
            .disposed(by: disposeBag)
    }
}
