FROM clojure

WORKDIR /app

COPY deps.edn ./
COPY src/ ./src/
COPY resources/ ./resources/
COPY Makefile ./

EXPOSE 3000

CMD ["make", "run"]
