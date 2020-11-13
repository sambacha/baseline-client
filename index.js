const execSync = require('child_process').execSync;
const path = require('path');
const os = require('os');
const http = require('http');
const url = require('url');
const fs = require('fs');

const configPath = path.join(process.cwd(), '.baseline.json');
const baselinePath = process.argv.slice(2)[0];
const port = parseInt(process.argv.slice(2)[1]);
var config = fs.existsSync(configPath) && readConfig();


function baseline() {
  const baselineDirPath = baselinePath.replace(/[\/\\]?baseline\.js?$/, '');
  return require(path.join(baselineDirPath, 'lib', 'index'));
}

function watchConfig() {
  fs.exists(configPath, function (exists) {
    exists && fs.watchFile(configPath, readConfig);
  });
}

function readConfig() {
  const jsonValue = fs.readFileSync(configPath, 'utf-8');
  return config = JSON.parse(jsonValue);
}

function requestHandler(request, response) {
  const filePath = url.parse(request.url, true).query.filePath;

  const errors = baseline().processFile(filePath, config);
  response.end(JSON.stringify(errors.messages));
}

function init() {
  watchConfig();

  http
    .createServer(requestHandler)
    .listen(port, function (err) {
      if (err) {
        return console.log('Error happened', err);
      }

      console.log('Server is listening on ' + port);
    });
}

init();