Feature: All prerequisites should be OK

    Scenario: Java 1.7 is present
        When I run `/usr/lib/jvm/java-7-openjdk-amd64/bin/java -version`
        Then the output should contain "1.7.0"

    Scenario: Java 1.8 is present
        When I run `/usr/lib/jvm/java-8-openjdk-amd64/bin/java -version`
        Then the output should contain "1.8.0"

    Scenario: Maven 3.1 is installed
        Given I set the environment variable "JAVA_HOME" to "/usr/lib/jvm/java-8-openjdk-amd64"
        When I run `/opt/local/maven-3.1.x/bin/mvn --version`
        Then it should pass with:
        """
        Apache Maven 3.1
        """

    Scenario: Maven 3.2 is installed
        Given I set the environment variable "JAVA_HOME" to "/usr/lib/jvm/java-8-openjdk-amd64"
        When I run `/opt/local/maven-3.2.x/bin/mvn --version`
        Then it should pass with:
        """
        Apache Maven 3.2
        """

    Scenario: Maven 3.3 is installed
        Given I set the environment variable "JAVA_HOME" to "/usr/lib/jvm/java-8-openjdk-amd64"
        When I run `/opt/local/maven-3.3.x/bin/mvn --version`
        Then it should pass with:
        """
        Apache Maven 3.3
        """
