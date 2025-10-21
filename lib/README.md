# JVM BASIC Libraries

This directory contains external Java libraries used by the JVM BASIC runtime.

## Libraries

### Google Gson 2.10.1
**Purpose**: Proper JSON parsing and generation  
**File**: `gson-2.10.1.jar`  
**Maven**: com.google.code.gson:gson:2.10.1  
**License**: Apache 2.0  

### PostgreSQL JDBC Driver 42.7.1
**Purpose**: Database connectivity to PostgreSQL  
**File**: `postgresql-42.7.1.jar`  
**Maven**: org.postgresql:postgresql:42.7.1  
**License**: BSD-2-Clause  

### MariaDB JDBC Driver 3.3.2
**Purpose**: Database connectivity to MariaDB/MySQL  
**File**: `mariadb-java-client-3.3.2.jar`  
**Maven**: org.mariadb.jdbc:mariadb-java-client:3.3.2  
**License**: LGPL 2.1  

### Apache Commons IO 2.15.1
**Purpose**: File I/O utilities  
**File**: `commons-io-2.15.1.jar`  
**Maven**: commons-io:commons-io:2.15.1  
**License**: Apache 2.0  

### Google Guava 33.0.0
**Purpose**: Collections, utilities, caching  
**File**: `guava-33.0.0-jre.jar`  
**Maven**: com.google.guava:guava:33.0.0-jre  
**License**: Apache 2.0  

## Usage

### Compilation
```bash
javac -cp "lib/*" BasicRuntime.java
```

### Runtime
```bash
java -cp ".:lib/*:basicrt" BasicProgram
```

### Build Script
The `buildrun.sh` script automatically includes these libraries in the classpath.

## XML Support

Uses Java's built-in `javax.xml` APIs (no external library needed):
- `javax.xml.parsers.DocumentBuilder`
- `javax.xml.xpath.XPath`

## Download

All libraries downloaded from Maven Central:
- https://repo1.maven.org/maven2/

## Total Size

Approximately 2.5 MB total.

