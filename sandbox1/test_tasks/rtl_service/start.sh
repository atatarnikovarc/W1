#!/usr/bin/env bash
forever start ./server/generator.js
forever start ./server/receiver.js
forever start ./server/client.js
