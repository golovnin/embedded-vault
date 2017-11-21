# Embedded Vault

Embedded Vault provides a platform neutral way for running [Vault](https://www.vaultproject.io) in tests.
This library is based on [Flapdoodle OSS's embed process](https://github.com/flapdoodle-oss/de.flapdoodle.embed.process). 

### Gradle

The artifacts are available on JCenter. Therefore you must add JCenter to
your build script repositories:
```groovy
repositories {
    jcenter()
}
```
Add a Gradle compile dependency to the `build.gradle` file of your project:
```groovy
testCompile 'com.github.golovnin:embedded-vault:0.9.0.0'
```

### Usage

Here is the example of how to launch the Vault instance:
```java
VaultServerConfig config = new VaultServerConfig.Builder()
    .build();
VaultServerStarter starter = VaultServerStarter.getDefaultInstance();
VaultServerExecutable executable = starter.prepare(config);
VaultServerProcess process = executable.start();

// Execute your tests here

process.stop();
```
Here is the example of how to launch the Vault instance using a custom version:
```java
IVersion v0_7_2 = () -> "0.7.2";
VaultServerConfig config = new VaultServerConfig.Builder()
    .version(v0_7_2)
    .build();
VaultServerStarter starter = VaultServerStarter.getDefaultInstance();
VaultServerExecutable executable = starter.prepare(config);
VaultServerProcess process = executable.start();

// Execute your tests here

process.stop();
```

### Supported Vault versions and platforms

Versions: 0.9.0 and any custom

Platforms: Mac OS X, FreeBSD, Linux, Solaris and Windows


Copyright (c) 2017, Andrej Golovnin
