#!/usr/bin/env bash
forever stop ./server/generator.js
forever stop ./server/receiver.js
forever stop ./server/client.js