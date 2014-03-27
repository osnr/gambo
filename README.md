Gambo is a Game Boy emulator written in Java.

## Web app

Gambo supports compilation to JavaScript with Google Web Toolkit.

First, copy a base64-encoded ROM to web-app/web/src/main/webapp/game.gb.b64.

Then install Maven and run `mvn package` in web-app/.

The compiled HTML, CSS, and JS will be in web-app/web/target/web-1/. Run an HTTP server from there and visit index.html to play.
