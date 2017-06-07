# home-helper
HomeHelper: advanced schared listview app

HomeHelper is an android app and a side project of mine. 

It is an advanced lisview app that multiple users can install and use. By adding something to the list, the new items will also appear on the HomeHelper apps of other users. 

- The app stores all the new items locally in a simple text file
- When Internet is available the app stores all the new items in a mysqldatabase which is free and managed by Helioshost.org
- When the app is loaded, it checks if their are items in the database which it does not contain yet and then adds it to the listview and it's local text file. 
- The app asks for one permission, to use the local storage of your smartphone. When the permission is not granted, it still works with the internet. 
