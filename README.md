# Hierarchy Filter

This project implements a `filter` function for a hierarchy data structure represented as arrays of node IDs and depths.
A node appears in the filtered hierarchy if and only if it passes the predicate and all of its ancestors do as well.

## Requirements
- JDK 17+ (Gradle wrapper included)

## Running Tests
```bash
./gradlew clean test

