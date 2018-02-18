const lib = require('lib');

/**
 * This gets data from the Hopify Firebase firestore
 * @param {string} uuid UUID provided on time of creation of resource
 * @returns {object}
 */
module.exports = async (uuid, context) => {
    const result = await lib.neelmehta247.firebase['@dev'].get('data', uuid);

    return result;
}
