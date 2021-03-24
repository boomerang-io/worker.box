# Box Worker

This is a community created service for integration with Box and used in Boomerang Flow Tasks.

## Design

- Developed with Java and Spring Boot. 
- This **does not** rely on the Boomerang Worker CLI or Boomerang Worker Core. As such the Lifecycle watcher must be enabled when running as a Boomerang Flow task
- Utilizes a bash script called start.sh as the entrypoint to the container and starts the JAR

## Developing

### How to run locally

TBA

### How to test locally

TBA

## Packaging

### Automatic

1. Create Git Tag
2. Push Git Tag
3. Check Boomerang CICD

### Manual

```bash
VERSION=<tag> && mvn clean package -Dversion.name=$VERSION && docker build -t boomerangio/box-service:$VERSION --build-arg BMRG_TAG=$VERSION . && docker push boomerangio/box-service:$VERSION
```

## License

Apache 2.0
