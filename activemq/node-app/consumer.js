var stompit = require('stompit');
 
stompit.connect({ 'client-id': 'SampleNodeActiveMQClient', host: '192.168.99.100', port: 61613 }, (err, client) => {
    client.subscribe({ 'activemq.subscriptionName': 'SampleSubscription', destination: '/topic/test' }, (err, msg) => {
        msg.readString('UTF-8', (err, body) => {
            console.log(body);
 
            //client.disconnect();
          });
      });
  });