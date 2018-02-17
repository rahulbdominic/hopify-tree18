const axios = require('axios');

const GOOGLE_API_KEY = process.env.google_api_key;
const NEARBY_SEARCH_URL = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json';
const MAX_RESULTS = 25;
const ALLOWED_TYPES = [
    'accounting', 'airport', 'amusement_park', 'aquarium', 'art_gallery',
    'atm', 'bakery', 'bank', 'bar', 'beauty_salon', 'bicycle_store',
    'book_store', 'bowling_alley', 'bus_station', 'cafe', 'campground',
    'car_dealer', 'car_rental', 'car_repair', 'car_wash', 'casino',
    'cemetery', 'church', 'city_hall', 'clothing_store',
    'convenience_store', 'courthouse', 'dentist', 'department_store',
    'doctor', 'electrician', 'electronics_store', 'embassy', 'fire_station',
    'florist', 'funeral_home', 'furniture_store', 'gas_station', 'gym',
    'hair_care', 'hardware_store', 'hindu_temple', 'home_goods_store',
    'hospital', 'insurance_agency', 'jewelry_store', 'laundry', 'lawyer',
    'library', 'liquor_store', 'local_government_office', 'locksmith',
    'lodging', 'meal_delivery', 'meal_takeaway', 'mosque', 'movie_rental',
    'movie_theater', 'moving_company', 'museum', 'night_club', 'painter',
    'park', 'parking', 'pet_store', 'pharmacy', 'physiotherapist',
    'plumber', 'police', 'post_office', 'real_estate_agency', 'restaurant',
    'roofing_contractor', 'rv_park', 'school', 'shoe_store', 'shopping_mall',
    'spa', 'stadium', 'storage', 'store', 'subway_station', 'supermarket',
    'synagogue', 'taxi_stand', 'train_station', 'transit_station',
    'travel_agency', 'veterinary_care', 'zoo'
];

/**
* A function to query Google Maps and get nearby locations based on interests
* @param {number} lat lattitude to search from
* @param {number} lng longitude to search from
* @param {number} radius radius to search in (meters)
* @param {array} types the types of places to search for
* @returns {array}
*/
module.exports = async (lat, lng, radius, types = [], context) => {
    // Bunch of validation stuff
    if (radius > 50000) {
        throw new Error(`Radius value ${radius}m too large. Try less than 50000m.`);
    }

    if (types.length === 0) {
        throw new Error('Must specify at least one type');
    }

    // Move this into the loop if necessary. Optimization.
    types.forEach(type => {
        if (!ALLOWED_TYPES.includes(type)) {
            throw new Error(`${type} is not an allowed type`);
        }
    });

    // Make the request for each type
    const promiseList = types.map(type => {
        return axios.get(NEARBY_SEARCH_URL, {
            params: {
                location: `${lat},${lng}`,
                radius: radius,
                types: type,
                key: GOOGLE_API_KEY
            }
        });
        // Could be smart here and process responses as they come back. Would need to test.
    });

    let dataSet = new Set();
    let resultList = [];
    // This is not giving good results as of now because it appends all the types one after the other.
    // Change so that it puts the first of each type first in the overall list.
    await Promise.all(promiseList).then(resolved => {
        resolved.forEach(({ data }) => {
            // Data for all the types (promises), one by one
            // Essentially each call, so every different type

            const results = data.results;
            results.forEach(result => {
                // Each individual place result

                // Dedupe data
                if (!dataSet.has(result.id)) {
                    dataSet.add(result.id);

                    let count = 0;
                    const resultTypes = result.types;
                    resultTypes.forEach(resultType => {
                        // Every type for the result
                        if (types.includes(resultType)) {
                            count++;
                        }
                    });

                    // Assign each result a "count" for relevence
                    result.count = count;
                    resultList.push(result);
                }
            });
        });

        resultList.sort((item1, item2) => {
            return item2.count - item1.count;
        });
    }).catch(error => {
        throw error;
    });

    return resultList;
}
