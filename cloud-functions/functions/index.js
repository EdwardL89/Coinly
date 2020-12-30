// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

const db = admin.firestore();

// Listen for any change on collection `notifications`
exports.notificationListener = functions.firestore
    .document('users/{userEmail}/notifications/{notificationId}').onCreate( async (snapshot, context) => {
        const userDoc = await db.collection('users').where('email', '==', context.params.userEmail).get();
        const notificationDoc = snapshot.data()
        userDoc.forEach(doc => {
            const message = {
                data: {
                    title: notificationDoc.type,
                    body: "You've got a new notification!"
                },
                token: doc.data().token
            };
            admin.messaging().send(message).then((response) => {
                console.log('Successfully sent message:', response);
              })
              .catch((error) => {
                console.log('Error sending message:', error);
              });
        });
    });