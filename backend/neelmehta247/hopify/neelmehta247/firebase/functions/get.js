const admin = require('firebase-admin');
const config = require('../firebase.json');
const db_name = process.env.db_name;

admin.initializeApp({
    credential: admin.credential.cert(config),
    databaseURL: db_name
});

const db = admin.firestore();

/**
 * A service to get data from Firebase firestore
 * @param {string} collection Name for the collection to fetch from
 * @param {string} name Name for the document to fetch
 * @returns {object}
 */
module.exports = async (collection, name, context) => {
    const result = await db.collection(collection).doc(name).get();

    return result.data();
}
