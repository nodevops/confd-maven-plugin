Feature: Cucumber
Scenario: First Run
Given a file named "file.txt" with:
"""
   Hello World
   """
Then the file "file.txt" should contain:
"""
   Hello World
   """
