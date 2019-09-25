# AJAX Video Repository
This application emulates a video repository which hosts collection of websites and their various videos information. It starts by providing a selection of websites curated and sent by the server. Once the user has chosen a website, the application loads a variety of encoding formats to sort by. Once a format has been selected, the app will then present a list of movies offered by that website and in the users chosen format. Changes to these selections will automatically update within the same browser window.

In more techincal terms, the uses AJAX POST requests to dynamically/asynchronously update the same webpage. All responses from the server are encoded in JSON.

NOTE: Ensure that all 3 files (home.html, home.php, and home.css) all reside in the same directory (that is currently where they are pointed).

PS: Though there is not enough entries currently, the vertical list of videos utilizes overflow-y:scroll which adds the scrolling effect.  