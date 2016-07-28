var stompit = require('stompit');
 
var connectOptions = {
  'host': '192.168.99.100',
  'port': 61613,
  'connectHeaders':{
    'host': '/',
    'heart-beat': '5000,5000'
  }
};
 
stompit.connect(connectOptions, function(error, client) {
  
  if (error) {
    console.log('connect error ' + error.message);
    return;
  }
  
  var sendHeaders = {
    'destination': '/topic/test',
    'content-type': 'text/plain',
	'persistent': 'true'
  };
  
  var frame = client.send(sendHeaders);
  frame.write('hello');
  frame.end();
  
  client.disconnect();
});