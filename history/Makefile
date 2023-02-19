.PHONY: run test docker-build docker-run docker-stop repl

repl:
	clj -M:repl/conjure

run:
	clojure -M -m history.main

test:
	clojure -X:test

docker-build:
	docker build . -t grierson/history

docker-run:
	 docker run -d -p 3000:3000 grierson/history

docker-stop:
	docker stop history
