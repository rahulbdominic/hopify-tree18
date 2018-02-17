/**
* A basic Hello World function
* @param {number} lat lattitude of the user
* @param {number} lng longitude of the user
* @param {number} radius The amount of distance user is willing to travel (in meters)
* @param {array} interests The users interests
* @returns {object}
*/
module.exports = async (lat, lng, radius, interests = [], context) => {
    if (radius >= 50000) {
        throw new Error(`${radius} meters is too far away`);
    } else {
        return {
            value: radius
        };
    }
};
