
```shell

docker run -d -p 9411:9411 openzipkin/zipkin
docker run -d --name tempo -p 9411:9411 -p 3200:3200 grafana/tempo:latest
docker run -d --name grafana -p 3000:3000 grafana/grafana:latest

```
