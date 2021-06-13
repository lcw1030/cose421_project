const express = require('express');
const Readline = require('@serialport/parser-readline');
const SerialPort = require('serialport');
const https = require('https')
const fs = require("fs")

var sensor = [0.0, 0.0, 0.0, 0.0];
var isTest = process.argv.length > 2;

const posturesData = [
// FL  FR   RL   RR    // F: Front, R: Rear, L: Left, R: Right
 [1.0, 1.0, 1.0, 1.0], // 0. Normal; 정상 자세
 [1.0, 1.0, 0.0, 0.0], // 1. Sway back; 엉덩이를 앞으로 뺀 자세. 
 [0.0, 0.0, 1.0, 1.0], // 2. Hunched back; 등을 앞으로 굽힌 자세
 [1.0, 0.0, 1.0, 1.0], // 3-1. Legs Crossed; 다리를 꼰 자세
 [0.0, 1.0, 1.0, 1.0], // 3-2. Legs Crossed; 다리를 꼰 자세
 [1.0, 0.0, 1.0, 0.0], // 4-1. Tilted body; 한쪽으로 기운 자세
 [0.0, 1.0, 0.0, 1.0], // 4-2. Tilted body; 한쪽으로 기운 자세
];

function normalize(v) {
    max = Math.max(...v);
    return v.map(x => Math.round(x / max));
}

function dist(v, w) {
    if (v.length != w.length)
        return -1;

    var sum = 0;
    for (let i = 0; i < v.length; ++i) {
        sum += (v[i] - w[i]) * (v[i] - w[i]);
    }
    return Math.sqrt(sum);
}

function evaluatePosture(v) {
    v = normalize(v);
    
    var min = 2, idx = -1;
    var d0 = dist(v, posturesData[0]);
    for (let i = 0; i < posturesData.length; ++i) {
        var d = dist(v, posturesData[i]);
        if (min > d) {
            min = d;
            idx = i;
        }
    }
    return {pos: idx, score: Math.round((1.0 - d0 / 2.0) * 100)};
}

if (!isTest) {
    // Setup serial connection
    const serialPort = new SerialPort('COM5', { baudRate: 9600 });
    const parser = serialPort.pipe(new Readline({ delimeter: '\n' }));

    parser.on('data', data => {
        sensor = JSON.parse(data);
    });
}
else {
    // Setup mock-up serial connection.
    var mockParser = setInterval(function() {
        fs.readFile('test.json', function(err, data) {
            sensor = JSON.parse(data);
        });
    }, 1000);
}

// Setup event stream
const app = express();
const port = 3000;

app.use(express.static('.'));
app.get('/serial', function(req, res) {
    res.writeHead(200, {
        'Content-Type': 'text/event-stream',
        'Cache-Control': 'no-cache',
        'Connection': 'keep-alive'
    });
    setInterval(function() {
        e = evaluatePosture(sensor);
        switch (e.pos) {
            case 0:
            case 1:
            case 2:
                break;
            
            case 3:
            case 4:
                e.pos = 3;
                break;

            case 5:
            case 6:
                e.pos = 4;
                break;
        }

        res.write('data: ' + '{ "sensor":' + JSON.stringify(sensor) + ', '
                           + '"pos":' + JSON.stringify(e.pos) + ', '
                           + '"score": ' + JSON.stringify(e.score) + '}\n\n');
    }, 1000);
});

// Run server
app.listen(port, () => {});
