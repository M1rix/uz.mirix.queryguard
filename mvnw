#!/bin/sh
set -e
MAVEN_VERSION="3.9.11"
MAVEN_HOME="${HOME}/.m2/wrapper/dists/apache-maven-${MAVEN_VERSION}"
MAVEN_BIN="${MAVEN_HOME}/apache-maven-${MAVEN_VERSION}/bin/mvn"
if [ ! -x "$MAVEN_BIN" ]; then
  mkdir -p "$MAVEN_HOME"
  ARCHIVE="$MAVEN_HOME/apache-maven-${MAVEN_VERSION}-bin.tar.gz"
  URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$URL" -o "$ARCHIVE"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$URL" -O "$ARCHIVE"
  else
    echo "QueryGuard mvnw requires curl or wget to bootstrap Maven." >&2
    exit 1
  fi
  tar -xzf "$ARCHIVE" -C "$MAVEN_HOME"
fi
exec "$MAVEN_BIN" "$@"
