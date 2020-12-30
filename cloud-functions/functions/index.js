// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

const db = admin.firestore();

// Listen for any change on collection `notifications`
exports.notificationListener = functions.firestore
    .document('users/{userEmail}/notifications/{notificationId}').onWrite( async (change, context) => {

        console.log("IN LISTENER FUNCTION!")

        const usersRef = db.collection('users');
        const snapshot = await usersRef.where('email', '==', context.params.userEmail).get();
        snapshot.forEach(doc => {

            console.log(doc.data().token);

            const message = {
                data: {
                    title: 'Hello!',
                    body: "You've got a new notification!"
                },
                token: doc.data().token
            };

            admin.messaging().send(message).then((response) => {
                console.log('Successfully sent message:', response);
                return true;
              })
              .catch((error) => {
                console.log('Error sending message:', error);
              });
        });
    });