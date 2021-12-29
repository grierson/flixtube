all:
	echo "all"

cart2:
	echo "start cart";
	cd cart/ && clj -X:main :port 3000 :join? false

test:
	echo "test"