.PHONY: all cart catalog

all:
	echo "all"

cart:
	echo "start cart";
	cd cart/ && make run

catalog:
	echo "start catalog";
	cd catalog/ && make run
