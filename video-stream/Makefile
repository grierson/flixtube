.PHONY: run repl test docker-build docker-run docker-stop

repl:
	clj -M:repl/conjure

run:
	clojure -M -m video_stream.main

test:
	clojure -X:test

docker-build:
	docker build . -t grierson/video-stream

docker-run:
	 docker run -d -p 3000:3000 grierson/video-stream

docker-stop:
	docker stop grierson/video-stream
