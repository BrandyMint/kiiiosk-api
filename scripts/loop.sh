#!/bin/bash


for ((i=1;i<=600;i++));
do
  echo "Start $i"
  curl -X POST --header "Content-Type: application/json" --header "Accept: application/json" "http://openapi.kiiiosk.ru/yandex-market/$i"
done
