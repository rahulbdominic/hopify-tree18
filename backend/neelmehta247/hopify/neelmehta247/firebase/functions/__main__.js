const admin = require('firebase-admin');
const config = require('../firebase.json');
const db_name = process.env.db_name;

admin.initializeApp({
    credential: admin.credential.cert(config),
    databaseURL: db_name
});

const db = admin.firestore();

/**
* A project to set Firebase fields
* @param {string} collection Collection to write to
* @param {string} name Name of file to write to
* @param {object} data Data to write
* @returns {object}
*/
module.exports = async (collection, name, data, context) => {
    const docRef = await db.collection(collection).doc(name);
    const setData = await docRef.set(data);

    return setData;
};
