#!/bin/sh
DOCUMENT_ROOT=/Users/tueda/Sites/blanco
LIBRARY_PATH=/Users/tueda/blancolib

ETC_EXCLUDE_FILE=etc_exclude.lst
ROOT_EXCLUDE_FILE=root_exclude.lst

SRC_BASE=php
API_BASE=${SRC_BASE}/blanco/php
API_MAIN=blanco.php/main

API_ROOT=${API_BASE}/html
API_ETC=${API_BASE}/etc
API_COMMON=${API_BASE}/common
API_IMPLE=${API_BASE}/api

LOG4PHP_DIR=${SRC_BASE}/log4php

rsync -av --exclude-from=${ROOT_EXCLUDE_FILE} ${API_ROOT}/ ${DOCUMENT_ROOT}
rsync -av --exclude-from=${ETC_EXCLUDE_FILE} ${API_ETC} ${LIBRARY_PATH}
rsync -av ${API_COMMON} ${LIBRARY_PATH}
rsync -av ${API_IMPLE} ${LIBRARY_PATH}
rsync -av ${API_MAIN} ${LIBRARY_PATH}
rsync -av ${LOG4PHP_DIR} ${LIBRARY_PATH}

if [ ! -e ${DOCUMENT_ROOT}/env.php ]; then
    cp ${API_ROOT}/env.php ${DOCUMENT_ROOT}/
fi

if [ ! -e ${LIBRARY_PATH}/etc/ApiConfig.php ]; then
    cp ${API_ETC}/ApiConfig.php ${LIBRARY_PATH}/etc/
fi

if [ ! -e ${LIBRARY_PATH}/etc/log4php.properties ]; then
    cp ${API_ETC}/log4php.properties ${LIBRARY_PATH}/etc/
fi
