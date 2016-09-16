@mavenBuild
Feature: When all is clear, all is good


    Scenario Outline: fail-because-missing-keys-java-processor
    This project has the prepare step shared by 2 profiles: local is the default profile, delivery is the second.
    Some keys are missing in the local dict so the build should fail

        Given my test project root is : src/integration-test/data/projects/fail-because-missing-keys-with-profile-java-processor
        And JAVA_HOME is set to: <java_home>
        And M2_HOME is set to: <m2_home>
        And we expect a failure with exit code: 1
        When I run maven with args: clean, package
        Then the build is not OK

        Examples:
            | java_home                     | m2_home                |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.1.x |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.2.x |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.3.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.1.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.2.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.3.x |


    Scenario Outline: fail-because-name-space-java-processor
    This project has the prepare step shared by 2 profiles: local is the default profile, delivery is the second.
    Some keys are missing in the local dict so the build should fail

        Given my test project root is : src/integration-test/data/projects/fail-because-name-space-with-profile-java-processor
        And JAVA_HOME is set to: <java_home>
        And M2_HOME is set to: <m2_home>
        And we expect a failure with exit code: 1
        When I run maven with args: clean, package
        Then the build is not OK

        Examples:
            | java_home                     | m2_home                |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.1.x |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.2.x |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.3.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.1.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.2.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.3.x |

    Scenario Outline: fail-because-missing-keys-confd-processor
    This project has the prepare step shared by 2 profiles: local is the default profile, delivery is the second.
    Some keys are missing in the local dict so the build should fail

        Given my test project root is : src/integration-test/data/projects/fail-because-missing-keys-with-profile-confd-processor
        And JAVA_HOME is set to: <java_home>
        And M2_HOME is set to: <m2_home>
        And confd for cucumber is located in: <confd_location>
        And we expect a failure with exit code: 1
        When I run maven with args: clean, package
        Then the build is not OK

        Examples:
            | java_home                     | m2_home                | confd_location                       |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.1.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.2.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.3.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.1.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.2.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.3.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.1.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.2.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.3.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.1.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.2.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.3.x | /opt/local/confd-0.12.0-alpha3/confd |

    Scenario Outline: fail-because-name-space-confd-processor
    This project has the prepare step shared by 2 profiles: local is the default profile, delivery is the second.
    Some keys are missing in the local dict so the build should fail

        Given my test project root is : src/integration-test/data/projects/fail-because-name-space-with-profile-confd-processor
        And JAVA_HOME is set to: <java_home>
        And M2_HOME is set to: <m2_home>
        And confd for cucumber is located in: <confd_location>
        And we expect a failure with exit code: 1
        When I run maven with args: clean, package
        Then the build is not OK

        Examples:
            | java_home                     | m2_home                | confd_location                       |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.1.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.2.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.3.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.1.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.2.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.3.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.1.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.2.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.3.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.1.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.2.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.3.x | /opt/local/confd-0.12.0-alpha3/confd |

