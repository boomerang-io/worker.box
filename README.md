# Box Worker

This is a community created service for integration with Box and used in Boomerang Flow Tasks.

## Design

- CLI application developed with Java and Spring Boot. 
- This **does not** rely on the Boomerang Worker CLI or Boomerang Worker Core. As such the Lifecycle watcher must be enabled when running as a Boomerang Flow task

### Commands

All the available commands are located in the `BoxCommand.java` file and they are:
- `list` - to list box folders
- `add` - to add a new box folder
- `join` - to add an user to a box folder
- `leave` - to remove an user from a box folder
- `remove` - to remove a box folder
- `upload` - to upload a file to a box folder
- `download` - to download a file from box
- `info` - to get the details of a box folder

## Developing

### How to run locally with Java

Build and run the JAR using the following command:
```bash
VERSION=<tag> && mvn clean package -Dversion.name=$VERSION && ENTERPRISEID=<enterpriseId> && CLIENTID=<clientId> && CLIENTSECRET=<clientSecret> && PUBLICKEYID=<publicKeyId> && PRIVATEKEY=<privateKey> && PASSPHRASE=<passphrase> && java -jar target/service-box-$VERSION.jar box list --enterpriseId $ENTERPRISEID --clientId $CLIENTID --clientSecret $CLIENTSECRET --publicKeyId $PUBLICKEYID --privateKey $PRIVATEKEY --passphrase $PASSPHRASE
```

### How to run locally with Docker

Build and run the docker image using the following command:
```bash
VERSION=<tag> && mvn clean package -Dversion.name=$VERSION && docker build -t boomerangio/box-service:$VERSION --build-arg BMRG_TAG=$VERSION . && ENTERPRISEID=<enterpriseId> && CLIENTID=<clientId> && CLIENTSECRET=<clientSecret> && PUBLICKEYID=<publicKeyId> && PRIVATEKEY=<privateKey> && PASSPHRASE=<passphrase> && docker run -it boomerangio/box-service:$VERSION box list --enterpriseId $ENTERPRISEID --clientId $CLIENTID --clientSecret $CLIENTSECRET --publicKeyId $PUBLICKEYID --privateKey $PRIVATEKEY --passphrase $PASSPHRASE
```

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
