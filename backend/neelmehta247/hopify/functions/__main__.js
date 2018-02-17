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
    let result = await lib.neelmehta247.hopify['@dev'].maps(lat, lng, radius, interests);

    return result;
};
