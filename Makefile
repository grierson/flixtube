.PHONY: all test cart catalog

all:
	echo "all"

cart:
	echo "start cart";
	cd cart/ && clj -X:main :port 3000 :join? false

catalog:
	echo "start catalog";
	cd catalog/ && clj -X:main

test:
	echo "test"