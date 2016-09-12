Feature: everything works as expected when config is in 2 execution steps

    Scenario: Let's build the springboot-simple-noprofile project
        When I run `/opt/local/maven-3.1.x/bin/mvn -f /test/projects/springboot-simple-noprofile clean package`
