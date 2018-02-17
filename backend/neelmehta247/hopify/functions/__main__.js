const lib = require('lib');

/**
* API to return recommendations for how to spend the night.
* @param {number} lat lattitude of the user
* @param {number} lng longitude of the user
* @param {number} radius The amount of distance user is willing to travel (in meters)
* @param {array} interests The users interests
* @returns {array}
*/
module.exports = async (lat, lng, radius, interests = [], context) => {
    const result = await lib.neelmehta247.hopify['@dev'].maps(lat, lng, radius, interests);

    return priorities(result);
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
