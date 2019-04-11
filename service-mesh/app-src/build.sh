#!/bin/bash

cd base && ./build.sh && cd -
cd apps && ./build.sh && cd -
cd requestgen && ./build.sh && cd -
