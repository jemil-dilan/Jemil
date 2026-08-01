@agency @registration
Feature: Agency Registration
  As an admin user
  I want to register and manage transport agencies
  So that agencies can be onboarded onto the platform

  Background:
    Given a valid agency payload with name "Global Voyages" and phone "237653492410"

  Scenario: Register a new agency successfully
    When I register the agency as an admin
    Then the response status is 201
    And the response contains an agency id

  Scenario: Retrieve an agency by its ID
    Given a valid agency payload with name "Trésor Voyages" and phone "237699999999"
    When I register the agency as an admin
    And I retrieve the agency by its id
    Then the response status is 200
    And the agency name is "Trésor Voyages"

  Scenario: List all registered agencies
    Given a valid agency payload with name "Global Voyages" and phone "237653492410"
    When I register the agency as an admin
    And I list all agencies
    Then the response status is 200
    And the response contains at least 1 agency

  Scenario: Reject registration with invalid phone number
    Given an invalid agency payload with phone "12345678901"
    When I register the agency as an admin
    Then the response status is 400
    And the response has validation errors

  Scenario: Add a route to an existing agency
    Given a valid agency payload with name "Express Lines" and phone "237612345678"
    When I register the agency as an admin
    Given I add a route from "Douala" to "Yaoundé" with price 5000.0 and 50 seats
    When I add the route to the agency
    Then the response status is 201
    And the response contains a route id
