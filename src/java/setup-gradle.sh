#!/bin/bash
#
# Bootstrap script to set up Gradle wrapper for JVM BASIC compiler
#

GRADLE_VERSION="9.2.1"
GRADLE_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"

echo "Setting up Gradle ${GRADLE_VERSION} for JVM BASIC compiler..."

# Create gradle wrapper directory
mkdir -p gradle/wrapper

# Download gradle wrapper jar and properties
if command -v curl &> /dev/null; then
    echo "Downloading gradle wrapper..."
    curl -sL "https://raw.githubusercontent.com/gradle/gradle/v${GRADLE_VERSION}/gradle/wrapper/gradle-wrapper.jar" -o gradle/wrapper/gradle-wrapper.jar
elif command -v wget &> /dev/null; then
    wget -q "https://raw.githubusercontent.com/gradle/gradle/v${GRADLE_VERSION}/gradle/wrapper/gradle-wrapper.jar" -O gradle/wrapper/gradle-wrapper.jar
else
    echo "Error: Neither curl nor wget found. Please install one of them."
    exit 1
fi

# Create gradle-wrapper.properties
cat > gradle/wrapper/gradle-wrapper.properties << EOF
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
EOF

# Create gradlew script
cat > gradlew << 'GRADLEW_SCRIPT'
#!/bin/sh
# Gradle wrapper script

# Determine the Java command to use
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Run gradle wrapper
exec "$JAVACMD" -jar gradle/wrapper/gradle-wrapper.jar "$@"
GRADLEW_SCRIPT

chmod +x gradlew

# Create gradlew.bat for Windows
cat > gradlew.bat << 'GRADLEW_BAT'
@echo off
set JAVA_EXE=java.exe
if defined JAVA_HOME set JAVA_EXE=%JAVA_HOME%\bin\java.exe
"%JAVA_EXE%" -jar gradle\wrapper\gradle-wrapper.jar %*
GRADLEW_BAT

echo "Gradle wrapper setup complete!"
echo ""
echo "To build the project:"
echo "  ./gradlew build"
echo ""
echo "To generate ANTLR parser/lexer/visitor/listener:"
echo "  ./gradlew generateGrammarSource"
echo ""
echo "To see all tasks:"
echo "  ./gradlew tasks"
