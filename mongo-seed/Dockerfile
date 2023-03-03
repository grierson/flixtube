FROM mongo

COPY init.json /init.json
CMD mongoimport --host mongodb --db video-streaming --collection videos --type json --file /init.json
