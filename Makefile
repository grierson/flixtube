.PHONY: all up down reload cart catalog video

all:
	echo "all"

up:
	docker-compose up --build

down:
	docker-compose down

reboot:
	docker-compose down && docker-compose up --build

cart:
	echo "start cart";
	cd cart/ && make run

catalog:
	echo "start catalog";
	cd catalog/ && make run

video:
	echo "start video";
	cd video/ && make run
