'use strict';

var kafkaesque = require('kafkaesque')({brokers: [{host: '134.221.20.239', port: 3334}],
                                               clientId: 'fish',
                                               group: 'cheese',
                                               maxBytes: 1024*1024});

// this is the poll handler, passed to .poll()
// this is called per partition that kafkaesque connects to
// when invoking .poll()
var poll = function (err, kafka) {
  console.log(err);

  // handle messaged from kafka
  kafka.on('message', function(message, commit) {
    console.log(JSON.stringify(message));

    // ensure the offset is commited so kafkaesque can provide the next message from kafka
    commit();
  });

  kafka.on('error', function(error) {
    console.log(JSON.stringify(error));
  });
};

// to fetch from the begining for all partitions
kafkaesque.poll({topic: 'test-flooding', offset: 0}, poll)