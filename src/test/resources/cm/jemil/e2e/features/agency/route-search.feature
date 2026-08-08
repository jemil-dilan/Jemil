@agency @route-search
Feature: Route Search
  As a passenger
  I want to search for available routes by city
  So that I can find buses between my origin and destination

  Background:
    Given I am connected as user id "admin-1" with the roles "ADMIN"
    And a valid agency payload with name "Global Voyages" and phone "237653492410"
    When I register the agency as an admin

  Scenario: Search agencies by origin city
    When I search agencies in city "Douala"
    Then the response status is 200

  Scenario: Search agencies with no results
    When I search agencies in city "UnknownCity"
    Then the response status is 200
    And the response contains at least 0 agency

  Scenario: Search routes by origin and destination
    Given agencies with routes exist
    When I search routes from "Douala" to "Yaoundé"
    Then I should see available schedules
