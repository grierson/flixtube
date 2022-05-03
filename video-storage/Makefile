.PHONY: run test docker-build docker-run docker-stop

run:
	clojure -M -m video.main

test:
	clojure -X:test

docker-build:
	docker build . -t grierson/video

docker-run:
	 docker run -d -p 3000:3000 grierson/video

docker-stop:
	docker stop cart
