.PHONY: all cart catalog

all:
	echo "all"

cart:
	echo "start cart";
	cd cart/ && clj -X:main :port 3000 :join? false

catalog:
	echo "start catalog";
	cd catalog/ && clj -X:main