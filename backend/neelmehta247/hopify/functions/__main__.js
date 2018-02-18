const lib = require('lib');
const uuid = require('uuid/v4');
const axios = require('axios');

const GOOGLE_API_KEY = process.env.google_distance_matrix_key;
const DISTANCE_MATRIX_URL = 'https://maps.googleapis.com/maps/api/distancematrix/json';

/**
* API to return recommendations for how to spend the night.
* @param {number} lat lattitude of the user
* @param {number} lng longitude of the user
* @param {number} radius The amount of distance user is willing to travel (in meters)
* @param {integer} maxPrice This amount is the maximum price from 0-4
* @param {integer} hours The number of hours the user is willing to spend
* @param {array} interests The users interests
* @returns {object}
*/
module.exports = async (lat, lng, radius, maxPrice, hours, interests = [], context) => {
    const result = await lib.neelmehta247.hopify['@1.1.3'].maps(lat, lng, radius, maxPrice, interests);

    const prioritized = priorities(result).slice(0, hours);
    if (prioritized.length === 0) {
        return {
            "data": []
        }
    }

    const vertices = placeids(prioritized);
    const edges = pairs(vertices.length);
    const distancesData = await distances(prioritized, lat, lng);
    const edgeMap = distancesData.data;
    const minMaxData = distancesData.minMaxData;
    edgeMap[minMaxData.minDistPlaceID][minMaxData.maxDistPlaceID] = 0;
    edgeMap[minMaxData.maxDistPlaceID][minMaxData.minDistPlaceID] = 0;

    const tps = await lib.neelmehta247.hopify['@1.1.3'].tsp(vertices, edges, edgeMap);
    const offset = tps.indexOf(minMaxData.minDistIndex.toString());

    const finalArray = [];
    for (i = 0; i < tps.length; i++) {
        const toPut = tps[(i + offset) % tps.length];
        finalArray.push(prioritized[parseInt(toPut)]);
    }
    const docName = uuid();

    // Write to Firebase
    await lib({bg: true}).neelmehta247.firebase['@1.0.0']("data", docName.toString(), { data: finalArray });

    return {
        uuid: docName,
        data: finalArray
    };
};

const priorities = (places) => {
    places = places.filter(item => !!item);
    for (i = 0; i < places.length; i++) {
        const relevancy_rating = places.length - i;
        console.log("places[i] at " + i + ": " + places[i]);
        const count_score = places[i].count * 3;
        const relevancy_score = (relevancy_rating * 9.5) / places.length;

        if ("rating" in places[i]) {
            const google_rating = places[i].rating * 2;
            places[i].final_score = count_score + relevancy_score + google_rating;
        } else {
            places.pop(places[i]);
        }
    }

    places.sort((a, b) => b.final_score-a.final_score);

    return places;
}

const placeids = (places) => {
    placeidlist = [];
    for (i = 0; i < places.length; i++) {
        placeidlist.push(places[i].place_id);
    }

    return placeidlist;
}

const pairs = (length) => {
    mainlist = [];
    for (i = 0; i < length; i++) {
        for (j = 0; j < length; j++) {
            if (i !== j) {
                mainlist.push([i, j]);
            }
        }
    }

    return mainlist;
}

const distances = async (places, lat, lng) => {
    journeys = {};

    const minMaxData = {};

    for (i = 0; i < places.length; i++) {
        const currentPlaceId = places[i].place_id;
        const latLngComp = await axios.get(DISTANCE_MATRIX_URL, {
            params: {
                origins: `place_id:${currentPlaceId}`,
                destinations: `${lat},${lng}`,
                key: GOOGLE_API_KEY
            }
        });

        const distFromHome = latLngComp.data.rows[0].elements[0].duration.value

        // Set min
        if (minMaxData.hasOwnProperty("minDist")) {
            if (distFromHome <= minMaxData.minDist) {
                minMaxData.minDist = distFromHome;
                minMaxData.minDistPlaceID = currentPlaceId;
                minMaxData.minDistIndex = i;
            }
        } else {
            // Doesn't have any data, so set it.
            minMaxData.minDist = distFromHome;
            minMaxData.minDistPlaceID = currentPlaceId;
            minMaxData.minDistIndex = i;
        }

        // Set max
        if (minMaxData.hasOwnProperty("maxDist")) {
            if (distFromHome > minMaxData.maxDist) {
                minMaxData.maxDist = distFromHome;
                minMaxData.maxDistPlaceID = currentPlaceId;
            }
        } else {
            // Doesn't have max data, so set it
            minMaxData.maxDist = distFromHome;
            minMaxData.maxDistPlaceID = currentPlaceId;
        }

        for (j = i + 1; j < places.length; j++) {
            iplace_id = places[i].place_id;
            jplace_id = places[j].place_id;

            if (!journeys.hasOwnProperty(places[i].place_id)) {
                journeys[iplace_id] = {};
            }

            if (!journeys.hasOwnProperty(places[j].place_id)) {
                journeys[jplace_id] = {};
            }

            const result = await axios.get(DISTANCE_MATRIX_URL, {
                params: {
                    origins: `place_id:${iplace_id}`,
                    destinations: `place_id:${jplace_id}`,
                    key: GOOGLE_API_KEY
                }
            });

            data = result.data;
            // To add hours
            journeys[iplace_id][jplace_id] = data.rows[0].elements[0].duration.value + 3600;
            journeys[jplace_id][iplace_id] = data.rows[0].elements[0].duration.value + 3600;
        }
    }

    return {
        minMaxData, data: journeys
    };
}
