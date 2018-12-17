### MariaDB script
* start a mariadb server instance
```
docker run --name some-mariadb -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mariadb:tag
```

* Container shell access and mariadb access
```
docker exec -it some-mariadb bash
mysql -u root -p my-secret-pw
```

