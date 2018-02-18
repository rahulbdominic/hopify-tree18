const lib = require('lib');
const uuid = require('uuid/v4');
const axios = require('axios');

const GOOGLE_API_KEY = process.env.google_api_key;
const DISTANCE_MATRIX_URL = 'https://maps.googleapis.com/maps/api/distancematrix/json';

/**
* API to return recommendations for how to spend the night.
* @param {number} lat lattitude of the user
* @param {number} lng longitude of the user
* @param {number} radius The amount of distance user is willing to travel (in meters)
* @param {integer} maxPrice This amount is the maximum price from 0-4
* @param {integer} hours The number of hours the user is willing to spend
* @param {array} interests The users interests
* @returns {array}
*/
module.exports = async (lat, lng, radius, maxPrice, hours, interests = [], context) => {
    const result = await lib.neelmehta247.hopify['@dev'].maps(lat, lng, radius, maxPrice, interests);

    const prioritized = priorities(result).slice(0, hours);
    const docName = uuid();

    // Write to Firebase
    await lib({bg: true}).neelmehta247.firebase['@dev']("data", docName.toString(), { data: prioritized });

    return prioritized;
};

const priorities = (places = []) => {
    for (i = 0; i < places.length; i++) {
        const relevancy_rating = places.length - i;
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

const distances = (places = [], hours, lat, lng) => {
  journeys = {};

  for (i = 0; i < places.length; i++) {
    for (j = i + 1; j < places.length; j++) {
      if (!journeys.hasOwnProperty(places[i].id)) {
        journeys.places[i].id = {};
      }
      if (!journeys.hasOwnProperty(places[j].id)) {
        journeys.places[j].id = {};
      }
      axios.get(DISTANCE_MATRIX_URL, {
          params: {
              origin: `${places[i].geometry.location.lat},${places[i].geometry.location.lng}`,
              destination: `${places[j].geometry.location.lat},${places[j].geometry.location.lng}`,
              key: GOOGLE_API_KEY
          }
      }).then(result => {
        data = result.data
        journeys.places[i].id.places[j].id = data.rows[0].elements[0].duration.value
        journeys.places[j].id.places[i].id = data.rows[0].elements[0].duration.value
      });

    }
  }
}
