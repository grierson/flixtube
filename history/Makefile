.PHONY: run test docker-build docker-run docker-stop repl

run:
	clojure -M -m history.main

test:
	clojure -X:test

repl:
	clj -m nrepl.cmdline \
    --middleware "[cider.nrepl/cider-middleware]" \
    --interactive

docker-build:
	docker build . -t grierson/history

docker-run:
	 docker run -d -p 3000:3000 grierson/history

docker-stop:
	docker stop history
