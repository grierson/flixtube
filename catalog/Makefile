.PHONY: run test docker-build docker-run docker-stop

run:
	clojure -M -m catalog.main

test:
	clojure -X:test

docker-build:
	docker build . -t grierson/catalog

docker-run:
	 docker run -p 3000:3000 --rm --name cart grierson/catalog

docker-stop:
	docker stop cart
