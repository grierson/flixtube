.PHONY: run repl test docker-build docker-run docker-stop

repl:
	clj -M:repl/conjure

run:
	clojure -M -m video_storage.main

test:
	clojure -X:test

docker-build:
	docker build . -t grierson/video-storage

docker-run:
	 docker run -d -p 3000:3000 grierson/video-storage

docker-stop:
	docker stop grierson/video-storage
