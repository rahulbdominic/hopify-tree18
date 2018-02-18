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

const distances = async (places = [], hours, lat, lng) => {
  journeys = {};

  for (i = 0; i < places.length; i++) {
    for (j = i + 1; j < places.length; j++) {
      iplace_id = places[i].place_id;
      jplace_id = places[j].place_id;
      if (!journeys.hasOwnProperty(places[i].place_id)) {
        journeys[iplace_id] = {};
      }
      if (!journeys.hasOwnProperty(places[j].place_id)) {
        journeys[jplace_id] = {};
      }
      await axios.get(DISTANCE_MATRIX_URL, {
          params: {
              origins: `place_id:${iplace_id}`,
              destinations: `place_id:${jplace_id}`,
              key: GOOGLE_API_KEY
          }
      }).then(result => {
        data = result.data
        journeys[iplace_id][jplace_id] = data.rows[0].elements[0].duration.value
        journeys[jplace_id][iplace_id] = data.rows[0].elements[0].duration.value
      });
    }
  }
  return journeys;
}
