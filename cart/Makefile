.PHONY: run test docker-build docker-run docker-stop

run:
	clojure -M -m cart.main

test:
	clojure -X:test

docker-build:
	docker build . -t grierson/cart

docker-run:
	 docker run -p 3000:3000 --rm --name cart grierson/cart

docker-stop:
	docker stop cart