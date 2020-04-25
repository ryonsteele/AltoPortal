
const https = require("https"),
  fs = require("fs");

const options = {

 cert:  fs.readFileSync('./ssl/altowebbapp_com.crt'),
 ca: fs.readFileSync('./ssl/altowebbapp_com.ca-bundle'),
 key: fs.readFileSync('./ssl/private.key')
};

const httpsServer = https.createServer(options, (req, res) => {
  res.statusCode = 200;
  res.setHeader('Content-Type', 'text/html');
  res.end('<h1>HelloWorld!</h1>');

});

httpsServer.listen(4443, '127.0.0.1');
