# ChatsUp
ChatsUp is an Android App which helps you to message to other android phones having ChatsUp Application via internet using Google Cloud Messaging.

Firstly, there is MainActivity, where we check if the user has been registered or not. If not, it ask for his/her number (only once) and registers him online.

Then comes RecentContactsActivity, where the contacts with whom conversations were made are listed out, based on the latest conversations first.

Users can select a contact with whom no chat has been made via new chat option in the menu of RecentContactsActivity, which launches ContactsActivity.

There are two receivers for the application, along with two services, a pair of each to handle the incoming messages from the GoogleCloudMessaging and to send failed messages to server when the Internet connection is established.

There is another Service, SyncContactsService, which syncs the contacts from the phone to the database of the application.

Application uses a ContentProvider(DataProvider) backed with SQLite which stores all messages, user information and pending messages.

Notification is pushed whenever a user receives a message.